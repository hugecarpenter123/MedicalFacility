from django.db import models
from django.contrib.auth.models import AbstractUser
from django.utils import timezone
import datetime


class Specjalnosc(models.Model):
    nazwa = models.CharField(max_length=150)

    def __str__(self):
        return self.nazwa

class Personel(models.Model):
    imie = models.CharField(max_length=50)
    nazwisko = models.CharField(max_length=50)
    specjalnosc = models.ForeignKey(Specjalnosc, on_delete=models.SET_NULL, null=True)

    def __str__(self):
        return f'{self.imie} {self.nazwisko}, {self.specjalnosc}'

class Termin(models.Model):
    data = models.DateTimeField()
    status = models.BooleanField(default=True)
    personel = models.ForeignKey(Personel, on_delete=models.CASCADE)

    def __str__(self):
        return f'{self.data}: {self.personel}'

class Uzytkownik(AbstractUser):
    username = models.CharField(max_length=150, db_column='nazwa_uzytkownika', unique=True)
    first_name = models.CharField(max_length=150, db_column='imie', null=True)
    last_name = models.CharField(max_length=150, db_column='nazwisko', null=True)
    password = models.CharField(max_length=150, db_column='haslo')
    stworzony = models.DateTimeField(default=timezone.now, blank=True, null=True)
    pesel = models.BigIntegerField(unique=True, null=True)
    nr_telefonu = models.IntegerField(null=True)
    miasto = models.CharField(max_length=50)
    kod_pocztowy = models.CharField(max_length=50)
    ulica = models.CharField(max_length=50)
    nr_budynku = models.CharField(max_length=50)

    def __str__(self):
        return f"{self.username}, {self.pesel}"

class Wizyta(models.Model):
    uzytkownik = models.ForeignKey(Uzytkownik, on_delete=models.CASCADE)
    termin = models.ForeignKey(Termin, on_delete=models.CASCADE)

    def __str__(self):
        return f'{self.termin}'


