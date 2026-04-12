import { registerUser, loginUser } from '../services/authService.js';
import { sendOtp, checkOtp, updatePassword } from "../services/otpService.js";

export const register = async (req, res) => {
  try {
    const { email, password } = req.body;

    const userId = await registerUser(email, password);

    res.json({ userId });
  } catch (error) {
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
      res.status(400).json({ error: "Email required" });
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