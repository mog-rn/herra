from rest_framework import authentication
from rest_framework import exceptions
from django.contrib.auth import get_user_model
from django.contrib.auth.backends import BaseBackend
from firebase_admin import auth as firebase_auth
import jwt
import logging

logger = logging.getLogger(__name__)
User = get_user_model()

class FirebaseAuthentication(authentication.BaseAuthentication):
    def authenticate(self, request):
        auth_header = request.headers.get('Authorization')
        if not auth_header or not auth_header.startswith('Bearer '):
            logger.debug("No valid Authorization header")
            return None

        try:
            token = auth_header.split('Bearer ')[1]
            
            # Decode token without verification to get uid (matching get_profile)
            decoded_token = jwt.decode(token, options={"verify_signature": False})
            firebase_uid = decoded_token.get('uid')
            
            if not firebase_uid:
                logger.warning("No UID found in token")
                raise exceptions.AuthenticationFailed('Invalid token format')

            try:
                # Get user from database directly (matching get_profile)
                django_user = User.objects.get(firebase_uid=firebase_uid)
                logger.debug(f"Found user: {django_user.email}")
                
                # Store firebase_uid in request for later use if needed
                request.firebase_uid = firebase_uid
                
                return (django_user, None)
                
            except User.DoesNotExist:
                logger.warning(f"No user found for firebase_uid: {firebase_uid}")
                raise exceptions.AuthenticationFailed('User not found')
                
        except jwt.InvalidTokenError as e:
            logger.error(f"Invalid token: {str(e)}")
            raise exceptions.AuthenticationFailed('Invalid token')
        except Exception as e:
            logger.error(f"Authentication error: {str(e)}")
            raise exceptions.AuthenticationFailed(str(e))

    def authenticate_header(self, request):
        return 'Bearer'

class FirebaseAuthenticationBackend(BaseBackend):
    def authenticate(self, request, firebase_uid=None):
        if not firebase_uid:
            return None

        try:
            user = User.objects.get(firebase_uid=firebase_uid)
            logger.info(f"Authenticated user with firebase_uid: {firebase_uid}")
            return user
        except User.DoesNotExist:
            logger.warning(f"No user found with firebase_uid: {firebase_uid}")
            return None

    def get_user(self, user_id):
        try:
            return User.objects.get(pk=user_id)
        except User.DoesNotExist:
            return None