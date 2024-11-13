from rest_framework import viewsets, status
from rest_framework.decorators import action
from rest_framework.response import Response
from django_filters import rest_framework as filters
from rest_framework.permissions import IsAuthenticated
from rest_framework.renderers import JSONRenderer

from apps.accounts.services.firebase_backend import FirebaseAuthentication
from .models import CycleTracking
from .serializers import (
    CycleTrackingSerializer,
    CycleTrackingCreateSerializer,
    SymptomSerializer
)
from .services import CycleService

import logging

logger = logging.getLogger(__name__)

class CycleTrackingViewSet(viewsets.ModelViewSet):
    authentication_classes = [FirebaseAuthentication]
    permission_classes = [IsAuthenticated]
    filter_backends = [filters.DjangoFilterBackend]
    filterset_fields = ['start_date', 'current_phase']
    ordering_fields = ['start_date', 'cycle_length']
    renderer_classes = [JSONRenderer]


    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.cycle_service = CycleService()

    def get_queryset(self):
        logger.debug(f"Request headers: {self.request.headers}")
        logger.debug(f"Auth header: {self.request.headers.get('Authorization')}")
        return CycleTracking.objects.filter(
            user=self.request.user,
            is_active=True
        )

    def get_serializer_class(self):
        if self.action == 'create':
            return CycleTrackingCreateSerializer
        return CycleTrackingSerializer

    def perform_create(self, serializer):
        # Deactivate previous cycles
        CycleTracking.objects.filter(
            user=self.request.user,
            is_active=True
        ).update(is_active=False)
        serializer.save(user=self.request.user)

    @action(detail=True, methods=['post'])
    def add_symptom(self, request, pk=None):
        cycle = self.get_object()
        serializer = SymptomSerializer(
            data=request.data,
            context={'cycle': cycle}
        )
        
        if serializer.is_valid():
            serializer.save(cycle=cycle)
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    @action(detail=False, methods=['get'])
    def current_phase(self, request):
        cycle = self.cycle_service.get_current_cycle(request.user)
        if not cycle:
            return Response(
                {'error': 'No active cycle found'},
                status=status.HTTP_404_NOT_FOUND
            )

        current_phase = self.cycle_service.update_cycle_phase(cycle)
        days_until_next = self.cycle_service.calculate_days_until_next_period(cycle)

        return Response({
            'current_phase': current_phase,
            'days_until_next_period': days_until_next
        })
