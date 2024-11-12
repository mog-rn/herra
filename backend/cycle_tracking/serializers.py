from rest_framework import serializers
from .models import CycleTracking, Symptom

class SymptomSerializer(serializers.ModelSerializer):
    class Meta:
        model = Symptom
        fields = [
            'id', 'date', 'mood', 'energy', 'flow_intensity',
            'pain_level', 'notes', 'created_at'
        ]
        read_only_fields = ['created_at']

    def validate_date(self, value):
        cycle = self.context.get('cycle')
        if cycle and value < cycle.start_date:
            raise serializers.ValidationError(
                "Symptom date cannot be before cycle start date."
            )
        return value

class CycleTrackingSerializer(serializers.ModelSerializer):
    symptoms = SymptomSerializer(many=True, read_only=True)
    days_until_next_period = serializers.SerializerMethodField()

    class Meta:
        model = CycleTracking
        fields = [
            'id', 'start_date', 'cycle_length', 'current_phase',
            'last_period_date', 'notes', 'symptoms',
            'days_until_next_period', 'created_at', 'updated_at'
        ]
        read_only_fields = ['created_at', 'updated_at', 'current_phase']

    def get_days_until_next_period(self, obj):
        from .services import CycleService
        return CycleService().calculate_days_until_next_period(obj)

        
class CycleTrackingCreateSerializer(serializers.ModelSerializer):
    class Meta:
        model = CycleTracking
        fields = ['last_period_date', 'cycle_length', 'notes']

    def create(self, validated_data):
        user = self.context['request'].user
        validated_data['user'] = user
        validated_data['start_date'] = validated_data['last_period_date']
        return super().create(validated_data)
