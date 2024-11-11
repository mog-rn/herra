# core/views.py
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from django.db import connection
from django.core.cache import cache

class HealthCheckView(APIView):
    """
    View to check the health status of the application
    """
    permission_classes = []  # Allow unauthenticated access

    def get(self, request, *args, **kwargs):
        # Check database connection
        try:
            with connection.cursor() as cursor:
                cursor.execute("SELECT 1")
            db_status = "healthy"
        except Exception as e:
            db_status = f"unhealthy: {str(e)}"

        # Check cache connection
        try:
            cache.set('health_check', 'ok', 1)
            cache_status = "healthy"
        except Exception as e:
            cache_status = f"unhealthy: {str(e)}"

        return Response({
            'status': 'online',
            'database': db_status,
            'cache': cache_status,
        }, status=status.HTTP_200_OK)

def custom_404(request, exception):
    """Custom 404 error handler"""
    return Response({
        'error': 'Resource not found',
        'detail': str(exception)
    }, status=status.HTTP_404_NOT_FOUND)

def custom_500(request):
    """Custom 500 error handler"""
    return Response({
        'error': 'Internal server error',
        'detail': 'An unexpected error occurred'
    }, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
    