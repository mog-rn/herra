from django.contrib.auth.models import AbstractBaseUser, BaseUserManager, PermissionsMixin
from django.db import models
from django.utils import timezone

class CustomUserManager(BaseUserManager):
    def create_user(self, email, password=None, **extra_fields):
        if not email:
            raise ValueError('The Email field must be set')
        email = self.normalize_email(email)
        
        # Ensure firebase_uid is properly handled
        firebase_uid = extra_fields.pop('firebase_uid', None)
        
        user = self.model(email=email, **extra_fields)
        if firebase_uid:
            user.firebase_uid = firebase_uid
            
        if password:
            user.set_password(password)
        else:
            user.set_unusable_password()
            
        user.save(using=self._db)
        return user

    def create_superuser(self, email, password=None, **extra_fields):
        extra_fields.setdefault('is_staff', True)
        extra_fields.setdefault('is_superuser', True)
        extra_fields.setdefault('is_active', True)

        if extra_fields.get('is_staff') is not True:
            raise ValueError('Superuser must have is_staff=True.')
        if extra_fields.get('is_superuser') is not True:
            raise ValueError('Superuser must have is_superuser=True.')

        return self.create_user(email, password, **extra_fields)
    
    def get_by_firebase_uid(self, firebase_uid):
        """
        Helper method to get a user by firebase_uid
        """
        return self.get(firebase_uid=firebase_uid)

class CustomUser(AbstractBaseUser, PermissionsMixin):
    email = models.EmailField(
        unique=True,
        error_messages={
            'unique': 'A user with that email already exists.',
        }
    )
    firebase_uid = models.CharField(
        max_length=255,
        unique=True,
        null=True,
        blank=True,
        db_index=True,  # Add index for faster queries
        error_messages={
            'unique': 'This Firebase UID is already registered.',
        }
    )
    first_name = models.CharField(max_length=30, blank=True)
    last_name = models.CharField(max_length=30, blank=True)
    is_active = models.BooleanField(
        default=True,
        help_text='Designates whether this user should be treated as active.',
    )
    is_staff = models.BooleanField(
        default=False,
        help_text='Designates whether the user can log into the admin site.',
    )
    date_joined = models.DateTimeField(default=timezone.now)
    last_login = models.DateTimeField(null=True, blank=True)
    
    # Add fields for tracking Firebase-specific data
    email_verified = models.BooleanField(default=False)
    firebase_last_login = models.DateTimeField(null=True, blank=True)
    firebase_sign_in_provider = models.CharField(max_length=50, null=True, blank=True)

    objects = CustomUserManager()

    USERNAME_FIELD = 'email'
    REQUIRED_FIELDS = []

    class Meta:
        verbose_name = 'user'
        verbose_name_plural = 'users'
        indexes = [
            models.Index(fields=['email', 'firebase_uid']),
        ]

    def __str__(self):
        return self.email

    def get_full_name(self):
        """
        Return the first_name plus the last_name, with a space in between.
        """
        full_name = f"{self.first_name} {self.last_name}".strip()
        return full_name or self.email

    def get_short_name(self):
        """Return the short name for the user."""
        return self.first_name or self.email.split('@')[0]

    @property
    def is_authenticated(self):
        """
        Always return True for CustomUser instances.
        This is how Django knows to treat this user as authenticated.
        """
        return True

    def update_firebase_data(self, firebase_user_data):
        """
        Update user data from Firebase user record
        """
        self.email_verified = firebase_user_data.get('email_verified', False)
        self.firebase_sign_in_provider = (
            firebase_user_data.get('firebase', {})
            .get('sign_in_provider')
        )
        self.firebase_last_login = timezone.now()
        self.save()