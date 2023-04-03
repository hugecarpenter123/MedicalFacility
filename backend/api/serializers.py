from rest_framework import serializers
from accounts.models import Personel, Specjalnosc, Termin, Wizyta, Uzytkownik
from django.contrib.auth import get_user_model, authenticate

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


class TerminFullSerializer(serializers.ModelSerializer):
    class Meta:
        model = Termin
        fields = ['id', 'data', 'status', 'personel_id']


class TerminSerializer(serializers.ModelSerializer):
    personel = serializers.SerializerMethodField(read_only=True)
    specjalnosc = serializers.SerializerMethodField(read_only=True)

    class Meta:
        model = Termin
        fields = ['id', 'data', 'personel', 'specjalnosc']

    def get_personel(self, obj):
        personel_obj = Personel.objects.get(pk=obj.personel_id)
        personel = f"{personel_obj.imie} {personel_obj.nazwisko}"
        return personel

    def get_specjalnosc(self, obj):
        specjalnsoc = Personel.objects.get(pk=obj.personel_id).specjalnosc.nazwa
        return specjalnsoc


class WizytaSerializer(serializers.ModelSerializer):
    personel = serializers.SerializerMethodField(read_only=True)
    termin = serializers.SerializerMethodField(read_only=True)
    specjalnosc = serializers.SerializerMethodField(read_only=True)

    class Meta:
        model = Wizyta
        fields = ['id', 'termin', "personel", "specjalnosc"]

    def get_personel(self, obj):
        termin_obj = Termin.objects.get(id=obj.termin.id)
        personel = Personel.objects.get(id=termin_obj.personel.id)
        return f"{personel.imie} {personel.nazwisko}"

    def get_termin(self, obj):
        termin_obj = Termin.objects.get(id=obj.termin.id)
        return termin_obj.data

    def get_specjalnosc(self, obj):
        termin_obj = Termin.objects.get(id=obj.termin.id)
        personel = Personel.objects.get(id=termin_obj.personel.id)
        specjalnosc = personel.specjalnosc.nazwa

        return specjalnosc


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
        fields = (
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
        )
        extra_kwargs = {
            'password': {'write_only': True},
        }

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
        return super().validate(attrs)

    def create(self, validated_data):
        new_user = User.objects.create_user(
            **validated_data
        )
        return new_user

class UzytkownikAccountsSettingsSerializer(serializers.ModelSerializer):
    old_password = serializers.CharField(required=False, allow_blank=True, write_only=True)
    new_password = serializers.CharField(required=False, allow_blank=True, write_only=True)

    class Meta:
        model = User
        fields = (
            'email',
            'old_password',
            'new_password',
            'first_name',
            'last_name',
            'nr_telefonu',
            'miasto',
            'kod_pocztowy',
            'ulica',
            'nr_budynku',
        )

        extra_kwargs = {
            field_name: {'required': False, 'allow_blank': True}
            for field_name in fields if field_name not in ('old_password', 'new_password')
        }

    def validate(self, attrs):
        user = self.instance
        email = attrs.get('email')
        old_pwd = attrs.get('old_password')
        new_pwd = attrs.get('new_password')

        # email check ---------------------
        if email:
            print("email exists: ", email)
            if User.objects.filter(email=email).exists():
                raise serializers.ValidationError("Nowy email już istnieje")

        # password check ------------------
        if old_pwd and new_pwd:
            print("both pwd's are: ", old_pwd, new_pwd)
            if not authenticate(username=user.username, password=old_pwd):
                raise serializers.ValidationError("Stare hasło jest niepoprawne")

        elif any((old_pwd, new_pwd)):
            raise serializers.ValidationError("Nie podano jednego z haseł")

        return attrs

    def update(self, instance, validated_data):
        email = validated_data.get('email')
        new_pwd = validated_data.get('new_password')
        first_name = validated_data.get('first_name')
        last_name = validated_data.get('last_name')
        phone = validated_data.get('nr_telefonu')
        city = validated_data.get('miasto')
        city_code = validated_data.get('kod_pocztowy')
        street = validated_data.get('ulica')
        house_number = validated_data.get('nr_budynku')

        if email: instance.email = email
        if new_pwd: instance.set_password(new_pwd)
        if first_name: instance.first_name = first_name
        if last_name: instance.last_name = last_name
        if phone: instance.nr_telefonu = phone
        if city: instance.miasto = city
        if city_code: instance.kod_pocztowy = city_code
        if street: instance.ulica = street
        if house_number: instance.nr_budynku = house_number

        instance.save()

        return instance
