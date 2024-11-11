from rest_framework import permissions

class IsOwner(permissions.BasePermission):
    """Permission to only allow owners of an object to access it"""
    
    def has_object_permission(self, request, view, obj):
        if hasattr(obj, 'user'):
            return obj.user == request.user
        return obj == request.user

class IsVerifiedUser(permissions.BasePermission):
    """Permission to only allow verified users"""
    
    def has_permission(self, request, view):
        return bool(request.user and request.user.is_verified)