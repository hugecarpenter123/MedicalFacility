import hashlib

pwd = 'GeeksPassword'
hashed_pwd = hashlib.md5(pwd.encode()).hexdigest()

if hashlib.md5(pwd.encode()).hexdigest() == hashed_pwd:
    print("to samo")