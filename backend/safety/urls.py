from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import (
    EmergencyContactViewSet,
    SafeLocationViewSet,
    UserLocationViewSet,
    EmergencyAlertViewSet
)

router = DefaultRouter()
router.register(r'contacts', EmergencyContactViewSet, basename='contact')
router.register(r'locations', SafeLocationViewSet, basename='location')
router.register(r'tracking', UserLocationViewSet, basename='tracking')
router.register(r'alerts', EmergencyAlertViewSet, basename='alert')

app_name = 'safety'

urlpatterns = [
    path('', include(router.urls)),
]
