import os
from PIL import Image
from transformers import AutoProcessor, Gemma3ForConditionalGeneration
import torch


class GemmaHandwritingOCR:
    def __init__(self, model_name: str = "google/gemma-3-4b-it"):
        self.device = "cuda" if torch.cuda.is_available() else "cpu"

        self.model = Gemma3ForConditionalGeneration.from_pretrained(
            model_name,
            torch_dtype=torch.float16 if self.device == "cuda" else torch.float32,
            device_map="auto"
        )

        self.processor = AutoProcessor.from_pretrained(model_name)

    def extract_text_from_image(self, image_path: str) -> str:
        if not os.path.exists(image_path):
            raise FileNotFoundError(f"Файл не найден: {image_path}")

        image = Image.open(image_path).convert("RGB")

        prompt = """
Ты OCR-система для распознавания русского рукописного текста.

Правила:
1. Прочитай текст на изображении.
2. Верни только распознанный текст.
3. Сохраняй переносы строк.
4. Если часть текста неразборчива, пиши [неразборчиво].
5. Не добавляй комментарии.
"""

        messages = [
            {
                "role": "user",
                "content": [
                    {"type": "image", "image": image},
                    {"type": "text", "text": prompt}
                ]
            }
        ]

        inputs = self.processor.apply_chat_template(
            messages,
            add_generation_prompt=True,
            tokenize=True,
            return_dict=True,
            return_tensors="pt"
        ).to(self.model.device)

        input_len = inputs["input_ids"].shape[-1]

        with torch.inference_mode():
            generation = self.model.generate(
                **inputs,
                max_new_tokens=512,
                do_sample=False
            )

        generation = generation[0][input_len:]
        response = self.processor.decode(generation, skip_special_tokens=True)

        return response.strip()