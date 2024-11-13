# safety/serializers.py

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
    location = serializers.SerializerMethodField(read_only=True)

    class Meta:
        model = EmergencyContact
        fields = [
            'id', 'name', 'phone_number', 'contact_type',
            'relationship', 'location', 'address', 'is_primary',
            'is_active', 'created_at', 'latitude', 'longitude'
        ]
        read_only_fields = ['created_at', 'location']

    def get_location(self, obj):
        if obj.location:
            return {
                'latitude': obj.location.y,
                'longitude': obj.location.x
            }
        return None

    def create(self, validated_data):
        lat = validated_data.pop('latitude', None)
        lon = validated_data.pop('longitude', None)
        if lat is not None and lon is not None:
            validated_data['location'] = Point(lon, lat, srid=4326)  # Specifying SRID
        return super().create(validated_data)

class SafeLocationSerializer(serializers.ModelSerializer):
    latitude = serializers.FloatField(write_only=True, required=True)
    longitude = serializers.FloatField(write_only=True, required=True)
    location = serializers.SerializerMethodField(read_only=True)

    class Meta:
        model = SafeLocation
        fields = [
            'id', 'name', 'location', 'address', 'radius',
            'is_hidden', 'is_active', 'created_at',
            'latitude', 'longitude'
        ]
        read_only_fields = ['created_at', 'location']

    def get_location(self, obj):
        if obj.location:
            return {
                'latitude': obj.location.y,
                'longitude': obj.location.x
            }
        return None

    def create(self, validated_data):
        lat = validated_data.pop('latitude')
        lon = validated_data.pop('longitude')
        validated_data['location'] = Point(lon, lat, srid=4326)  # Specifying SRID
        return super().create(validated_data)

class UserLocationSerializer(serializers.ModelSerializer):
    latitude = serializers.FloatField(write_only=True, required=True)
    longitude = serializers.FloatField(write_only=True, required=True)
    location = serializers.SerializerMethodField(read_only=True)

    class Meta:
        model = UserLocation
        fields = [
            'id', 'location', 'address', 'timestamp',
            'battery_level', 'accuracy', 'is_charging',
            'latitude', 'longitude'
        ]
        read_only_fields = ['timestamp', 'location']

    def get_location(self, obj):
        if obj.location:
            return {
                'latitude': obj.location.y,
                'longitude': obj.location.x
            }
        return None

    def create(self, validated_data):
        lat = validated_data.pop('latitude')
        lon = validated_data.pop('longitude')
        validated_data['location'] = Point(lon, lat, srid=4326)  # Specifying SRID
        return super().create(validated_data)

class JourneyTrackingSerializer(serializers.ModelSerializer):
    start_latitude = serializers.FloatField(write_only=True, required=True)
    start_longitude = serializers.FloatField(write_only=True, required=True)
    destination_latitude = serializers.FloatField(write_only=True, required=True)
    destination_longitude = serializers.FloatField(write_only=True, required=True)
    start_location = serializers.SerializerMethodField(read_only=True)
    destination = serializers.SerializerMethodField(read_only=True)
    current_location = serializers.SerializerMethodField(read_only=True)
    current_latitude = serializers.FloatField(write_only=True, required=False)
    current_longitude = serializers.FloatField(write_only=True, required=False)

    class Meta:
        model = JourneyTracking
        fields = [
            'id', 'start_location', 'current_location', 'destination',
            'start_time', 'estimated_arrival', 'is_active', 'safe_word',
            'check_in_interval', 'last_check_in',
            'start_latitude', 'start_longitude',
            'destination_latitude', 'destination_longitude',
            'current_latitude', 'current_longitude'
        ]
        read_only_fields = ['start_time', 'last_check_in', 'start_location', 'destination', 'current_location']

    def get_start_location(self, obj):
        if obj.start_location:
            return {
                'latitude': obj.start_location.y,
                'longitude': obj.start_location.x
            }
        return None

    def get_destination(self, obj):
        if obj.destination:
            return {
                'latitude': obj.destination.y,
                'longitude': obj.destination.x
            }
        return None

    def get_current_location(self, obj):
        if obj.current_location:
            return {
                'latitude': obj.current_location.y,
                'longitude': obj.current_location.x
            }
        return None

    def create(self, validated_data):
        start_lat = validated_data.pop('start_latitude')
        start_lon = validated_data.pop('start_longitude')
        dest_lat = validated_data.pop('destination_latitude')
        dest_lon = validated_data.pop('destination_longitude')
        validated_data['start_location'] = Point(start_lon, start_lat, srid=4326)      # Specifying SRID
        validated_data['destination'] = Point(dest_lon, dest_lat, srid=4326)           # Specifying SRID

        # Optionally set current_location to start_location
        validated_data['current_location'] = validated_data['start_location']

        return super().create(validated_data)

    def update(self, instance, validated_data):
        current_lat = validated_data.pop('current_latitude', None)
        current_lon = validated_data.pop('current_longitude', None)
        if current_lat is not None and current_lon is not None:
            instance.current_location = Point(current_lon, current_lat, srid=4326)     # Specifying SRID
        return super().update(instance, validated_data)

class EmergencyAlertSerializer(serializers.ModelSerializer):
    latitude = serializers.FloatField(write_only=True, required=False)
    longitude = serializers.FloatField(write_only=True, required=False)
    location = serializers.SerializerMethodField(read_only=True)

    class Meta:
        model = EmergencyAlert
        fields = [
            'id', 'alert_type', 'location', 'address', 'status',
            'journey', 'message', 'battery_level', 'created_at', 'updated_at',
            'latitude', 'longitude'
        ]
        read_only_fields = ['created_at', 'updated_at', 'location']

    def get_location(self, obj):
        if obj.location:
            return {
                'latitude': obj.location.y,
                'longitude': obj.location.x
            }
        return None

    def create(self, validated_data):
        lat = validated_data.pop('latitude', None)
        lon = validated_data.pop('longitude', None)
        if lat is not None and lon is not None:
            validated_data['location'] = Point(lon, lat, srid=4326)  # Specifying SRID
        return super().create(validated_data)
