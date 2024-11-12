from django.core.validators import RegexValidator
from django.core.exceptions import ValidationError
from django.utils.translation import gettext_lazy as _

phone_regex = RegexValidator(
    regex=r'^\+?1?\d{9,15}$',
    message=_("Phone number must be entered in format: '+999999999'. Up to 15 digits allowed.")
)

def validate_point_location(point):
    """Validate geographic coordinates"""
    if not (-90 <= point.y <= 90) or not (-180 <= point.x <= 180):
        raise ValidationError(
            _('Invalid coordinates. Latitude must be between -90 and 90, longitude between -180 and 180.')
        )