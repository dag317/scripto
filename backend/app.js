import express from 'express';
import cors from 'cors';
import authRoutes from './routes/authRoutes.js';
import { mainScreen } from './controllers/authController.js';
import textRoutes from './routes/textRoutes.js';

const app = express();

app.use(cors());
app.use(express.json());
app.use('/api/texts', textRoutes);
app.use('/auth', authRoutes);
app.get("/", mainScreen);
export default app;