from django.db.models.signals import post_save
from django.dispatch import receiver
from django.contrib.auth import get_user_model
import logging

logger = logging.getLogger(__name__)

User = get_user_model()

@receiver(post_save, sender=User)
def log_user_save(sender, instance, created, **kwargs):
    """Log user creation and updates"""
    if created:
        logger.info(f"New user created: {instance.phone_number}")
    else:
        logger.info(f"User updated: {instance.phone_number}")