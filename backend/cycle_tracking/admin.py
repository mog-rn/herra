from django.contrib import admin
from .models import CycleTracking, Symptom

@admin.register(CycleTracking)
class CycleTrackingAdmin(admin.ModelAdmin):
    list_display = ['user', 'start_date', 'current_phase', 'is_active']
    list_filter = ['current_phase', 'is_active']
    search_fields = ['user__username', 'notes']
    date_hierarchy = 'start_date'

@admin.register(Symptom)
class SymptomAdmin(admin.ModelAdmin):
    list_display = ['cycle', 'date', 'mood', 'energy', 'flow_intensity']
    list_filter = ['date']
    search_fields = ['notes']
    date_hierarchy = 'date'