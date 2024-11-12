import logging
from typing import Any, Dict, Optional
from django.conf import settings

logger = logging.getLogger(__name__)

class LoggingUtils:
    @staticmethod
    def log_error(error: Exception, module: str, context: Optional[Dict] = None) -> None:
        """Centralized error logging"""
        if context is None:
            context = {}
        
        error_message = f"{module}: {str(error)}"
        if context:
            error_message += f" Context: {context}"
        
        logger.error(error_message, exc_info=True)

    @staticmethod
    def log_info(message: str, module: str, context: Optional[Dict] = None) -> None:
        """Centralized info logging"""
        if context is None:
            context = {}
        
        info_message = f"{module}: {message}"
        if context:
            info_message += f" Context: {context}"
        
        logger.info(info_message)