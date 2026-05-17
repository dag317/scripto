import express from 'express';
import multer from 'multer';
import textRoutes from './textRoutes.js';
import { processOcr } from '../controllers/ocrController.js';
import { mainScreen, register, login, forgotPassword, verifyOtp, resetPassword, verifyEmail, googleAuth } from "../controllers/authController.js";

const router = express.Router();
const upload = multer();

router.post('/register', register);
router.post('/login', login);
router.post("/forgot-password", forgotPassword);
router.post("/reset-password", resetPassword);
router.post("/verify-otp", verifyOtp);
router.get("/verify", verifyEmail);
router.post('/google', googleAuth);

router.post('/ocr', upload.single('file'), processOcr);
router.use('/api/texts', textRoutes); 

export default router;