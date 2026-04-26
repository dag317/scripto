import express from 'express';
import { mainScreen, register, login, forgotPassword, verifyOtp, resetPassword, verifyEmail, googleAuth } from "../controllers/authController.js";

const router = express.Router();

router.post('/register', register);
router.post('/login', login);
router.post("/forgot-password", forgotPassword);
router.post("/reset-password", resetPassword);
router.post("/verify-otp", verifyOtp);
router.get("/verify", verifyEmail);
router.get("/", mainScreen);
router.post('/google', googleAuth);

export default router;