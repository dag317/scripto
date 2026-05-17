import { recognizeHandwrittenText, correctOcrText } from '../services/ocrService.js';

export const processOcr = async (req, res) => {
  try {
    if (!req.file) {
      return res.status(400).json({ error: "Файл не загружен" });
    }

    const rawText = await recognizeHandwrittenText(req.file.buffer);

    if (!rawText) {
      return res.json({ raw_text: "", corrected_text: "" });
    }

    const correctedText = await correctOcrText(rawText);

    return res.json({
      raw_text: rawText,
      corrected_text: correctedText
    });

  } catch (error) {
    console.error("Ошибка OCR контроллера:", error.message);
    return res.status(500).json({ error: "Ошибка при обработке изображения" });
  }
};
