from django.contrib import admin
from .models import Recommendation

@admin.register(Recommendation)
class RecommendationAdmin(admin.ModelAdmin):
    list_display = ['user', 'cycle_phase', 'type', 'is_helpful', 'created_at']
    list_filter = ['cycle_phase', 'type', 'is_helpful']
    search_fields = ['content', 'gemini_prompt']
    date_hierarchy = 'created_at'
    