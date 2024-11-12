from rest_framework.exceptions import APIException
from rest_framework import status
from django.utils.translation import gettext_lazy as _

class ServiceUnavailable(APIException):
    status_code = status.HTTP_503_SERVICE_UNAVAILABLE
    default_detail = _('Service temporarily unavailable.')
    default_code = 'service_unavailable'

class LocationError(APIException):
    status_code = status.HTTP_400_BAD_REQUEST
    default_detail = _('Invalid location data provided.')
    default_code = 'invalid_location'

class InvalidOperation(APIException):
    status_code = status.HTTP_400_BAD_REQUEST
    default_detail = _('Invalid operation.')
    default_code = 'invalid_operation'