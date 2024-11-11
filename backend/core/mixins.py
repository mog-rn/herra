from django.contrib.gis.geos import Point
from rest_framework import status
from rest_framework.response import Response
from .services import GeocodingService

class LocationMixin:
    """Mixin for handling location-based operations in views"""
    
    geocoding_service = GeocodingService()
    
    def process_location_data(self, request_data):
        """Process location data from request"""
        try:
            latitude = float(request_data.get('latitude'))
            longitude = float(request_data.get('longitude'))
            return self.geocoding_service.coordinates_to_point(latitude, longitude)
        except (TypeError, ValueError) as e:
            logger.error(f"Error processing location data: {str(e)}")
            return None

    def get_location_data(self, request):
        """Extract location data from request"""
        location = self.process_location_data(request.data)
        if not location:
            return Response(
                {'error': 'Invalid location data'},
                status=status.HTTP_400_BAD_REQUEST
            )
        return location
        