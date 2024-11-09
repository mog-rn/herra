from django.urls import path
from . import views

urlpatterns = [
    path('profile/', views.profile, name='profile'),
    path('sync/', views.sync_user, name='sync_user'),
    path('test-token/', views.test_auth, name='test_firebase_token'),
]