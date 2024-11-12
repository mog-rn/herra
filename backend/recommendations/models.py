from django.db import models
from django.conf import settings
from cycle_tracking.models import CyclePhase

class RecommendationType(models.TextChoices):
    NUTRITION = 'nutrition', 'Nutrition'
    EXERCISE = 'exercise', 'Exercise'
    WELLNESS = 'wellness', 'Wellness'
    PRODUCTIVITY = 'productivity', 'Productivity'
    SLEEP = 'sleep', 'Sleep'

class Recommendation(models.Model):
    user = models.ForeignKey(
        settings.AUTH_USER_MODEL,
        on_delete=models.CASCADE,
        related_name='recommendations'
    )
    cycle_phase = models.CharField(
        max_length=20,
        choices=CyclePhase.choices
    )
    type = models.CharField(
        max_length=20,
        choices=RecommendationType.choices
    )
    content = models.TextField()
    gemini_prompt = models.TextField()  # Store the prompt used
    is_helpful = models.BooleanField(null=True)  # User feedback
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        indexes = [
            models.Index(fields=['user', 'cycle_phase', 'type']),
            models.Index(fields=['created_at']),
        ]
        ordering = ['-created_at']
        