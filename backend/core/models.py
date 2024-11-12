from django.contrib.gis.db import models
import uuid

class TimeStampedModel(models.Model):
    """Base model with UUID, created and updated timestamps"""
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        abstract = True

class Location(TimeStampedModel):
    """Base model for location-based models"""
    location = models.PointField(null=True, blank=True)
    address = models.TextField(blank=True)
    city = models.CharField(max_length=100, blank=True)
    state = models.CharField(max_length=100, blank=True)
    country = models.CharField(max_length=100, blank=True)
    postal_code = models.CharField(max_length=20, blank=True)
    formatted_address = models.TextField(blank=True)

    class Meta:
        abstract = True
        