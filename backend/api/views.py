from django.http import HttpResponse
from django.shortcuts import render, get_object_or_404
from rest_framework import generics, status
from rest_framework.response import Response
from rest_framework.views import APIView
from django.contrib.auth import get_user_model, authenticate
from accounts.models import Specjalnosc, Personel, Termin, Wizyta
from rest_framework import mixins
from . import serializers
import datetime
from django.utils import timezone
# Create your views here.

User = get_user_model()

def usun_stare_terminy():
    stare_terminy = Termin.objects.filter(data__lte=timezone.now()+timezone.timedelta(hours=-1))
    print("stare_terminy:", stare_terminy)
    stare_terminy.delete()

class SpecjalnoscView(generics.GenericAPIView, mixins.ListModelMixin, mixins.RetrieveModelMixin):
    queryset = Specjalnosc.objects.all()
    serializer_class = serializers.SpecjalnoscSerializer
    def get(self, request, pk=None):
        if pk:
            return self.retrieve(request)
        return self.list(request)

class PersonelView(generics.GenericAPIView, mixins.ListModelMixin, mixins.RetrieveModelMixin):
    queryset = Personel.objects.all()
    serializer_class = serializers.PersonelSerializer
    def get(self, request, pk=None):
        if pk:
            return self.retrieve(request)
        return self.list(request)


class TerminView(generics.GenericAPIView, mixins.ListModelMixin, mixins.RetrieveModelMixin, mixins.UpdateModelMixin):
    serializer_class = serializers.TerminSerializer

    def get(self, request, pk=None, *args, **kwargs):
        usun_stare_terminy()
        if pk:
            return self.retrieve(request, *args, **kwargs)
        return self.list(request, *args, **kwargs)

    def put(self, request, pk, *args, **kwargs):
        obj = Termin.objects.get(pk=pk)
        obj.status = request.data['status']
        obj.save()
        serializer = self.serializer_class(instance=obj)
        return Response(serializer.data, status=202)

    def get_queryset(self):
        queryset = Termin.objects.all().order_by('data')

        personel_id = self.request.query_params.get('personel_id', None)
        data = self.request.query_params.get('data', None)
        specjalnosc_id = self.request.query_params.get('specjalnosc_id', None)

        if personel_id:
            queryset = queryset.filter(personel_id=personel_id)
        if data:
            data = int(data) if data.isnumeric() else None
            if data:
                queryset = queryset.filter(data__date__lt=timezone.now()+timezone.timedelta(days=data))
        if specjalnosc_id:
            queryset = queryset.filter(personel__specjalnosc__id=specjalnosc_id)
            print(queryset)

        return queryset


class WizytaView(generics.GenericAPIView, mixins.ListModelMixin, mixins.RetrieveModelMixin, mixins.CreateModelMixin,    mixins.DestroyModelMixin):
    queryset = Wizyta.objects.all().order_by('id')
    serializer_class = serializers.WizytaSerializer

    def get(self, request, pk=None):
        usun_stare_terminy()
        if pk:
            return self.retrieve(request)
        return self.list(request)

    def post(self, request, pk=None):
        termin_pk = request.data.get('termin', None)
        termin_obj = get_object_or_404(Termin, id=termin_pk)
        user_pk = request.data.get('uzytkownik', None)
        user_obj = get_object_or_404(User, id=user_pk)
        # stwórz wizytę
        wizyta_obj = Wizyta.objects.create(termin=termin_obj, uzytkownik=user_obj)
        wizyta_obj.save()
        # ustaw status terminu na False
        termin_obj.status = False
        termin_obj.save()
        return Response(serializers.WizytaSerializer(wizyta_obj).data, status=201)

    def delete(self, request, pk):
        # przed usunięciem Wizyty (odwołaniem) przywróć status na TrueFrom
        termin_pk = Wizyta.objects.get(id=pk).termin.id
        termin_obj_qs = Termin.objects.filter(id=termin_pk)
        if termin_obj_qs:
            termin_obj_qs[0].status = True
            termin_obj_qs[0].save()
        return self.destroy(request)


class UzytkownikView(APIView):

    def get_object(self, pk):
        object = get_object_or_404(User, id=pk)
        return object

    def get(self, request, pk=None):
        if pk:
            object = self.get_object(pk)
            return Response(serializers.UzytkownikSerializer(object).data, status.HTTP_200_OK)
        objects = User.objects.all()
        return Response(serializers.UzytkownikSerializer(objects, many=True).data, status.HTTP_200_OK)

    def post(self, request):
        serializer = serializers.UzytkownikCreationSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status.HTTP_400_BAD_REQUEST)

    def update(self, request, pk):
        return

    def delete(self, request, pk):
        return

class UzytkownikLoginView(APIView):
    def post(self, request, pk=None):
        login = request.data.get('login', None)
        password = request.data.get('password', None)

        if not (login and password):
            return Response({'isAuthenticated': False, 'id': None}, status=status.HTTP_400_BAD_REQUEST)

        # sprawdź czy login jest numeryczny, jeśli tak - sprawdź czy istnieje taki pesel
        if isinstance(login, int) or login.isnumeric():
            query = User.objects.filter(pesel=login)
            if query:
                login = query[0].username

        user = authenticate(username=login, password=password)
        if user:
            return Response({'isAuthenticated': True, 'id': user.id}, status=status.HTTP_200_OK)
        else:
            return Response({'isAuthenticated': False, 'id': None}, status=status.HTTP_200_OK)

# class SearchInfoView(APIView):
#     serializer_class = serializers.InfoSerializer(many=True)
#
#     def get(self, request, pk=None):
#         return Response(self.serializer_class.data, status=status.HTTP_200_OK)


class SearchInfoView(APIView):
    specjalnosc_qs = Specjalnosc.objects.all()
    personel_qs = Personel.objects.all()

    def get(self, request, pk=None):
        specjalnosc_list = [x.nazwa for x in self.specjalnosc_qs]
        personel_list = [f"{x.imie} {x.nazwisko}" for x in self.personel_qs]
        data = {
            "specjalnosc": specjalnosc_list,
            "personel": personel_list
        }
        return Response(data, status=status.HTTP_200_OK)