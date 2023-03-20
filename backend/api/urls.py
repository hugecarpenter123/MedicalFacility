from django.urls import path
from . import views

urlpatterns = [
    path('info/', views.SearchInfoView.as_view(), name='search_info'),
    path('specjalnosc/', views.SpecjalnoscView.as_view(), name='specjalnosc'),
    path('specjalnosc/<int:pk>/', views.SpecjalnoscView.as_view(), name='specjalnosci_detail'),
    path('personel/', views.PersonelView.as_view(), name='personel'),
    path('personel/<int:pk>/', views.PersonelView.as_view(), name='personel_detail'),
    path('termin/', views.TerminView.as_view(), name='termin'),
    path('termin/<int:pk>/', views.TerminView.as_view(), name='termin_detail'),
    path('wizyta/', views.WizytaView.as_view(), name='wizyta'),
    path('wizyta/<int:pk>/', views.WizytaView.as_view(), name='wizyta_detail'),
    path('uzytkownik/', views.UzytkownikView.as_view(), name='uzytkownik'),
    path('uzytkownik/<int:pk>/', views.UzytkownikView.as_view(), name='uzytkownik_detail'),
    path('uzytkownik/zaloguj/', views.UzytkownikLoginView.as_view(), name='uzytkownik_login'),
]