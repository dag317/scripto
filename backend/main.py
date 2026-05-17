import sys
from ocr import (
    recognize_handwritten_text,
    extract_text
)
from llm_correction import correct_ocr_text

if len(sys.argv) > 1:
    IMAGE_PATH = sys.argv[1]
else:
    IMAGE_PATH = "samples/test.jpg"


def main():
    result = recognize_handwritten_text(IMAGE_PATH)
    raw_text = extract_text(result)

    print("=== RAW OCR ===")
    print(raw_text)

    print("=== LLM CORRECTION ===")
    corrected_text = correct_ocr_text(raw_text)
    print(corrected_text)


if __name__ == "__main__":
    main()