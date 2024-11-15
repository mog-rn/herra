# Generated by Django 5.1.2 on 2024-11-12 03:47

import django.db.models.deletion
from django.conf import settings
from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
    ]

    operations = [
        migrations.CreateModel(
            name='Recommendation',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('cycle_phase', models.CharField(choices=[('menstrual', 'Menstrual'), ('follicular', 'Follicular'), ('ovulation', 'Ovulation'), ('luteal', 'Luteal')], max_length=20)),
                ('type', models.CharField(choices=[('nutrition', 'Nutrition'), ('exercise', 'Exercise'), ('wellness', 'Wellness'), ('productivity', 'Productivity'), ('sleep', 'Sleep')], max_length=20)),
                ('content', models.TextField()),
                ('gemini_prompt', models.TextField()),
                ('is_helpful', models.BooleanField(null=True)),
                ('created_at', models.DateTimeField(auto_now_add=True)),
                ('user', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name='recommendations', to=settings.AUTH_USER_MODEL)),
            ],
            options={
                'ordering': ['-created_at'],
                'indexes': [models.Index(fields=['user', 'cycle_phase', 'type'], name='recommendat_user_id_7186ac_idx'), models.Index(fields=['created_at'], name='recommendat_created_0cbb1c_idx')],
            },
        ),
    ]
