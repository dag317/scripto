import express from 'express';
import { getUserTexts, createText, deleteText } from '../controllers/textController.js';
import { authMiddleware } from '../middleware/authMiddleware.js';

const router = express.Router();

router.get('/', authMiddleware, getUserTexts);
router.post('/', authMiddleware, createText);
router.delete('/:id', authMiddleware, deleteText);

export default router;
