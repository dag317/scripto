import base64
import json
import os
import requests
from dotenv import load_dotenv

load_dotenv()

# Меняем старый токен на вечный OAuth-токен
OAUTH_TOKEN = os.getenv("YANDEX_OAUTH_TOKEN")
FOLDER_ID = os.getenv("YANDEX_FOLDER_ID")

OCR_URL = "https://ocr.api.cloud.yandex.net/ocr/v1/recognizeText"
IAM_URL = "https://yandex.net"

# Функция, которая на лету берет свежий IAM-токен перед каждым распознаванием
def get_fresh_iam_token():
    try:
        payload = {"yandexPassportOauthToken": OAUTH_TOKEN}
        response = requests.post(IAM_URL, json=payload)
        response.raise_for_status()
        return response.json().get("iamToken")
    except Exception as e:
        print(f"Ошибка получения IAM токена в Python: {e}")
        raise

def encode_file(file_path: str) -> str:
    with open(file_path, "rb") as f:
        return base64.b64encode(f.read()).decode("utf-8")

def recognize_handwritten_text(image_path: str):
    iam_token = get_fresh_iam_token()
    
    content = encode_file(image_path)

    payload = {
        "mimeType": "JPEG",
        "languageCodes": ["ru", "en"],
        "model": "handwritten",
        "content": content
    }

    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {iam_token}",
        "x-folder-id": FOLDER_ID
    }

    response = requests.post(
        OCR_URL,
        headers=headers,
        data=json.dumps(payload)
    )

    response.raise_for_status()
    return response.json()

def extract_text(data):
    return (
        data.get("result", {})
        .get("textAnnotation", {})
        .get("fullText", "")
        .strip()
    )
