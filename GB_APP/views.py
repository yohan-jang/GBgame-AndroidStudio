from django.shortcuts import render

from rest_framework import viewsets

from GB_APP.models import Test
from GB_APP.serializers import TestSerializer

class TestViewSet(viewsets.ModelViewSet):
    queryset = Test.objects.all()
    serializer_class = TestSerializer
