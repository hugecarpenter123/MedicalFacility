from django.contrib import admin
from . import models

# Register your models here.
class PersonelInline(admin.StackedInline):
    model = models.Personel
    extra = 0
    readonly_fields = ('imie', 'nazwisko')

class SpecjalnoscAdmin(admin.ModelAdmin):
    inlines = [PersonelInline]

admin.site.register(models.Termin)
admin.site.register(models.Personel)
admin.site.register(models.Specjalnosc, SpecjalnoscAdmin)
admin.site.register(models.Uzytkownik)
admin.site.register(models.Wizyta)
