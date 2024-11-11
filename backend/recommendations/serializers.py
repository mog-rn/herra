from rest_framework import serializers
from .models import Recommendation

class RecommendationSerializer(serializers.ModelSerializer):
    class Meta:
        model = Recommendation
        fields = [
            'id', 'cycle_phase', 'type', 'content',
            'is_helpful', 'created_at'
        ]
        read_only_fields = ['created_at']

class RecommendationFeedbackSerializer(serializers.ModelSerializer):
    class Meta:
        model = Recommendation
        fields = ['is_helpful']
        