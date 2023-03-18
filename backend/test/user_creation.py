from django.contrib.auth import get_user_model

User = get_user_model()

user = User.objects.create_user(
    username='lisamormont',
    password='password123',
    first_name='Lisa',
    email='lisamm@123.com',
    last_name='Mormont',
    pesel=12341252982,
    nr_telefonu=123543234,
    miasto='Miami',
    kod_pocztowy='12002',
    ulica='Main St.',
    nr_budynku='223'
)

user2 = {
    "username": "carenloren",
    "password": "password123",
    "first_name": "Carne",
    "email": "carenloren@123.com",
    "last_name": "Loren",
    "pesel": 12341250082,
    "nr_telefonu": 123243234,
    "miasto": "Chelsea",
    "kod_pocztowy": "22-002",
    "ulica": "Main St.",
    "nr_budynku": "693"
}
