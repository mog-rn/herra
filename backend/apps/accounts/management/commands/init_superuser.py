from django.core.management.base import BaseCommand
from django.contrib.auth import get_user_model

User = get_user_model()

class Command(BaseCommand):
    help = 'Create a superuser non-interactively'

    def add_arguments(self, parser):
        parser.add_argument('--email', required=True)
        parser.add_argument('--password', required=True)
        parser.add_argument('--first_name')
        parser.add_argument('--last_name')

    def handle(self, *args, **kwargs):
        email = kwargs['email']
        password = kwargs['password']
        first_name = kwargs.get('first_name', '')
        last_name = kwargs.get('last_name', '')

        try:
            user = User.objects.get(email=email)
            self.stdout.write(self.style.WARNING(f'User {email} already exists'))
        except User.DoesNotExist:
            User.objects.create_superuser(
                email=email,
                password=password,
                first_name=first_name,
                last_name=last_name,
                is_active=True
            )
            self.stdout.write(self.style.SUCCESS(f'Superuser {email} created successfully'))