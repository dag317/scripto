import os

from openai import OpenAI
from dotenv import load_dotenv

load_dotenv()

API_KEY = os.getenv("YANDEX_GPT_API_KEY")

client = OpenAI(
    api_key=API_KEY,
    base_url="https://llm.api.cloud.yandex.net/v1"
)


def correct_ocr_text(text: str) -> str:
    prompt = f"""
    Ниже находится текст после OCR распознавания рукописного русского текста.

    Текст содержит:
    - ошибки OCR
    - неправильные переносы строк
    - дубликаты
    - разорванные фразы

    Твоя задача:
    1. восстановить естественный русский текст
    2. объединить строки в нормальные предложения
    3. удалить дубликаты
    4. исправить очевидные OCR ошибки

    Ничего не выдумывай.
    Верни только итоговый исправленный текст.

    OCR текст:
    {text}
    """

    response = client.chat.completions.create(
        model="gpt://b1grmkutmltcgmo7a1hj/yandexgpt-lite/latest",
        messages=[
            {
                "role": "user",
                "content": prompt
            }
        ],
        temperature=0.2
    )

    return response.choices[0].message.content.strip()