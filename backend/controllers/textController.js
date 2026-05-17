import { db } from '../config/db.js';

export const getUserTexts = async (req, res) => {
  try {
    const userId = req.user.id;
    const [rows] = await db.query(
      'SELECT * FROM user_texts WHERE user_id = ? ORDER BY created_at DESC', 
      [userId]
    );
    res.json(rows);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
};

export const createText = async (req, res) => {
  try {
    const userId = req.user.id;
    const { title, content } = req.body;
    
    if (!title || !content) {
      return res.status(400).json({ message: "Заголовок и текст не могут быть пустыми" });
    }

    const [result] = await db.query(
      'INSERT INTO user_texts (user_id, title, content) VALUES (?, ?, ?)', 
      [userId, title, content]
    );
    res.status(201).json({ id: result.insertId, message: "Текст добавлен в архив" });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
};

export const deleteText = async (req, res) => {
  try {
    const userId = req.user.id;
    const textId = req.params.id;

    const [result] = await db.query(
      'DELETE FROM user_texts WHERE id = ? AND user_id = ?', 
      [textId, userId]
    );

    if (result.affectedRows === 0) {
      return res.status(404).json({ message: "Текст не найден или доступ запрещен" });
    }

    res.json({ message: "Текст успешно удален" });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
};
