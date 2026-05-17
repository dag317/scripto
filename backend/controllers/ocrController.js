import { exec } from 'child_process';
import fs from 'fs';
import path from 'path';

export const processOcr = async (req, res) => {
  try {
    if (!req.file) {
      return res.status(400).json({ error: "Файл не загружен" });
    }

    const tempDir = path.join(process.cwd(), 'uploads');
    if (!fs.existsSync(tempDir)){
        fs.mkdirSync(tempDir);
    }
    
    const imagePath = path.join(tempDir, `temp_${Date.now()}.jpg`);
    fs.writeFileSync(imagePath, req.file.buffer);

    const pythonScriptPath = '/root/scripto/backend/main.py'; 

    exec(`python3 ${pythonScriptPath} ${imagePath}`, (error, stdout, stderr) => {
      if (fs.existsSync(imagePath)) fs.unlinkSync(imagePath);

      if (error) {
        console.error(`Ошибка запуска скрипта Python: ${error.message}`);
        return res.status(500).json({ error: "Ошибка на стороне скрипта распознавания" });
      }

      const outputLines = stdout.trim().split('\n');
      
      const rawTextIndex = outputLines.indexOf('=== RAW OCR ===');
      const llmTextIndex = outputLines.indexOf('=== LLM CORRECTION ===');

      let rawText = "";
      let correctedText = "";

      if (rawTextIndex !== -1 && llmTextIndex !== -1) {
         rawText = outputLines.slice(rawTextIndex + 1, llmTextIndex).join('\n').trim();
         correctedText = outputLines.slice(llmTextIndex + 1).join('\n').trim();
      } else {
         correctedText = stdout.trim();
      }

      return res.json({
        raw_text: rawText,
        corrected_text: correctedText
      });
    });

  } catch (error) {
    console.error("Ошибка OCR контроллера:", error.message);
    return res.status(500).json({ error: "Внутренняя ошибка сервера" });
  }
};