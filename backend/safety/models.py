from django.contrib.gis.db import models
from django.contrib.gis.geos import Point
from django.conf import settings
from django.core.validators import RegexValidator

phone_regex = RegexValidator(
    regex=r'^\+?1?\d{9,15}$',
    message="Phone number must be in format: '+999999999'. Up to 15 digits."
)

class ContactType(models.TextChoices):
    EMERGENCY = 'emergency', 'Emergency Contact'
    WELLNESS = 'wellness', 'Wellness Coach'  # Disguised authority contact
    FRIEND = 'friend', 'Friend'

class AlertType(models.TextChoices):
    CHECK_IN = 'check_in', 'Wellness Check'  # Disguised danger alert
    LOCATION = 'location', 'Location Share'
    MEDICAL = 'medical', 'Medical Alert'
    JOURNEY = 'journey', 'Journey Tracking'  # For tracking user's journey

class AlertStatus(models.TextChoices):
    PENDING = 'pending', 'Pending'
    SENT = 'sent', 'Sent'
    RECEIVED = 'received', 'Received'
    RESPONDED = 'responded', 'Responded'

class EmergencyContact(models.Model):
    user = models.ForeignKey(
        settings.AUTH_USER_MODEL,
        on_delete=models.CASCADE,
        related_name='emergency_contacts'
    )
    name = models.CharField(max_length=100)
    phone_number = models.CharField(
        validators=[phone_regex],
        max_length=17
    )
    contact_type = models.CharField(
        max_length=20,
        choices=ContactType.choices,
        default=ContactType.FRIEND
    )
    relationship = models.CharField(max_length=50)
    location = models.PointField(null=True, blank=True)
    address = models.TextField(blank=True)
    is_primary = models.BooleanField(default=False)
    is_active = models.BooleanField(default=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        indexes = [
            models.Index(fields=['user', 'is_active']),
            models.Index(fields=['phone_number']),
        ]

    def save(self, *args, **kwargs):
        if self.is_primary:
            # Ensure only one primary contact per user
            EmergencyContact.objects.filter(
                user=self.user,
                is_primary=True
            ).exclude(pk=self.pk).update(is_primary=False)
        super().save(*args, **kwargs)

class SafeLocation(models.Model):
    user = models.ForeignKey(
        settings.AUTH_USER_MODEL,
        on_delete=models.CASCADE,
        related_name='safe_locations'
    )
    name = models.CharField(max_length=100)
    location = models.PointField()
    address = models.TextField()
    radius = models.IntegerField(
        default=100,  # Default radius in meters
        help_text="Safe zone radius in meters"
    )
    is_hidden = models.BooleanField(
        default=False,
        help_text="Hide this location from main view"
    )
    is_active = models.BooleanField(default=True)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        indexes = [
            models.Index(fields=['user', 'is_active']),
        ]

class UserLocation(models.Model):
    user = models.ForeignKey(
        settings.AUTH_USER_MODEL,
        on_delete=models.CASCADE,
        related_name='locations'
    )
    location = models.PointField()
    address = models.TextField(blank=True)
    timestamp = models.DateTimeField(auto_now_add=True)
    battery_level = models.IntegerField(null=True)
    accuracy = models.FloatField(null=True)
    is_charging = models.BooleanField(default=False)

    class Meta:
        indexes = [
            models.Index(fields=['user', 'timestamp']),
        ]
        ordering = ['-timestamp']

class JourneyTracking(models.Model):
    user = models.ForeignKey(
        settings.AUTH_USER_MODEL,
        on_delete=models.CASCADE,
        related_name='journeys'
    )
    start_location = models.PointField()
    current_location = models.PointField()
    destination = models.PointField()
    start_time = models.DateTimeField(auto_now_add=True)
    estimated_arrival = models.DateTimeField()
    is_active = models.BooleanField(default=True)
    safe_word = models.CharField(max_length=50, blank=True)
    check_in_interval = models.IntegerField(
        default=15,
        help_text="Minutes between check-ins"
    )
    last_check_in = models.DateTimeField(auto_now_add=True)

    class Meta:
        indexes = [
            models.Index(fields=['user', 'is_active']),
        ]

class EmergencyAlert(models.Model):
    user = models.ForeignKey(
        settings.AUTH_USER_MODEL,
        on_delete=models.CASCADE,
        related_name='emergency_alerts'
    )
    alert_type = models.CharField(
        max_length=20,
        choices=AlertType.choices,
        default=AlertType.CHECK_IN
    )
    location = models.PointField(null=True)
    address = models.TextField(blank=True)
    status = models.CharField(
        max_length=20,
        choices=AlertStatus.choices,
        default=AlertStatus.PENDING
    )
    journey = models.ForeignKey(
        JourneyTracking,
        null=True,
        blank=True,
        on_delete=models.SET_NULL,
        related_name='alerts'
    )
    message = models.TextField(blank=True)
    battery_level = models.IntegerField(null=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        indexes = [
            models.Index(fields=['user', 'created_at']),
            models.Index(fields=['status']),
        ]
        