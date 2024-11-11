# config/urls.py
from django.contrib import admin
from django.urls import path, include
from drf_yasg.views import get_schema_view
from drf_yasg import openapi
from rest_framework import permissions

schema_view = get_schema_view(
   openapi.Info(
      title="Herra API",
      default_version='v1',
      description="API for Herra application",
      terms_of_service="https://www.yourapp.com/terms/",
      contact=openapi.Contact(email="contact@yourapp.com"),
      license=openapi.License(name="BSD License"),
   ),
   public=True,
   permission_classes=[permissions.AllowAny],
)

urlpatterns = [
    path('admin/', admin.site.urls),
    path('docs/', schema_view.with_ui('redoc', cache_timeout=0), name='schema-redoc'),
    path('swagger/', schema_view.with_ui('swagger', cache_timeout=0), name='schema-swagger-ui'),
    
    # API URLs
    path('api/v1/', include([
        path('', include(('core.urls', 'core'), namespace='core')),
        path('accounts/', include(('apps.accounts.urls', 'accounts'), namespace='accounts')),
        path('cycle/', include(('cycle_tracking.urls', 'cycle_tracking'), namespace='cycle_tracking')),
        path('safety/', include(('safety.urls', 'safety'), namespace='safety')),
        path('recommendations/', include(('recommendations.urls', 'recommendations'), namespace='recommendations')),
    ])),
]

# Add these for custom error handling
handler404 = 'core.views.custom_404'
handler500 = 'core.views.custom_500'
