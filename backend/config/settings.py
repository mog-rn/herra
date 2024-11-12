import os
import sys
from pathlib import Path
from dotenv import load_dotenv

# Load environment variables
load_dotenv()

# Build paths inside the project like this: BASE_DIR / 'subdir'.
BASE_DIR = Path(__file__).resolve().parent.parent

# Ensure logs directory exists
LOGS_DIR = os.path.join(BASE_DIR, 'logs')
if not os.path.exists(LOGS_DIR):
    os.makedirs(LOGS_DIR)

# SECURITY WARNING: keep the secret key used in production secret!
SECRET_KEY = os.getenv('SECRET_KEY')

# SECURITY WARNING: don't run with debug turned on in production!
DEBUG = os.getenv('DEBUG', 'False') == 'True'

GEMINI_API_KEY = os.getenv('GEMINI_API_KEY')

# Application definition
INSTALLED_APPS = [
    # Django apps
    'django.contrib.admin',
    'django.contrib.auth',
    'django.contrib.contenttypes',
    'django.contrib.sessions',
    'django.contrib.messages',
    'whitenoise.runserver_nostatic',  # Before staticfiles
    'django.contrib.staticfiles',
    'django.contrib.gis',  # After staticfiles

    # Third-party apps
    'rest_framework',
    'rest_framework.authtoken',
    'drf_yasg',
    'storages',

    # Local apps
    'apps.accounts.apps.AccountsConfig',
    'cycle_tracking',
    'safety',
    'recommendations',
    'core',
]

MIDDLEWARE = [
    'django.middleware.security.SecurityMiddleware',
    'whitenoise.middleware.WhiteNoiseMiddleware',  # After security middleware
    'django.contrib.sessions.middleware.SessionMiddleware',
    'django.middleware.common.CommonMiddleware',
    'django.middleware.csrf.CsrfViewMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
    'django.contrib.messages.middleware.MessageMiddleware',
    'django.middleware.clickjacking.XFrameOptionsMiddleware',
    'core.middleware.RequestTimingMiddleware',
    'core.middleware.SecurityHeadersMiddleware',
]

ROOT_URLCONF = 'config.urls'

TEMPLATES = [
    {
        'BACKEND': 'django.template.backends.django.DjangoTemplates',
        'DIRS': [],
        'APP_DIRS': True,
        'OPTIONS': {
            'context_processors': [
                'django.template.context_processors.debug',
                'django.template.context_processors.request',
                'django.contrib.auth.context_processors.auth',
                'django.contrib.messages.context_processors.messages',
                'django.template.context_processors.static',
            ],
        },
    },
]

WSGI_APPLICATION = 'config.wsgi.application'

# Database configuration with collectstatic handling
class DisableMigrations:
    def __contains__(self, item):
        return True
    def __getitem__(self, item):
        return None

if 'collectstatic' in sys.argv:
    DATABASES = {
        'default': {
            'ENGINE': 'django.db.backends.sqlite3',
            'NAME': ':memory:',
        }
    }
    MIGRATION_MODULES = DisableMigrations()
else:
    DATABASES = {
        'default': {
            'ENGINE': 'django.contrib.gis.db.backends.postgis',
            'NAME': os.getenv('DB_NAME'),
            'USER': os.getenv('DB_USER'),
            'PASSWORD': os.getenv('DB_PASSWORD'),
            'HOST': os.getenv('DB_HOST'),
            'PORT': os.getenv('DB_PORT'),
            'OPTIONS': {
                'sslmode': 'require' if not DEBUG else 'disable'
            }
        }
    }

# GeoDjango Settings
if os.path.exists('/usr/lib/x86_64-linux-gnu/libgdal.so.31'):
    GDAL_LIBRARY_PATH = '/usr/lib/x86_64-linux-gnu/libgdal.so.31'
elif os.path.exists('/usr/lib/libgdal.so'):
    GDAL_LIBRARY_PATH = '/usr/lib/libgdal.so'
else:
    GDAL_LIBRARY_PATH = os.getenv('GDAL_LIBRARY_PATH', '/usr/lib/libgdal.so')

if os.path.exists('/usr/lib/x86_64-linux-gnu/libgeos_c.so.1'):
    GEOS_LIBRARY_PATH = '/usr/lib/x86_64-linux-gnu/libgeos_c.so.1'
elif os.path.exists('/usr/lib/libgeos_c.so'):
    GEOS_LIBRARY_PATH = '/usr/lib/libgeos_c.so'
else:
    GEOS_LIBRARY_PATH = os.getenv('GEOS_LIBRARY_PATH', '/usr/lib/libgeos_c.so')

# Static files configuration
STATIC_ROOT = os.path.join(BASE_DIR, 'staticfiles')
MEDIA_URL = '/media/'
MEDIA_ROOT = os.path.join(BASE_DIR, 'media')

if DEBUG:
    STATIC_URL = '/static/'
    STATICFILES_STORAGE = 'whitenoise.storage.CompressedManifestStaticFilesStorage'
    STATICFILES_DIRS = [os.path.join(BASE_DIR, 'static')]
else:
    GS_BUCKET_NAME = os.getenv('GS_BUCKET_NAME', 'herra-static-assets')
    GS_DEFAULT_ACL = 'publicRead'
    GS_CACHE_CONTROL = 'public, max-age=86400'
    GS_FILE_OVERWRITE = True
    
    STATIC_URL = f'https://storage.googleapis.com/{GS_BUCKET_NAME}/'
    STATICFILES_STORAGE = 'storages.backends.gcloud.GoogleCloudStorage'
    DEFAULT_FILE_STORAGE = 'storages.backends.gcloud.GoogleCloudStorage'

# Media files configuration
MEDIA_URL = '/media/'
MEDIA_ROOT = os.path.join(BASE_DIR, 'media')

if not DEBUG:
    DEFAULT_FILE_STORAGE = 'storages.backends.gcloud.GoogleCloudStorage'

# Your existing settings continue from here...
# [Rest of your settings remain unchanged]

AUTH_PASSWORD_VALIDATORS = [
    {
        'NAME': 'django.contrib.auth.password_validation.UserAttributeSimilarityValidator',
    },
    {
        'NAME': 'django.contrib.auth.password_validation.MinimumLengthValidator',
    },
    {
        'NAME': 'django.contrib.auth.password_validation.CommonPasswordValidator',
    },
    {
        'NAME': 'django.contrib.auth.password_validation.NumericPasswordValidator',
    },
]

# Authentication
AUTH_USER_MODEL = 'accounts.CustomUser'

AUTHENTICATION_BACKENDS = [
    'django.contrib.auth.backends.ModelBackend',
    'apps.accounts.services.firebase_backend.FirebaseAuthentication',
]

# Internationalization
LANGUAGE_CODE = 'en-us'
TIME_ZONE = 'UTC'
USE_I18N = True
USE_TZ = True

# Default primary key field type
DEFAULT_AUTO_FIELD = 'django.db.models.BigAutoField'

# Security Settings
ALLOWED_HOSTS = os.getenv('ALLOWED_HOSTS', 'localhost').split(',') + ['.run.app']

# HTTPS/SSL Settings
SECURE_SSL_REDIRECT = not DEBUG
SECURE_PROXY_SSL_HEADER = ('HTTP_X_FORWARDED_PROTO', 'https')
USE_X_FORWARDED_HOST = True
USE_X_FORWARDED_PORT = True

CSRF_COOKIE_SECURE = not DEBUG
SESSION_COOKIE_SECURE = not DEBUG
CSRF_TRUSTED_ORIGINS = os.getenv('CSRF_TRUSTED_ORIGINS', 'localhost').split(',')

# Rest Framework Settings remain the same
REST_FRAMEWORK = {
    'DEFAULT_PERMISSION_CLASSES': [
        'rest_framework.permissions.IsAuthenticated',
    ],
    'DEFAULT_AUTHENTICATION_CLASSES': [
        'apps.accounts.services.firebase_backend.FirebaseAuthentication',
    ],
    'DEFAULT_PAGINATION_CLASS': 'rest_framework.pagination.PageNumberPagination',
    'PAGE_SIZE': 10,
    'DEFAULT_RENDERER_CLASSES': [
        'rest_framework.renderers.JSONRenderer',
        'rest_framework.renderers.BrowsableAPIRenderer' if DEBUG else None,
    ],
}

# Swagger Settings
REDOC_SETTINGS = {
    'LAZY_RENDERING': False,
    'NATIVE_SCROLLBARS': False,
    'REQUIRED_PROPS_FIRST': True,
    'EXPAND_RESPONSES': '200,201',
}

# Firebase Settings
FIREBASE_CREDENTIALS_FILE = os.getenv('FIREBASE_CREDENTIALS_FILE')

# Email Settings
SENDGRID_API_KEY = os.getenv('SENDGRID_API_KEY')
DEFAULT_FROM_EMAIL = os.getenv('DEFAULT_FROM_EMAIL')

# Logging Configuration
LOGGING = {
    'version': 1,
    'disable_existing_loggers': False,
    'formatters': {
        'verbose': {
            'format': '[{levelname}] {asctime} {module} {process:d} {thread:d} {message}',
            'style': '{',
        },
        'simple': {
            'format': '[{levelname}] {asctime} {message}',
            'style': '{',
        },
    },
    'handlers': {
        'console': {
            'level': 'INFO',
            'class': 'logging.StreamHandler',
            'formatter': 'simple',
        },
        'file': {
            'level': 'INFO',
            'class': 'logging.handlers.RotatingFileHandler',
            'filename': os.path.join(LOGS_DIR, 'django.log'),
            'maxBytes': 1024 * 1024 * 5,  # 5 MB
            'backupCount': 5,
            'formatter': 'verbose',
        },
        'firebase': {
            'level': 'INFO',
            'class': 'logging.handlers.RotatingFileHandler',
            'filename': os.path.join(LOGS_DIR, 'firebase.log'),
            'maxBytes': 1024 * 1024 * 5,
            'backupCount': 5,
            'formatter': 'verbose',
        },
        'core': {
            'level': 'INFO',
            'class': 'logging.handlers.RotatingFileHandler',
            'filename': os.path.join(LOGS_DIR, 'core.log'),
            'maxBytes': 1024 * 1024 * 5,
            'backupCount': 5,
            'formatter': 'verbose',
        },
    },
    'loggers': {
        'django': {
            'handlers': ['console', 'file'],
            'level': 'INFO',
            'propagate': True,
        },
        'django.server': {
            'handlers': ['console', 'file'],
            'level': 'INFO',
            'propagate': False,
        },
        'accounts': {
            'handlers': ['console', 'firebase'],
            'level': 'INFO',
            'propagate': False,
        },
        'core': {
            'handlers': ['console', 'core'],
            'level': 'INFO',
            'propagate': False,
        },
    },
}

# Cache settings
CACHES = {
    'default': {
        'BACKEND': 'django.core.cache.backends.locmem.LocMemCache',
        'LOCATION': 'unique-snowflake',
    }
}

# Messages Settings
MESSAGE_STORAGE = 'django.contrib.messages.storage.session.SessionStorage'

# Security Headers - Update for Cloud Run
SECURE_BROWSER_XSS_FILTER = True
SECURE_CONTENT_TYPE_NOSNIFF = True
X_FRAME_OPTIONS = 'DENY'
if not DEBUG:
    SECURE_HSTS_SECONDS = 31536000  # 1 year
    SECURE_HSTS_INCLUDE_SUBDOMAINS = True
    SECURE_HSTS_PRELOAD = True
    SECURE_REFERRER_POLICY = 'same-origin'

LOGGING['formatters']['cloud_run'] = {
    'format': '%(levelname)s %(asctime)s [%(name)s] %(message)s',
}
LOGGING['handlers']['cloud_run'] = {
    'class': 'logging.StreamHandler',
    'formatter': 'cloud_run',
}
LOGGING['loggers']['django']['handlers'].append('cloud_run')

# Session Settings - Update for Cloud Run
SESSION_ENGINE = 'django.contrib.sessions.backends.db'
SESSION_COOKIE_AGE = 1209600  # 2 weeks
SESSION_COOKIE_HTTPONLY = True
SESSION_COOKIE_NAME = '__Secure-sessionid' if not DEBUG else 'sessionid'