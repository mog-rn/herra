from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import RecommendationViewSet

router = DefaultRouter()
router.register(r'recommendations', RecommendationViewSet, basename='recommendation')

app_name = 'ai_recommendations'

urlpatterns = [
    path('', include(router.urls)),
]