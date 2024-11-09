from django.urls import path
from . import views

urlpatterns = [
    path('register/', views.register_user, name='register_user'),
    path('login/', views.login, name='login'),
    path('refresh-token/', views.refresh_access_token, name='refresh_token'),

    path('password/change/', views.change_password, name='change_password'),
    path('password/reset/', views.forgot_password, name='forgot_password'),
    path('email/verify/', views.verify_email, name='verify_email'),

    path('profile/', views.get_profile, name='profile'),
    path('profile/update/', views.update_profile, name='update_profile'),

    path('account/delete/', views.delete_account, name='delete_account'),
    
]