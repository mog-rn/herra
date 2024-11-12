from django.urls import path
from rest_framework.routers import DefaultRouter
from .views import HealthCheckView

router = DefaultRouter()

urlpatterns = [
    path('health/', HealthCheckView.as_view(), name='health-check'),
] + router.urls