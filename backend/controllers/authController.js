import { registerUser, loginUser, confirmEmail, googleLogin } from '../services/authService.js';
import { sendOtp, checkOtp, updatePassword } from "../services/otpService.js";
import { findUserByEmail } from '../models/userModel.js';

export const register = async (req, res) => {
  try {
    const { email, password } = req.body;

    const userId = await registerUser(email, password);

    res.json({ userId });
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
};

export const googleAuth = async (req, res) => {
  try {
    const { idToken } = req.body;

    if (!idToken) {
      return res.status(400).json({ error: "idToken is required" });
    }

    const token = await googleLogin(idToken);

    res.json({ token });
  } catch (error) {
    console.error("GOOGLE AUTH ERROR:", error);
    res.status(400).json({ error: error.message });
  }
};

export const login = async (req, res) => {
  try {
    const { email, password } = req.body;

    const token = await loginUser(email, password);

    res.json({ token });
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
};

export const forgotPassword = async (req, res) => {
  try {
    const { email } = req.body;

    if (!email) {
      return res.status(400).json({ error: "Email required" });
    }

    const user = await findUserByEmail(email);
    if (!user) {
      return res.status(404).json({ error: "User not found" });
    }

    await sendOtp(email);

    res.json({ message: "OTP sent" });
  } catch (e) {
    console.log("FORGOT PASSWORD ERROR:", e);
    res.status(500).json({ error: e.message });
  }
};

export const verifyOtp = async (req, res) => {
  try {
    const { email, code } = req.body;

    const result = await checkOtp(email, code);

    res.json(result);
  } catch (e) {
    res.status(400).json({ error: e.message });
  }
};

export const resetPassword = async (req, res) => {
  try {
    const { email, newPassword } = req.body;

    await updatePassword(email, newPassword);

    res.json({ success: true });
  } catch (e) {
    res.status(400).json({ error: e.message });
  }
};

export const verifyEmail = async (req, res) => {
  try {
    const { token } = req.query;
    if (!token) return res.status(400).send("Токен отсутствует");

    await confirmEmail(token);
    
    res.send(`
      <!DOCTYPE html>
      <html>
      <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Почта подтверждена</title>
      </head>
      <body style="background-color: #f4f7f6; display: flex; align-items: center; justify-content: center; height: 100vh; margin: 0; font-family: sans-serif;">
        <div style="background: white; padding: 40px; border-radius: 20px; box-shadow: 0 10px 25px rgba(0,0,0,0.05); text-align: center; max-width: 400px; width: 90%;">
          <div style="font-size: 60px; margin-bottom: 20px;">✅</div>
          <h1 style="color: #3F51B5; margin-bottom: 15px; font-size: 22px;">Почта подтверждена!</h1>
          <p style="color: #666; line-height: 1.6; margin-bottom: 25px;">
            Ваш аккаунт успешно активирован. Теперь вы можете вернуться в приложение <b>Scripto</b> и войти в свой профиль.
          </p>
          <div style="height: 4px; background: linear-gradient(90deg, #3F51B5, #5C6BC0); border-radius: 2px; width: 50px; margin: 0 auto;"></div>
        </div>
      </body>
      </html>
    `);
  } catch (error) {
    res.status(400).send(`
      <div style="text-align: center; margin-top: 50px; font-family: sans-serif; color: #d32f2f;">
        <h2>Ошибка верификации</h2>
        <p>${error.message}</p>
        <a href="#" onclick="window.close()" style="color: #3F51B5;">Закрыть страницу</a>
      </div>
    `);
  }
};