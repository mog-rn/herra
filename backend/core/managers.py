from django.contrib.gis.db import models
from django.contrib.gis.geos import Point
from django.contrib.gis.measure import D

class LocationQuerySet(models.QuerySet):
    def nearby(self, point: Point, km: float = 1, limit: int = None):
        """Find objects within a certain distance of a point"""
        qs = self.filter(location__distance_lte=(point, D(km=km)))
        if limit:
            qs = qs[:limit]
        return qs