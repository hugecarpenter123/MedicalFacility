import requests
import json

def get_method():
    data_about = input("Data about: ")
    detail_pk = input("pk: ")
    endpoint = 'http://127.0.0.1:8000/api/%s/%s' % (data_about, detail_pk)
    get_response = requests.get(endpoint)
    print(get_response)
    print(get_response.json())

def post_method():
    title = input("Give title: ")
    content = input("Give content: ")
    price = input("Give price: ")
    endpoint = 'http://127.0.0.1:8000/api/'

    data = {}
    data['title'] = title
    if content: data['content'] = content
    if price: data['price'] = price

    post_response = requests.post(endpoint, json=data)
    print(post_response)
    print(post_response.json())

def put_method():
    data_about = input("Data about: ")
    detail_pk = input("pk: ")
    status = input("status: ")
    status = True if status.strip().casefold() == "true" else False
    endpoint = 'http://127.0.0.1:8000/api/%s/%s/' % (data_about, detail_pk)

    data = {
        'status': status,
    }

    put_response = requests.put(endpoint, json=data)
    print(put_response)
    print(put_response.json())

def delete_method():
    detail_pk = input("Give pk: ")

    endpoint = 'http://127.0.0.1:8000/api/%s' % detail_pk
    post_response = requests.delete(endpoint)
    print(post_response)

request_type = input("request_type: ")
if request_type.casefold() == "get":
    get_method()
elif request_type == "post":
    post_method()
elif request_type == "put":
    put_method()
elif request_type == "delete":
    delete_method()