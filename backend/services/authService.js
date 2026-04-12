import bcrypt from 'bcrypt';
import jwt from 'jsonwebtoken';
import { findUserByEmail, createUser } from '../models/userModel.js';

export const registerUser = async (email, password) => {
  const existingUser = await findUserByEmail(email);

  if (existingUser) {
    throw new Error('User already exists');
  }

  const hash = await bcrypt.hash(password, 10);
  const userId = await createUser(email, hash);

  return userId;
};

export const loginUser = async (email, password) => {
  const user = await findUserByEmail(email);

  if (!user) {
    throw new Error('User not found');
  }

  const isMatch = await bcrypt.compare(password, user.password_hash);

  if (!isMatch) {
    throw new Error('Invalid password');
  }

  const token = jwt.sign(
    { id: user.id },
    'SECRET_KEY',
    { expiresIn: '7d' }
  );

  return token;
};

