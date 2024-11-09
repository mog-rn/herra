from rest_framework import authentication
from rest_framework import exceptions
from django.contrib.auth import get_user_model
from django.contrib.auth.models import AnonymousUser
from django.contrib.auth.backends import BaseBackend
import logging

logger = logging.getLogger(__name__)

User = get_user_model()

class FirebaseAuthentication(authentication.BaseAuthentication):
    def authenticate(self, request):
        # Instead of checking request.user, check for _firebase_user directly
        if hasattr(request, '_firebase_user') and request._firebase_user:
            logger.info(f"Found firebase user: {request._firebase_user.email}")
            return (request._firebase_user, None)
        
        # Check for Authorization header
        auth_header = request.headers.get('Authorization')
        if not auth_header or not auth_header.startswith('Bearer '):
            logger.debug("No valid Authorization header")
            return None

        logger.debug("No firebase user found")
        return None

    def authenticate_header(self, request):
        return 'Bearer'

class FirebaseAuthenticationBackend(BaseBackend):
    def authenticate(self, request, firebase_uid=None):
        if not firebase_uid:
            logger.debug("No firebase_uid provided")
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
            user = User.objects.get(pk=user_id)
            logger.debug(f"Found user with id: {user_id}")
            return user
        except User.DoesNotExist:
            logger.warning(f"No user found with id: {user_id}")
            return None