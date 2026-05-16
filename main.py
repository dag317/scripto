from ocr_gemini import GeminiHandwritingOCR


def main():
    ocr = GeminiHandwritingOCR()

    image_path = "images/ph2004.jpg"     #notebook_page.jpg

    try:
        text = ocr.extract_text_from_image(image_path)

        print("\n=== РАСПОЗНАННЫЙ ТЕКСТ ===\n")
        print(text)

    except Exception as e:
        print(f"Ошибка: {e}")


if __name__ == "__main__":
    main()

# from gemma_ocr import GemmaHandwritingOCR
#
#
# def main():
#     image_path = "images/notebook_page.jpg"
#
#     try:
#         print("Загрузка модели...")
#         ocr = GemmaHandwritingOCR()
#
#         print("Распознавание текста...")
#         text = ocr.extract_text_from_image(image_path)
#
#         print("\n=== РАСПОЗНАННЫЙ ТЕКСТ ===\n")
#         print(text)
#
#     except FileNotFoundError:
#         print(f"Файл не найден: {image_path}")
#
#     except Exception as e:
#         print(f"Ошибка: {e}")
#
#
# if __name__ == "__main__":
#     main()