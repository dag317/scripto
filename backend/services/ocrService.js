import axios from 'axios';
import dotenv from 'dotenv';

dotenv.config();

const OAUTH_TOKEN = process.env.YANDEX_OAUTH_TOKEN;
const FOLDER_ID = process.env.YANDEX_FOLDER_ID;
const GPT_API_KEY = process.env.YANDEX_GPT_API_KEY;

const getFreshIamToken = async () => {
  try {
    const response = await axios.post('https://yandex.net', {
      yandexPassportOauthToken: OAUTH_TOKEN
    });
    return response.data.iamToken;
  } catch (error) {
    console.error("Ошибка получения IAM токена:", error.response?.data || error.message);
    throw new Error("Не удалось авторизоваться в Yandex Cloud");
  }
};

export const recognizeHandwrittenText = async (imageBuffer) => {
  const OCR_URL = "https://yandex.net";
  
  const iamToken = await getFreshIamToken();

  const contentBase64 = imageBuffer.toString("base64");

  const payload = {
    mimeType: "JPEG",
    languageCodes: ["ru", "en"],
    model: "handwritten",
    content: contentBase64
  };

  const response = await axios.post(OCR_URL, payload, {
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${iamToken}`,
      "x-folder-id": FOLDER_ID
    }
  });

  return response.data?.result?.textAnnotation?.fullText?.trim() || "";
};

export const correctOcrText = async (text) => {
  const GPT_URL = "https://yandex.net";

  const prompt = `
  Ниже находится text после OCR распознавания рукописного русского текста.
  Текст содержит: ошибки OCR, неправильные переносы строк, дубликаты, разорванные фразы.
  Твоя задача:
  1. восстановить естественный русский текст
  2. объединить строки в нормальные предложения
  3. удалить дубликаты
  4. исправить очевидные OCR ошибки
  Ничего не выдумывай. Верни только итоговый исправленный текст.
  OCR текст:
  ${text}
  `;

  const payload = {
    model: "gpt://b1grmkutmltcgmo7a1hj/yandexgpt-lite/latest",
    messages: [
      {
        role: "user",
        content: prompt
      }
    ],
    temperature: 0.2
  };

  const response = await axios.post(GPT_URL, payload, {
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Api-Key ${GPT_API_KEY}`
    }
  });

  return response.data?.choices?.[0]?.message?.content?.trim() || "";
};
