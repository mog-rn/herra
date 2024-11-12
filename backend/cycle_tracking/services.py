from datetime import datetime
from .models import CycleTracking, CyclePhase

class CycleService:
    def get_current_cycle(self, user):
        return CycleTracking.objects.filter(
            user=user,
            is_active=True
        ).first()

    def calculate_days_until_next_period(self, cycle):
        today = datetime.now().date()
        days_passed = (today - cycle.last_period_date).days
        return cycle.cycle_length - (days_passed % cycle.cycle_length)

    def calculate_current_phase(self, cycle):
        today = datetime.now().date()
        days_passed = (today - cycle.last_period_date).days % cycle.cycle_length

        if days_passed < 5:
            return CyclePhase.MENSTRUAL
        elif days_passed < 14:
            return CyclePhase.FOLLICULAR
        elif days_passed == 14:
            return CyclePhase.OVULATION
        else:
            return CyclePhase.LUTEAL

    def update_cycle_phase(self, cycle):
        current_phase = self.calculate_current_phase(cycle)
        if cycle.current_phase != current_phase:
            cycle.current_phase = current_phase
            cycle.save(update_fields=['current_phase', 'updated_at'])
        return current_phase