import base64
import json
import os

import requests
from dotenv import load_dotenv

load_dotenv()

IAM_TOKEN = os.getenv("YANDEX_IAM_TOKEN")
FOLDER_ID = os.getenv("YANDEX_FOLDER_ID")

OCR_URL = "https://ocr.api.cloud.yandex.net/ocr/v1/recognizeText"


def encode_file(file_path: str) -> str:
    with open(file_path, "rb") as f:
        return base64.b64encode(f.read()).decode("utf-8")


def recognize_handwritten_text(image_path: str):

    content = encode_file(image_path)

    payload = {
        "mimeType": "JPEG",
        "languageCodes": ["ru", "en"],
        "model": "handwritten",
        "content": content
    }

    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {IAM_TOKEN}",
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