import os
from PIL import Image
from dotenv import load_dotenv
import google.generativeai as genai


load_dotenv()


class GeminiHandwritingOCR:
    def __init__(self, model_name: str = "gemini-2.5-flash"):
        api_key = os.getenv("GEMINI_API_KEY")

        if not api_key:
            raise ValueError(
                "Не найден GEMINI_API_KEY. Добавьте его в .env файл"
            )

        genai.configure(api_key=api_key)
        self.model = genai.GenerativeModel(model_name)

    def _load_image(self, image_path: str) -> Image.Image:
        """
        Загружает изображение.
        """
        if not os.path.exists(image_path):
            raise FileNotFoundError(f"Файл не найден: {image_path}")

        image = Image.open(image_path)
        return image

    def extract_text_from_image(
        self,
        image_path: str,
        keep_line_breaks: bool = True,
        return_only_text: bool = True,
    ) -> str:
        """
        Распознаёт русский рукописный текст с изображения.

        Args:
            image_path: путь к изображению
            keep_line_breaks: сохранять переносы строк
            return_only_text: возвращать только текст без пояснений

        Returns:
            Распознанный текст
        """

        image = self._load_image(image_path)

        prompt = """
Ты OCR-система для распознавания русского рукописного текста.

Задача:
1. Внимательно прочитай рукописный текст на фотографии.
2. Верни только распознанный русский текст.
3. Сохраняй абзацы и переносы строк.
4. Не добавляй комментарии, объяснения и свои догадки.
5. Если часть слова неразборчива, используй [неразборчиво].
6. Если текст отсутствует, верни: Текст не найден.
7. Игнорируй фон, линии тетради, рисунки и лишние объекты.
"""

        if not keep_line_breaks:
            prompt += "\n8. Верни текст одной строкой."

        if return_only_text:
            prompt += "\n9. Ответ должен содержать только итоговый текст."

        response = self.model.generate_content([
            prompt,
            image
        ])

        return response.text.strip()

    def extract_text_with_confidence_hint(self, image_path: str) -> dict:
        """
        Возвращает текст и примерную оценку качества распознавания.
        """

        image = self._load_image(image_path)

        prompt = """
Ты OCR-система для распознавания русского рукописного текста.

Верни ответ строго в формате:

TEXT:
<распознанный текст>

QUALITY:
<high / medium / low>

Правила:
- high: текст читается хорошо
- medium: есть отдельные неразборчивые слова
- low: текст читается плохо
- Не добавляй ничего кроме указанного формата
"""

        response = self.model.generate_content([
            prompt,
            image
        ])

        result_text = response.text.strip()

        text_value = ""
        quality_value = "unknown"

        if "QUALITY:" in result_text:
            parts = result_text.split("QUALITY:")
            text_part = parts[0].replace("TEXT:", "").strip()
            quality_part = parts[1].strip().lower()

            text_value = text_part
            quality_value = quality_part
        else:
            text_value = result_text

        return {
            "text": text_value,
            "quality": quality_value,
        }


