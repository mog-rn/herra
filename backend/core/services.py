from django.contrib.gis.geos import Point
from typing import Tuple, Dict, Optional
import requests
import logging

logger = logging.getLogger(__name__)

class GeocodingService:
    """Service for handling geocoding operations"""
    
    @staticmethod
    def coordinates_to_point(latitude: float, longitude: float) -> Point:
        """Convert latitude and longitude to Point"""
        return Point(longitude, latitude, srid=4326)

    @staticmethod
    def point_to_coordinates(point: Point) -> Tuple[float, float]:
        """Convert Point to latitude, longitude tuple"""
        return point.y, point.x

    @staticmethod
    def get_address_components(address_data: Dict) -> Dict:
        """Extract address components from geocoding response"""
        try:
            components = {
                'city': '',
                'state': '',
                'country': '',
                'postal_code': '',
                'formatted_address': address_data.get('formatted_address', '')
            }
            
            for component in address_data.get('address_components', []):
                types = component.get('types', [])
                if 'locality' in types:
                    components['city'] = component.get('long_name', '')
                elif 'administrative_area_level_1' in types:
                    components['state'] = component.get('long_name', '')
                elif 'country' in types:
                    components['country'] = component.get('long_name', '')
                elif 'postal_code' in types:
                    components['postal_code'] = component.get('long_name', '')
            
            return components
        except Exception as e:
            logger.error(f"Error parsing address components: {str(e)}")
            return {}
            