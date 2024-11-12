from rest_framework import viewsets, status
from rest_framework.decorators import action
from rest_framework.response import Response
from cycle_tracking.models import CycleTracking
from .models import Recommendation
from .serializers import RecommendationSerializer, RecommendationFeedbackSerializer
from .services import GeminiService

class RecommendationViewSet(viewsets.ReadOnlyModelViewSet):
    serializer_class = RecommendationSerializer
    gemini_service = GeminiService()

    def get_queryset(self):
        return Recommendation.objects.filter(user=self.request.user)

    @action(detail=False, methods=['get'])
    def current(self, request):
        """Get recommendations for current cycle phase"""
        # Get current cycle
        current_cycle = CycleTracking.objects.filter(
            user=request.user,
            is_active=True
        ).first()

        if not current_cycle:
            return Response(
                {'error': 'No active cycle found'},
                status=status.HTTP_404_NOT_FOUND
            )

        # Get recent symptoms
        symptoms = current_cycle.symptoms.all()[:5]
        symptom_dict = {}
        for symptom in symptoms:
            symptom_dict.update({
                'mood': symptom.mood,
                'energy': symptom.energy,
                'pain': symptom.pain_level
            })

        # Generate recommendations
        recommendations = self.gemini_service.generate_recommendations(
            request.user,
            current_cycle.current_phase,
            symptom_dict
        )

        return Response(recommendations)

    @action(detail=True, methods=['post'])
    def feedback(self, request, pk=None):
        """Submit feedback for a recommendation"""
        recommendation = self.get_object()
        serializer = RecommendationFeedbackSerializer(
            recommendation,
            data=request.data,
            partial=True
        )
        
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
        