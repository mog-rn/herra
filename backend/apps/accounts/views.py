from django.utils import timezone
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from firebase_admin import auth as firebase_auth
from rest_framework.decorators import api_view, authentication_classes, permission_classes
from rest_framework.permissions import IsAuthenticated

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
            # Add any additional fields you want to return
        }
        
        # If you're using CustomUser model, you can fetch additional data
        try:
            custom_user = CustomUser.objects.get(firebase_uid=firebase_user['uid'])
            user_data.update({
                'first_name': custom_user.first_name,
                'last_name': custom_user.last_name,
                # Add other custom fields
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
    
@api_view(['GET'])
@authentication_classes([FirebaseAuthentication])
def test_auth(request):
    return JsonResponse({
        'authenticated': request.user.is_authenticated,
        'user_id': getattr(request.user, 'id', None),
        'email': getattr(request.user, 'email', None),
        'firebase_uid': getattr(request._firebase_user, 'firebase_uid', None) if hasattr(request, '_firebase_user') else None,
    })