# backend/apps/accounts/views.py
from django.utils import timezone
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from django.conf import settings
from firebase_admin import auth as firebase_auth
from rest_framework.decorators import api_view, authentication_classes, permission_classes
from rest_framework.permissions import IsAuthenticated, AllowAny
from rest_framework.response import Response

from apps.accounts.services.firebase_backend import FirebaseAuthentication
from .models import CustomUser
import logging
from functools import wraps

logger = logging.getLogger(__name__)

def firebase_auth_required(view_func):
    """Decorator to verify Firebase authentication token"""
    @wraps(view_func)
    def wrapped_view(request, *args, **kwargs):
        auth_header = request.headers.get('Authorization')
        if not auth_header or not auth_header.startswith('Bearer '):
            logger.error("Missing or invalid Authorization header")
            return JsonResponse({'error': 'Unauthorized - Invalid or missing token'}, status=401)
        
        try:
            token = auth_header.split('Bearer ')[1]
            decoded_token = firebase_auth.verify_id_token(token)
            # Add the decoded token to the request object for use in views
            request.firebase_user = decoded_token
            return view_func(request, *args, **kwargs)
        except firebase_auth.InvalidIdTokenError:
            logger.error("Invalid Firebase token provided")
            return JsonResponse({'error': 'Invalid token'}, status=401)
        except firebase_auth.ExpiredIdTokenError:
            logger.error("Expired Firebase token provided")
            return JsonResponse({'error': 'Token expired'}, status=401)
        except Exception as e:
            logger.error(f"Firebase authentication error: {str(e)}")
            return JsonResponse({'error': 'Authentication failed'}, status=401)
    
    return wrapped_view

@api_view(['GET'])
@firebase_auth_required
def profile(request):
    """Get user profile using Firebase authentication"""
    try:
        firebase_user = request.firebase_user
        user_data = {
            'uid': firebase_user['uid'],
            'email': firebase_user.get('email'),
            'email_verified': firebase_user.get('email_verified', False),
        }
        
        try:
            custom_user = CustomUser.objects.get(firebase_uid=firebase_user['uid'])
            user_data.update({
                'first_name': custom_user.first_name,
                'last_name': custom_user.last_name,
            })
        except CustomUser.DoesNotExist:
            logger.warning(f"CustomUser not found for Firebase UID: {firebase_user['uid']}")
        
        return JsonResponse({'profile': user_data})
    except Exception as e:
        logger.error(f"Profile retrieval failed: {str(e)}")
        return JsonResponse({'error': 'Failed to retrieve profile'}, status=500)

@csrf_exempt
@api_view(['POST'])
@authentication_classes([FirebaseAuthentication])
@permission_classes([IsAuthenticated])
def sync_user(request):
    """Synchronize user data with Firebase"""
    logger.info("Processing sync_user request")
    
    if not hasattr(request, '_firebase_user') or not request._firebase_user:
        logger.error("No Firebase user found")
        return JsonResponse({'error': 'Unauthorized'}, status=401)
    
    try:
        user = request._firebase_user
        sync_data = {
            'user_id': user.id,
            'email': user.email,
            'firebase_uid': user.firebase_uid,
            'last_sync': timezone.now().isoformat()
        }
        
        return JsonResponse({
            'message': 'User synchronized successfully',
            'data': sync_data
        }, status=200)
    except Exception as e:
        logger.error(f"Sync failed: {str(e)}")
        return JsonResponse({'error': str(e)}, status=500)

@api_view(['POST'])
@permission_classes([AllowAny])
def create_superuser(request):
    """Create a superuser via API endpoint"""
    # Verify secret key to ensure this is a legitimate request
    if request.data.get('secret_key') != settings.SECRET_KEY:
        logger.error("Unauthorized superuser creation attempt")
        return Response({'error': 'Unauthorized'}, status=401)
    
    try:
        email = request.data.get('email')
        password = request.data.get('password')
        first_name = request.data.get('first_name', '')
        last_name = request.data.get('last_name', '')
        
        if not email or not password:
            return Response({'error': 'Email and password are required'}, status=400)
            
        # Check if user exists
        if CustomUser.objects.filter(email=email).exists():
            logger.info(f"Superuser with email {email} already exists")
            return Response({'message': 'User already exists'}, status=200)
            
        # Create superuser
        superuser = CustomUser.objects.create_superuser(
            email=email,
            password=password,
            first_name=first_name,
            last_name=last_name,
            is_active=True
        )
        logger.info(f"Superuser created successfully: {email}")
        return Response({
            'message': 'Superuser created successfully',
            'email': superuser.email,
            'id': superuser.id
        }, status=201)
        
    except Exception as e:
        logger.error(f"Superuser creation failed: {str(e)}")
        return Response({'error': str(e)}, status=400)

@api_view(['GET'])
@authentication_classes([FirebaseAuthentication])
@permission_classes([IsAuthenticated])
def test_auth(request):
    """Test authentication endpoint"""
    return JsonResponse({
        'authenticated': request.user.is_authenticated,
        'user_id': getattr(request.user, 'id', None),
        'email': getattr(request.user, 'email', None),
        'firebase_uid': getattr(request._firebase_user, 'firebase_uid', None) if hasattr(request, '_firebase_user') else None,
    })