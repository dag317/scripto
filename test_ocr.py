from ocr import (
    recognize_handwritten_text,
    extract_text
)

from llm_correction import correct_ocr_text

IMAGE_PATH = "samples/test.jpg"


def main():

    print("Отправляем изображение в OCR...\n")

    result = recognize_handwritten_text(IMAGE_PATH)

    raw_text = extract_text(result)

    print("=== RAW OCR ===\n")
    print(raw_text)

    print("\n=== LLM CORRECTION ===\n")

    corrected_text = correct_ocr_text(raw_text)

    print(corrected_text)


if __name__ == "__main__":
    main()