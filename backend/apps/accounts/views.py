# backend/apps/accounts/views.py
from django.utils import timezone
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from django.conf import settings
from firebase_admin import auth as firebase_auth
import jwt
from rest_framework.decorators import api_view, authentication_classes, permission_classes
from rest_framework.permissions import IsAuthenticated, AllowAny
from rest_framework.response import Response
from django.db import transaction

from apps.accounts.services.firebase_backend import FirebaseAuthentication
from core.utils.email_service import EmailService
from .models import CustomUser
import logging
from functools import wraps

logger = logging.getLogger(__name__)

@api_view(['POST'])
@permission_classes([AllowAny])
@csrf_exempt
def register_user(request):
    """
    Register a new user with Firebase and sync with database.
    Expected payload:
    {
        "email": "user@example.com",
        "password": "securepassword",
        "first_name": "John",
        "last_name": "Doe"
    }
    """
    try:
        email = request.data.get('email')
        password = request.data.get('password')
        first_name = request.data.get('first_name', '')
        last_name = request.data.get('last_name', '')

        if not email or not password:
            return Response({
                'error': 'Email and password are required'
            }, status=400)

        # Create the user in Firebase
        try:
            firebase_user = firebase_auth.create_user(
                email=email,
                password=password,
                email_verified=False
            )

            firebase_auth.generate_email_verification_link(email)
        except firebase_auth.EmailAlreadyExistsError:
            return Response({
                'error': 'Email already exists'
            }, status=400)
        except Exception as e:
            logger.error(f"Firebase user creation failed: {str(e)}")
            return Response({
                'error': 'Failed to create Firebase user'
            }, status=500)

        # Create the user in your database
        try:
            with transaction.atomic():
                custom_user = CustomUser.objects.create(
                    email=email,
                    firebase_uid=firebase_user.uid,
                    first_name=first_name,
                    last_name=last_name,
                    is_active=True
                )
        except Exception as e:
            # If database creation fails, delete the Firebase user
            try:
                firebase_auth.delete_user(firebase_user.uid)
            except Exception as delete_error:
                logger.error(f"Failed to delete Firebase user after DB error: {str(delete_error)}")
            
            logger.error(f"Database user creation failed: {str(e)}")
            return Response({
                'error': 'Failed to create user in database'
            }, status=500)

        # Generate a custom token for initial sign-in (optional)
        try:
            custom_token = firebase_auth.create_custom_token(firebase_user.uid)
        except Exception as e:
            logger.error(f"Custom token creation failed: {str(e)}")
            custom_token = None

        return Response({
            'message': 'User registered successfully',
            'user': {
                'id': custom_user.id,
                'email': custom_user.email,
                'firebase_uid': firebase_user.uid,
                'first_name': custom_user.first_name,
                'last_name': custom_user.last_name
            },
            'custom_token': custom_token.decode() if custom_token else None
        }, status=201)

    except Exception as e:
        logger.error(f"Registration failed: {str(e)}")
        return Response({
            'error': 'Registration failed'
        }, status=500)

@api_view(['POST'])
@permission_classes([AllowAny])
@csrf_exempt
def login(request):
    """
    Login endpoint that creates/verifies Firebase user and returns user data with tokens
    Expected payload:
    {
        "email": "user@example.com",
        "password": "userpassword"
    }
    """
    try:
        email = request.data.get('email')
        password = request.data.get('password')

        if not email or not password:
            return Response({
                'error': 'Email and password are required'
            }, status=400)

        try:
            # Get the user from Firebase
            firebase_user = firebase_auth.get_user_by_email(email)
            firebase_uid = firebase_user.uid
            
            try:
                custom_user = CustomUser.objects.get(firebase_uid=firebase_uid)
            except CustomUser.DoesNotExist:
                custom_user = CustomUser.objects.create(
                    email=firebase_user.email,
                    firebase_uid=firebase_uid,
                    is_active=True
                )

            # Generate access token (custom token)
            access_token = firebase_auth.create_custom_token(firebase_uid, {
                'token_type': 'access'  # We can add custom claims, just not 'exp'
            })

            # Generate refresh token (another custom token)
            refresh_token = firebase_auth.create_custom_token(firebase_uid, {
                'token_type': 'refresh'
            })

            return Response({
                'message': 'Login successful',
                'user': {
                    'id': custom_user.id,
                    'email': custom_user.email,
                    'firebase_uid': custom_user.firebase_uid,
                    'first_name': custom_user.first_name,
                    'last_name': custom_user.last_name,
                    'is_active': custom_user.is_active,
                },
                'tokens': {
                    'access_token': access_token.decode(),
                    'refresh_token': refresh_token.decode(),
                    'expires_in': 3600,  # 1 hour in seconds
                }
            }, status=200)

        except firebase_auth.UserNotFoundError:
            return Response({
                'error': 'User not found'
            }, status=404)
        except Exception as e:
            logger.error(f"Firebase authentication failed: {str(e)}")
            return Response({
                'error': 'Authentication failed'
            }, status=401)

    except Exception as e:
        logger.error(f"Login failed: {str(e)}")
        return Response({
            'error': 'Login failed'
        }, status=500)

@api_view(['POST'])
@permission_classes([AllowAny])
@csrf_exempt
def refresh_access_token(request):
    """
    Refresh token endpoint to get a new access token
    Expected payload: {
        "access_token": "current_access_token",
        "refresh_token": "current_refresh_token"
    }
    """
    try:
        access_token = request.data.get('access_token')
        refresh_token = request.data.get('refresh_token')
        
        if not access_token or not refresh_token:
            return Response({
                'error': 'Both access token and refresh token are required'
            }, status=400)

        try:
            # Extract the UID from the JWT without verification
            # This is safe because we're only using it to create new tokens
            import jwt
            decoded_refresh = jwt.decode(refresh_token, options={"verify_signature": False})
            firebase_uid = decoded_refresh.get('uid')
            
            if not firebase_uid:
                return Response({
                    'error': 'Invalid refresh token format'
                }, status=401)

            # Generate new tokens
            new_access_token = firebase_auth.create_custom_token(
                firebase_uid,
                {"token_type": "access"}
            )
            new_refresh_token = firebase_auth.create_custom_token(
                firebase_uid,
                {"token_type": "refresh"}
            )

            try:
                # Get user info
                custom_user = CustomUser.objects.get(firebase_uid=firebase_uid)
                user_data = {
                    'id': custom_user.id,
                    'email': custom_user.email,
                    'firebase_uid': custom_user.firebase_uid,
                    'first_name': custom_user.first_name,
                    'last_name': custom_user.last_name,
                }
            except CustomUser.DoesNotExist:
                user_data = None

            return Response({
                'message': 'Tokens refreshed successfully',
                'user': user_data,
                'tokens': {
                    'access_token': new_access_token.decode(),
                    'refresh_token': new_refresh_token.decode(),
                    'expires_in': 3600,  # 1 hour
                }
            }, status=200)

        except jwt.InvalidTokenError:
            return Response({
                'error': 'Invalid token format'
            }, status=401)
        except Exception as e:
            logger.error(f"Token refresh failed: {str(e)}")
            return Response({
                'error': 'Invalid tokens'
            }, status=401)

    except Exception as e:
        logger.error(f"Token refresh failed: {str(e)}")
        return Response({
            'error': 'Token refresh failed'
        }, status=500)
    
@api_view(['GET'])
@permission_classes([AllowAny])
def get_profile(request):
    """Get user profile"""
    try:
        auth_header = request.headers.get('Authorization')
        if not auth_header or not auth_header.startswith('Bearer '):
            logger.error("Missing or invalid Authorization header")
            return Response({
                'error': 'Invalid or missing token'
            }, status=401)
        
        token = auth_header.split('Bearer ')[1]
        
        # Decode token to get UID
        import jwt
        try:
            decoded_token = jwt.decode(token, options={"verify_signature": False})
            firebase_uid = decoded_token.get('uid')
            
            if not firebase_uid:
                return Response({
                    'error': 'Invalid token format'
                }, status=401)

            # Get user info
            try:
                custom_user = CustomUser.objects.get(firebase_uid=firebase_uid)
                
                profile_data = {
                    'id': custom_user.id,
                    'email': custom_user.email,
                    'firebase_uid': custom_user.firebase_uid,
                    'first_name': custom_user.first_name,
                    'last_name': custom_user.last_name,
                    'is_active': custom_user.is_active
                }
                
                return Response({
                    'status': 'success',
                    'message': 'Profile retrieved successfully',
                    'data': profile_data
                }, status=200)
                
            except CustomUser.DoesNotExist:
                logger.warning(f"CustomUser not found for Firebase UID: {firebase_uid}")
                return Response({
                    'status': 'error',
                    'message': 'Profile not found'
                }, status=404)
            
        except jwt.InvalidTokenError as e:
            logger.error(f"JWT decode error: {str(e)}")
            return Response({
                'error': 'Invalid token format'
            }, status=401)
            
    except Exception as e:
        logger.error(f"Profile retrieval failed: {str(e)}")
        return Response({
            'status': 'error',
            'message': 'Failed to retrieve profile',
            'error': str(e)
        }, status=500)
    
@api_view(['PUT'])
@permission_classes([AllowAny])
def update_profile(request):
    """Update user profile details"""
    try:
        # Get user from token
        auth_header = request.headers.get('Authorization')
        if not auth_header or not auth_header.startswith('Bearer '):
            return Response({'error': 'Invalid or missing token'}, status=401)
        
        token = auth_header.split('Bearer ')[1]
        decoded_token = jwt.decode(token, options={"verify_signature": False})
        firebase_uid = decoded_token.get('uid')
        
        if not firebase_uid:
            return Response({'error': 'Invalid token'}, status=401)

        try:
            user = CustomUser.objects.get(firebase_uid=firebase_uid)
            
            # Update user fields
            if 'first_name' in request.data:
                user.first_name = request.data['first_name']
            if 'last_name' in request.data:
                user.last_name = request.data['last_name']
            if 'phone_number' in request.data:
                user.phone_number = request.data['phone_number']
            # Add any other fields you want to allow updating
            
            user.save()
            
            return Response({
                'message': 'Profile updated successfully',
                'data': {
                    'id': user.id,
                    'email': user.email,
                    'firebase_uid': user.firebase_uid,
                    'first_name': user.first_name,
                    'last_name': user.last_name,
                    'is_active': user.is_active
                }
            }, status=200)
            
        except CustomUser.DoesNotExist:
            return Response({
                'error': 'User not found'
            }, status=404)
            
    except Exception as e:
        logger.error(f"Profile update failed: {str(e)}")
        return Response({
            'error': 'Failed to update profile'
        }, status=500)

@api_view(['POST'])
@permission_classes([AllowAny])
def change_password(request):
    """Change user password"""
    try:
        auth_header = request.headers.get('Authorization')
        if not auth_header or not auth_header.startswith('Bearer '):
            return Response({'error': 'Invalid or missing token'}, status=401)
        
        token = auth_header.split('Bearer ')[1]
        decoded_token = jwt.decode(token, options={"verify_signature": False})
        firebase_uid = decoded_token.get('uid')
        
        if not firebase_uid:
            return Response({'error': 'Invalid token'}, status=401)
            
        old_password = request.data.get('old_password')
        new_password = request.data.get('new_password')
        
        if not old_password or not new_password:
            return Response({
                'error': 'Both old and new passwords are required'
            }, status=400)
            
        try:
            # Update password in Firebase
            firebase_auth.update_user(
                firebase_uid,
                password=new_password
            )
            
            return Response({
                'message': 'Password updated successfully'
            }, status=200)
            
        except Exception as e:
            logger.error(f"Password change failed: {str(e)}")
            return Response({
                'error': 'Failed to change password'
            }, status=400)
            
    except Exception as e:
        logger.error(f"Password change failed: {str(e)}")
        return Response({
            'error': 'Failed to change password'
        }, status=500)

@api_view(['POST'])
@permission_classes([AllowAny])
def forgot_password(request):
    """Send password reset email using SendGrid"""
    try:
        email = request.data.get('email')
        if not email:
            return Response({
                'error': 'Email is required'
            }, status=400)
            
        try:
            # Check if user exists
            try:
                firebase_user = firebase_auth.get_user_by_email(email)
            except firebase_auth.UserNotFoundError:
                return Response({
                    'error': 'No user found with this email'
                }, status=404)
            
            # Generate reset link
            try:
                reset_link = firebase_auth.generate_password_reset_link(email)
                
                # Send email using SendGrid
                email_service = EmailService()
                email_sent = email_service.send_password_reset(email, reset_link)
                
                if email_sent:
                    return Response({
                        'message': 'Password reset email sent successfully'
                    }, status=200)
                else:
                    return Response({
                        'error': 'Failed to send email'
                    }, status=500)
                
            except Exception as e:
                logger.error(f"Error generating reset link: {str(e)}")
                return Response({
                    'error': 'Failed to generate reset link'
                }, status=500)
            
        except Exception as e:
            logger.error(f"Password reset failed: {str(e)}")
            return Response({
                'error': str(e)
            }, status=500)
            
    except Exception as e:
        logger.error(f"Password reset failed: {str(e)}")
        return Response({
            'error': 'Failed to send password reset email'
        }, status=500)

@api_view(['POST'])
@permission_classes([AllowAny])
def verify_email(request):
    """Send verification email using SendGrid"""
    try:
        auth_header = request.headers.get('Authorization')
        if not auth_header or not auth_header.startswith('Bearer '):
            return Response({'error': 'Invalid or missing token'}, status=401)
        
        token = auth_header.split('Bearer ')[1]
        decoded_token = jwt.decode(token, options={"verify_signature": False})
        firebase_uid = decoded_token.get('uid')
        
        if not firebase_uid:
            return Response({'error': 'Invalid token'}, status=401)
            
        try:
            user = firebase_auth.get_user(firebase_uid)
            verification_link = firebase_auth.generate_email_verification_link(user.email)
            
            # Send email using SendGrid
            email_service = EmailService()
            email_sent = email_service.send_verification_email(user.email, verification_link)
            
            if email_sent:
                return Response({
                    'message': 'Verification email sent successfully'
                }, status=200)
            else:
                return Response({
                    'error': 'Failed to send email'
                }, status=500)
            
        except firebase_auth.UserNotFoundError:
            return Response({
                'error': 'User not found'
            }, status=404)
            
    except Exception as e:
        logger.error(f"Email verification failed: {str(e)}")
        return Response({
            'error': 'Failed to send verification email'
        }, status=500)

@api_view(['DELETE'])
@permission_classes([AllowAny])
def delete_account(request):
    """Delete user account"""
    try:
        auth_header = request.headers.get('Authorization')
        if not auth_header or not auth_header.startswith('Bearer '):
            return Response({'error': 'Invalid or missing token'}, status=401)
        
        token = auth_header.split('Bearer ')[1]
        decoded_token = jwt.decode(token, options={"verify_signature": False})
        firebase_uid = decoded_token.get('uid')
        
        if not firebase_uid:
            return Response({'error': 'Invalid token'}, status=401)
            
        try:
            # Delete from your database
            user = CustomUser.objects.get(firebase_uid=firebase_uid)
            user.delete()
            
            # Delete from Firebase
            firebase_auth.delete_user(firebase_uid)
            
            return Response({
                'message': 'Account deleted successfully'
            }, status=200)
            
        except CustomUser.DoesNotExist:
            return Response({
                'error': 'User not found'
            }, status=404)
            
    except Exception as e:
        logger.error(f"Account deletion failed: {str(e)}")
        return Response({
            'error': 'Failed to delete account'
        }, status=500)
