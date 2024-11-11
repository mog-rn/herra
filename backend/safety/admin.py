# safety/admin.py
from django.contrib.gis import admin
from .models import (
    EmergencyContact,
    SafeLocation,
    UserLocation,
    JourneyTracking,
    EmergencyAlert
)

@admin.register(EmergencyContact)
class EmergencyContactAdmin(admin.ModelAdmin):
    list_display = ['user', 'name', 'phone_number', 'contact_type', 'is_primary']
    list_filter = ['contact_type', 'is_active', 'is_primary']
    search_fields = ['name', 'phone_number', 'user__username']
    readonly_fields = ['created_at', 'updated_at']

@admin.register(SafeLocation)
class SafeLocationAdmin(admin.GISModelAdmin):  # Changed from OSMGeoAdmin to GISModelAdmin
    list_display = ['user', 'name', 'address', 'radius', 'is_active']
    list_filter = ['is_active', 'is_hidden']
    search_fields = ['name', 'address', 'user__username']
    readonly_fields = ['created_at']

@admin.register(UserLocation)
class UserLocationAdmin(admin.GISModelAdmin):  # Changed from OSMGeoAdmin to GISModelAdmin
    list_display = ['user', 'timestamp', 'battery_level', 'is_charging']
    list_filter = ['is_charging']
    search_fields = ['user__username', 'address']
    readonly_fields = ['timestamp']
    ordering = ['-timestamp']

@admin.register(JourneyTracking)
class JourneyTrackingAdmin(admin.GISModelAdmin):  # Changed from OSMGeoAdmin to GISModelAdmin
    list_display = [
        'user', 'start_time', 'estimated_arrival',
        'is_active', 'last_check_in'
    ]
    list_filter = ['is_active']
    search_fields = ['user__username']
    readonly_fields = ['start_time', 'last_check_in']

@admin.register(EmergencyAlert)
class EmergencyAlertAdmin(admin.GISModelAdmin):  # Changed from OSMGeoAdmin to GISModelAdmin
    list_display = [
        'user', 'alert_type', 'status',
        'created_at', 'battery_level'
    ]
    list_filter = ['alert_type', 'status']
    search_fields = ['user__username', 'message']
    readonly_fields = ['created_at', 'updated_at']
    ordering = ['-created_at']
    