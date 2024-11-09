import firebase_admin
from firebase_admin import auth as firebase_auth
from django.conf import settings
from django.utils.deprecation import MiddlewareMixin
from django.utils import timezone

from .models import CustomUser
import logging

logger = logging.getLogger(__name__)

class FirebaseAuthMiddleware(MiddlewareMixin):
    def __init__(self, get_response):
        super().__init__(get_response)
        if not firebase_admin._apps:
            cred = firebase_admin.credentials.Certificate(settings.FIREBASE_CREDENTIALS_FILE)
            firebase_admin.initialize_app(cred)
        logger.info("Firebase Admin SDK initialized.")

    def process_request(self, request):
        """Set request._firebase_user and request._firebase_data"""
        # Initialize with None
        request._firebase_user = None
        request._firebase_data = None
        
        auth_header = request.headers.get('Authorization')
        if not auth_header or not auth_header.startswith('Bearer '):
            logger.warning("No valid Authorization header")
            return

        try:
            id_token = auth_header.split('Bearer ')[1]
            decoded_token = firebase_auth.verify_id_token(id_token)
            uid = decoded_token['uid']
            email = decoded_token.get('email')

            logger.info(f"Decoded token UID: {uid}, Email: {email}")

            # Get or create Django user
            user, created = CustomUser.objects.get_or_create(
                firebase_uid=uid,
                defaults={
                    'email': email,
                    'email_verified': decoded_token.get('email_verified', False),
                    'firebase_sign_in_provider': decoded_token.get('firebase', {}).get('sign_in_provider')
                }
            )

            if not created:
                user.email_verified = decoded_token.get('email_verified', False)
                user.firebase_sign_in_provider = decoded_token.get('firebase', {}).get('sign_in_provider')
                user.firebase_last_login = timezone.now()
                user.last_login = timezone.now()
                user.save(update_fields=['email_verified', 'firebase_sign_in_provider', 
                                       'firebase_last_login', 'last_login'])

            # Store the user and token data
            request._firebase_user = user
            request._firebase_data = decoded_token
            
            logger.info(f"Authentication successful - User: {email}, UID: {uid}")

        except Exception as e:
            logger.error(f"Authentication failed: {str(e)}")
            request._firebase_user = None
            request._firebase_data = None

    def __call__(self, request):
        """Process the request and response"""
        self.process_request(request)
        return self.get_response(request)