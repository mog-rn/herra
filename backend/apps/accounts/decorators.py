from functools import wraps
from django.http import JsonResponse
from firebase_admin import auth
import logging

logger = logging.getLogger('accounts')

def firebase_auth_required(view_func):
    @wraps(view_func)
    def wrapped_view(request, *args, **kwargs):
        auth_header = request.headers.get('Authorization')
        
        if not auth_header:
            logger.error("No Authorization header present")
            return JsonResponse({'error': 'No Authorization header'}, status=401)
            
        if not auth_header.startswith('Bearer '):
            logger.error("Invalid Authorization header format")
            return JsonResponse({'error': 'Invalid Authorization format'}, status=401)
            
        try:
            token = auth_header.split('Bearer ')[1]
            decoded_token = auth.verify_id_token(token)
            request.firebase_user = decoded_token
            logger.info(f"Successfully authenticated user: {decoded_token.get('uid')}")
            return view_func(request, *args, **kwargs)
            
        except auth.InvalidIdTokenError:
            logger.error("Invalid Firebase ID token")
            return JsonResponse({'error': 'Invalid token'}, status=401)
            
        except auth.ExpiredIdTokenError:
            logger.error("Expired Firebase ID token")
            return JsonResponse({'error': 'Token expired'}, status=401)
            
        except auth.RevokedIdTokenError:
            logger.error("Revoked Firebase ID token")
            return JsonResponse({'error': 'Token revoked'}, status=401)
            
        except auth.CertificateFetchError:
            logger.error("Failed to fetch Firebase certificates")
            return JsonResponse({'error': 'Authorization server unavailable'}, status=503)
            
        except Exception as e:
            logger.error(f"Firebase authentication error: {str(e)}")
            return JsonResponse({'error': 'Authentication failed'}, status=401)
            
    return wrapped_view
