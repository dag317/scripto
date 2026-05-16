import os
import shutil

from fastapi import FastAPI, UploadFile, File

from ocr import (
    recognize_handwritten_text,
    extract_text
)

from llm_correction import correct_ocr_text


app = FastAPI()


UPLOAD_DIR = "uploads"

os.makedirs(UPLOAD_DIR, exist_ok=True)


@app.get("/")
def root():
    return {
        "status": "OCR server working"
    }


@app.post("/ocr")
async def process_ocr(
    file: UploadFile = File(...)
):

    file_path = os.path.join(
        UPLOAD_DIR,
        file.filename
    )

    # Сохраняем изображение
    with open(file_path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)

    # OCR
    result = recognize_handwritten_text(file_path)

    raw_text = extract_text(result)

    # LLM correction
    corrected_text = correct_ocr_text(raw_text)

    return {
        "raw_text": raw_text,
        "corrected_text": corrected_text
    }