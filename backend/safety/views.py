from rest_framework import viewsets, status
from rest_framework.decorators import action
from rest_framework.response import Response
from django.contrib.gis.geos import Point
from django.contrib.gis.db.models.functions import Distance
from django.contrib.gis.measure import D
from .models import (
    EmergencyContact,
    SafeLocation,
    UserLocation,
    JourneyTracking,
    EmergencyAlert,
    AlertType
)
from .serializers import (
    EmergencyContactSerializer,
    SafeLocationSerializer,
    UserLocationSerializer
)
from .services import GeocodeService, SafetyService

class EmergencyContactViewSet(viewsets.ModelViewSet):
    serializer_class = EmergencyContactSerializer
    geocode_service = GeocodeService()

    def get_queryset(self):
        return EmergencyContact.objects.filter(
            user=self.request.user,
            is_active=True
        )

    def perform_create(self, serializer):
        # Check if this is set as primary
        if serializer.validated_data.get('is_primary', False):
            # Set all other contacts as non-primary
            EmergencyContact.objects.filter(
                user=self.request.user,
                is_primary=True
            ).update(is_primary=False)
            
        if serializer.validated_data.get('address'):
            point, formatted_address = self.geocode_service.geocode_address(
                serializer.validated_data['address']
            )
            if point:
                serializer.save(
                    user=self.request.user,
                    location=point,
                    address=formatted_address or serializer.validated_data['address']
                )
            else:
                serializer.save(user=self.request.user)
        else:
            serializer.save(user=self.request.user)

class SafeLocationViewSet(viewsets.ModelViewSet):
    serializer_class = SafeLocationSerializer
    geocode_service = GeocodeService()

    def get_queryset(self):
        return SafeLocation.objects.filter(
            user=self.request.user,
            is_active=True
        )

    def perform_create(self, serializer):
        address = serializer.validated_data.get('address')
        if address:
            point, formatted_address = self.geocode_service.geocode_address(address)
            if point:
                serializer.save(
                    user=self.request.user,
                    location=point,
                    address=formatted_address or address
                )
            else:
                serializer.save(user=self.request.user)
        else:
            serializer.save(user=self.request.user)

    @action(detail=False, methods=['get'])
    def nearby(self, request):
        """Find nearby safe locations"""
        try:
            lat = float(request.query_params.get('latitude'))
            lon = float(request.query_params.get('longitude'))
            radius = float(request.query_params.get('radius', 5000))  # Default 5km
            
            user_location = Point(lon, lat)
            
            nearby_locations = SafeLocation.objects.filter(
                user=request.user,
                is_active=True
            ).annotate(
                distance=Distance('location', user_location)
            ).filter(
                distance__lte=radius
            ).order_by('distance')

            serializer = self.get_serializer(nearby_locations, many=True)
            return Response(serializer.data)
        except (ValueError, TypeError):
            return Response(
                {'error': 'Invalid coordinates provided'},
                status=status.HTTP_400_BAD_REQUEST
            )

class UserLocationViewSet(viewsets.ModelViewSet):
    serializer_class = UserLocationSerializer
    geocode_service = GeocodeService()
    safety_service = SafetyService()

    def get_queryset(self):
        return UserLocation.objects.filter(user=self.request.user)

    def perform_create(self, serializer):
        # Get the location point
        location = Point(
            float(self.request.data.get('longitude')),
            float(self.request.data.get('latitude'))
        )
        
        # Reverse geocode to get address
        address = self.geocode_service.reverse_geocode(
            float(self.request.data.get('latitude')),
            float(self.request.data.get('longitude'))
        )
        
        # Save location
        user_location = serializer.save(
            user=self.request.user,
            location=location,
            address=address
        )
        
        # Check if user is in safe zone
        is_safe, safe_location = self.safety_service.check_safe_zones(
            self.request.user,
            location
        )
        
        # Check active journeys
        active_journey = JourneyTracking.objects.filter(
            user=self.request.user,
            is_active=True
        ).first()
        
        if active_journey:
            active_journey.current_location = location
            active_journey.save()
            self.safety_service.track_journey(active_journey)

        return user_location

class EmergencyAlertViewSet(viewsets.ModelViewSet):
    serializer_class = EmergencyAlertSerializer
    safety_service = SafetyService()

    def get_queryset(self):
        return EmergencyAlert.objects.filter(user=self.request.user)

    @action(detail=False, methods=['post'])
    def trigger(self, request):
        """Trigger emergency alert (disguised as wellness timer)"""
        try:
            lat = float(request.data.get('latitude'))
            lon = float(request.data.get('longitude'))
            location = Point(lon, lat)
            
            alert = EmergencyAlert.objects.create(
                user=request.user,
                alert_type=AlertType.CHECK_IN,
                location=location,
                message="Wellness check initiated",
                battery_level=request.data.get('battery_level')
            )
            
            # Trigger emergency response
            self.safety_service.trigger_emergency_alert(alert)
            
            # Return disguised response
            return Response({
                "session_id": alert.id,
                "message": "Wellness timer started",
                "duration": "10:00"
            })
            
        except (ValueError, TypeError):
            return Response(
                {'error': 'Invalid parameters'},
                status=status.HTTP_400_BAD_REQUEST
            )

    @action(detail=True, methods=['post'])
    def cancel(self, request, pk=None):
        """Cancel emergency alert (disguised as stop timer)"""
        alert = self.get_object()
        alert.status = AlertStatus.RESPONDED
        alert.save()
        
        return Response({
            "message": "Timer stopped",
            "session_completed": False
        })
        