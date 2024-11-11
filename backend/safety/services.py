from django.contrib.gis.geos import Point
from django.contrib.gis.measure import D
from django.contrib.gis.db.models.functions import Distance
from django.conf import settings
from geopy.geocoders import Nominatim
from geopy.distance import geodesic
import requests

class GeocodeService:
    def __init__(self):
        self.geolocator = Nominatim(user_agent="herra_app")

    def geocode_address(self, address):
        try:
            location = self.geolocator.geocode(address)
            if location:
                return Point(location.longitude, location.latitude), location.address
            return None, None
        except Exception as e:
            print(f"Geocoding error: {str(e)}")
            return None, None

    def reverse_geocode(self, latitude, longitude):
        try:
            location = self.geolocator.reverse((latitude, longitude))
            return location.address if location else None
        except Exception as e:
            print(f"Reverse geocoding error: {str(e)}")
            return None

class SafetyService:
    def __init__(self):
        self.geocode_service = GeocodeService()

    def check_safe_zones(self, user, current_location):
        """Check if user is within any safe zones"""
        safe_locations = SafeLocation.objects.filter(
            user=user,
            is_active=True
        ).annotate(
            distance=Distance('location', current_location)
        )

        for safe_location in safe_locations:
            if safe_location.distance.m <= safe_location.radius:
                return True, safe_location
        return False, None

    def find_nearest_safe_location(self, current_location, max_distance=5000):
        """Find nearest safe location within max_distance meters"""
        return SafeLocation.objects.annotate(
            distance=Distance('location', current_location)
        ).filter(
            distance__lte=max_distance
        ).order_by('distance').first()

    def track_journey(self, journey):
        """Update journey tracking and check for safety"""
        current_location = journey.current_location
        destination = journey.destination
        
        # Calculate distance to destination
        distance_to_dest = geodesic(
            (current_location.y, current_location.x),
            (destination.y, destination.x)
        ).meters

        # Check if user has deviated from expected route
        # This is a simple straight-line check, could be enhanced with actual route
        if distance_to_dest > journey.expected_route_deviation:
            self.trigger_journey_alert(journey)

    def trigger_journey_alert(self, journey):
        """Create alert for journey deviation"""
        EmergencyAlert.objects.create(
            user=journey.user,
            alert_type=AlertType.JOURNEY,
            location=journey.current_location,
            journey=journey,
            message="Journey deviation detected"
        )
        