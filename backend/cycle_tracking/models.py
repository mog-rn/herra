from django.db import models
from django.conf import settings

class CyclePhase(models.TextChoices):
    MENSTRUAL = 'menstrual', 'Menstrual'
    FOLLICULAR = 'follicular', 'Follicular'
    OVULATION = 'ovulation', 'Ovulation'
    LUTEAL = 'luteal', 'Luteal'

class SymptomIntensity(models.IntegerChoices):
    NONE = 1, 'None'
    MILD = 2, 'Mild'
    MODERATE = 3, 'Moderate'
    SEVERE = 4, 'Severe'
    EXTREME = 5, 'Extreme'

class CycleTracking(models.Model):
    user = models.ForeignKey(
        settings.AUTH_USER_MODEL,
        on_delete=models.CASCADE,
        related_name='cycle_tracks'
    )
    start_date = models.DateField(db_index=True)
    cycle_length = models.PositiveIntegerField(default=28)
    current_phase = models.CharField(
        max_length=20,
        choices=CyclePhase.choices,
        default=CyclePhase.MENSTRUAL
    )
    last_period_date = models.DateField()
    notes = models.TextField(blank=True)
    is_active = models.BooleanField(default=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        ordering = ['-start_date']
        indexes = [
            models.Index(fields=['user', 'start_date']),
            models.Index(fields=['user', 'is_active']),
        ]

    def __str__(self):
        return f"{self.user.username} - Cycle starting {self.start_date}"

class Symptom(models.Model):
    cycle = models.ForeignKey(
        CycleTracking,
        on_delete=models.CASCADE,
        related_name='symptoms'
    )
    date = models.DateField()
    mood = models.IntegerField(
        choices=SymptomIntensity.choices,
        null=True
    )
    energy = models.IntegerField(
        choices=SymptomIntensity.choices,
        null=True
    )
    flow_intensity = models.IntegerField(
        choices=SymptomIntensity.choices,
        null=True
    )
    pain_level = models.IntegerField(
        choices=SymptomIntensity.choices,
        null=True
    )
    notes = models.TextField(blank=True)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        ordering = ['-date']
        indexes = [
            models.Index(fields=['cycle', 'date']),
        ]
