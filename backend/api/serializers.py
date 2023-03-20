from rest_framework import serializers
from accounts.models import Personel, Specjalnosc, Termin, Wizyta, Uzytkownik
from django.contrib.auth import get_user_model

User = get_user_model()

class PersonelSerializer(serializers.ModelSerializer):
    class Meta:
        model = Personel
        fields = ['id', 'imie', 'nazwisko']


class SpecjalnoscSerializer(serializers.ModelSerializer):
    personel_set = PersonelSerializer(many=True, read_only=True)

    class Meta:
        model = Specjalnosc
        fields = ['id', 'nazwa', 'personel_set']

class TerminSerializer(serializers.ModelSerializer):
    class Meta:
        model = Termin
        fields = ['id', 'data', 'status', 'personel_id']


class WizytaSerializer(serializers.ModelSerializer):
    personel = serializers.SerializerMethodField(read_only=True)
    termin = serializers.SerializerMethodField(read_only=True)

    class Meta:
        model = Wizyta
        fields = ['id', 'personel', 'termin']

    def get_personel(self, obj):
        termin_obj = Termin.objects.get(id=obj.termin.id)
        personel = Personel.objects.get(id=termin_obj.personel.id)
        return f"{personel.imie} {personel.nazwisko}"

    def get_termin(self, obj):
        termin_obj = Termin.objects.get(id=obj.termin.id)
        return termin_obj.data

class UzytkownikSerializer(serializers.ModelSerializer):
    wizyty = serializers.SerializerMethodField(read_only=True)

    class Meta:
        model = Uzytkownik
        fields = ["id", "first_name", "last_name", "wizyty"]

    def get_wizyty(self, obj):
        wizyty_qs = Wizyta.objects.filter(uzytkownik=obj)
        return WizytaSerializer(wizyty_qs, many=True).data


class UzytkownikCreationSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = [
            'username',
            'first_name',
            'last_name',
            'email',
            'password',
            'pesel',
            'nr_telefonu',
            'miasto',
            'kod_pocztowy',
            'ulica',
            'nr_budynku',
        ]

    def validate_username(self, value):
        if User.objects.filter(username=value).exists():
            raise serializers.ValidationError("Nazwa użytkownika jest zajęta.")
        return value

    def validate_email(self, value):
        if User.objects.filter(email=value).exists():
            raise serializers.ValidationError("Osoba z tym emailem już istnieje.")
        return value

    def validate_pesel(self, value):
        print("validate_pesel() called ---------")
        if User.objects.filter(pesel=value).exists():
            raise serializers.ValidationError("Osoba z tym peselem już istnieje.")
        return value

    def validate(self, attrs):
        # sprawdź czy wszystkie pola zostały wysłane
        for field in self.fields:
            if not attrs.get(field):
                raise serializers.ValidationError('Not all fields provided')


    def create(self, validated_data):
        new_user = User.objects.create_user(
            **validated_data
        )
        return new_user

