from rest_framework import serializers
from django.contrib.gis.geos import Point
from .models import (
    EmergencyContact,
    SafeLocation,
    UserLocation,
    JourneyTracking,
    EmergencyAlert
)

class EmergencyContactSerializer(serializers.ModelSerializer):
    latitude = serializers.FloatField(write_only=True, required=False)
    longitude = serializers.FloatField(write_only=True, required=False)

    class Meta:
        model = EmergencyContact
        fields = [
            'id', 'name', 'phone_number', 'contact_type',
            'relationship', 'location', 'address', 'is_primary',
            'is_active', 'created_at', 'latitude', 'longitude'
        ]
        read_only_fields = ['created_at']

    def create(self, validated_data):
        lat = validated_data.pop('latitude', None)
        lon = validated_data.pop('longitude', None)
        if lat and lon:
            validated_data['location'] = Point(lon, lat)
        return super().create(validated_data)

class SafeLocationSerializer(serializers.ModelSerializer):
    latitude = serializers.FloatField(write_only=True)
    longitude = serializers.FloatField(write_only=True)

    class Meta:
        model = SafeLocation
        fields = [
            'id', 'name', 'location', 'address', 'radius',
            'is_hidden', 'is_active', 'created_at',
            'latitude', 'longitude'
        ]
        read_only_fields = ['created_at']

    def create(self, validated_data):
        lat = validated_data.pop('latitude')
        lon = validated_data.pop('longitude')
        validated_data['location'] = Point(lon, lat)
        return super().create(validated_data)

class UserLocationSerializer(serializers.ModelSerializer):
    latitude = serializers.FloatField(write_only=True)
    longitude = serializers.FloatField(write_only=True)

    class Meta:
        model = UserLocation
        fields = [
            'id', 'location', 'address', 'timestamp',
            'battery_level', 'accuracy', 'is_charging',
            'latitude', 'longitude'
        ]
        read_only_fields = ['timestamp']

    def create(self, validated_data):
        lat = validated_data.pop('latitude')
        lon = validated_data.pop('longitude')
        validated_data['location'] = Point(lon, lat)
        return super().create(validated_data)
        