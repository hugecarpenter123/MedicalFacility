import datetime
from django.utils import timezone
# # from accounts.models import Personel, Termin
# # allPersonel = Personel.objects.all()
# #
# # # allPersonel = (1,2,3,4,5)
# #
# # start_date = datetime.datetime.today()
# # start_date = start_date.replace(hour=8, minute=0, second=0, microsecond=0)
# # start_date_looper = start_date
# # end_date = start_date + datetime.timedelta(days=31*3)
# #
# # delta = datetime.timedelta(hours=1)
# #
# # for personel in allPersonel:
# #     while start_date_looper < end_date:
# #         if start_date_looper.weekday() >= 5:
# #             start_date_looper += datetime.timedelta(days=1)
# #             continue
# #
# #         # database operation ...
# #         Termin.objects.create(start_date, True, personel)
# #         # print(f"Termin.objects.create(data={start_date}, status={True}, personel={personel})")
# #
# #         if start_date.hour == 15:  # Check if the hour is 15:00
# #             start_date_looper += datetime.timedelta(days=1)  # Increment the date to the next day
# #             start_date_looper = start_date_looper.replace(hour=8, minute=0)  # Set the time to 8:00 on the next day
# #         else:
# #             start_date_looper += delta  # Increment the time by one hour for the next iteration
# #
# #     start_date_looper = start_date

run_dates = False
if run_dates:
    from django.utils import timezone
    from accounts.models import Personel, Termin
    allPersonel = Personel.objects.all()

    start_date = timezone.now()
    start_date = start_date.replace(hour=7, minute=0, second=0, microsecond=0)
    start_date_looper = start_date
    end_date = start_date + timezone.timedelta(days=30*3)

    delta = timezone.timedelta(hours=1)

    for personel in allPersonel:
        while start_date_looper < end_date:
            if start_date_looper.weekday() >= 5:
                start_date_looper += timezone.timedelta(days=1)
                continue
            t = Termin.objects.create(data=start_date_looper, status=True, personel=personel)
            t.save()
            print(f"Termin.objects.create(data={start_date_looper}, status={True}, personel={personel})")
            if start_date_looper.hour == 14:  # Check if the hour is 15:00
                # print("reset hour to 14 and go next day")
                start_date_looper += timezone.timedelta(days=1)  # Increment the date to the next day
                start_date_looper = start_date_looper.replace(hour=7, minute=0)  # Set the time to 8:00 on the next day
            else:
                start_date_looper += delta  # Increment the time by one hour for the next iteration
        start_date_looper = start_date

if run_dates:
    from django.utils import timezone
    from accounts.models import Personel, Termin
    personel1 = Personel.objects.get(imie="Katarzyna", nazwisko="Piwko")
    allPersonel = [personel1]

    start_date = timezone.now()
    start_date_hour = start_date.hour + 1 # when inflating, hour may be after 7, so pick currunt + 1
    start_date = start_date.replace(hour=7, minute=0, second=0, microsecond=0) # but for another days leave hour 7
    start_date_looper = start_date.replace(hour=start_date_hour)
    end_date = start_date + timezone.timedelta(days=30*2) # here set time how many days to inflate

    delta = timezone.timedelta(hours=1)

    for personel in allPersonel:
        while start_date_looper < end_date:
            if start_date_looper.weekday() >= 5:
                start_date_looper += timezone.timedelta(days=1)
                continue
            t = Termin.objects.create(data=start_date_looper, status=True, personel=personel)
            t.save()
            print(f"Termin.objects.create(data={start_date_looper}, status={True}, personel={personel})")
            if start_date_looper.hour == 14:  # Check if the hour is 15:00
                start_date_looper += timezone.timedelta(days=1)  # Increment the date to the next day
                start_date_looper = start_date_looper.replace(hour=7, minute=0)  # Set the time to 8:00 on the next day
            else:
                start_date_looper += delta  # Increment the time by one hour for the next iteration
        start_date_looper = start_date




