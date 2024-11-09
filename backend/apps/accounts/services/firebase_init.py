import firebase_admin
from firebase_admin import credentials
from django.conf import settings
import logging
import os

logger = logging.getLogger(__name__)

def initialize_firebase():
    try:
        # Check if Firebase is already initialized
        firebase_admin.get_app()
        logger.info("Firebase Admin SDK already initialized.")
    except ValueError:
        try:
            # Get the absolute path to your service account file
            service_account_path = os.path.join(
                settings.BASE_DIR,
                # 'accounts',
                'shield-her-e8fe5-firebase-adminsdk-h12gc-7bc8ee4192.json'
            )
            
            if not os.path.exists(service_account_path):
                logger.error(f"Service account file not found at: {service_account_path}")
                raise FileNotFoundError(f"Service account file not found at: {service_account_path}")
            
            # Initialize Firebase Admin SDK
            cred = credentials.Certificate(service_account_path)
            firebase_admin.initialize_app(cred)
            logger.info("Firebase Admin SDK initialized successfully.")
        except Exception as e:
            logger.error(f"Failed to initialize Firebase Admin SDK: {e}")
            raise

# Initialize Firebase when this module is imported
initialize_firebase()