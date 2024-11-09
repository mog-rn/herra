from django.core.management.base import BaseCommand
from django.contrib.auth import get_user_model

User = get_user_model()

class Command(BaseCommand):
    help = 'Creates a superuser account non-interactively'

    def add_arguments(self, parser):
        parser.add_argument('--email', required=True)
        parser.add_argument('--password', required=True)
        parser.add_argument('--first_name')
        parser.add_argument('--last_name')

    def handle(self, *args, **options):
        email = options['email']
        password = options['password']
        first_name = options.get('first_name', '')
        last_name = options.get('last_name', '')

        if not User.objects.filter(email=email).exists():
            self.stdout.write(f'Creating superuser for {email}')
            User.objects.create_superuser(
                email=email,
                password=password,
                first_name=first_name,
                last_name=last_name
            )
            self.stdout.write(self.style.SUCCESS(f'Superuser {email} created successfully'))
        else:
            self.stdout.write(self.style.WARNING(f'User {email} already exists'))