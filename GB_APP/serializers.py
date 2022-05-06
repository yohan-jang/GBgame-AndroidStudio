from rest_framework import serializers
from GB_APP.models import Test

class TestSerializer(serializers.ModelSerializer):
    class Meta:
        model = Test
        fields = ('test', 'id')
