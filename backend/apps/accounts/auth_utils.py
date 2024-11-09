from django.contrib.auth.models import AnonymousUser

class FirebaseUser:
    def __init__(self, user, firebase_data=None):
        self._user = user
        self.firebase_data = firebase_data
    
    @property
    def is_authenticated(self):
        return True
    
    @property
    def is_anonymous(self):
        return False
    
    def __getattr__(self, name):
        return getattr(self._user, name)

def get_user_from_request(request):
    if not hasattr(request, '_cached_firebase_user'):
        if hasattr(request, '_firebase_user') and request._firebase_user:
            request._cached_firebase_user = FirebaseUser(
                request._firebase_user,
                getattr(request, '_firebase_data', None)
            )
        else:
            request._cached_firebase_user = AnonymousUser()
    return request._cached_firebase_user