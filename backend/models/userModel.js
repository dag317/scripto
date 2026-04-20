import { db } from '../config/db.js';

export const findUserByEmail = async (email) => {
  const [rows] = await db.query(
    'SELECT * FROM users WHERE email = ?',
    [email]
  );
  return rows[0];
};

export const createUser = async (email, passwordHash, token) => {
  const [result] = await db.query(
    'INSERT INTO users (email, password_hash, verification_token, verified) VALUES (?, ?, ?, 0)',
    [email, passwordHash, token]
  );
  return result.insertId;
};

export const setUserVerified = async (email) => {
  const query = 'UPDATE users SET verified = 1, verification_token = NULL WHERE email = ?';
  const [result] = await db.execute(query, [email]);
  return result.affectedRows > 0;
};