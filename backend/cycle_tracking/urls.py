from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import CycleTrackingViewSet

router = DefaultRouter()
router.register(r'cycles', CycleTrackingViewSet, basename='cycle')

app_name = 'cycle_tracking'

urlpatterns = [
    path('', include(router.urls)),
]