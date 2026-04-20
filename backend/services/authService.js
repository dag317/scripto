import bcrypt from 'bcrypt';
import jwt from 'jsonwebtoken';
import { findUserByEmail, createUser, setUserVerified } from '../models/userModel.js';
import { sendVerificationEmail } from './mailService.js';
import { OAuth2Client } from 'google-auth-library';

export const ACCESS_SECRET = 'ScriptoLoginSecret';
export const EMAIL_SECRET = 'ScriptoEmaiSecret';
const GOOGLE_CLIENT_ID = '365323608098-a9du3snf98ovv5eqbmpspivo9l4un0vh.apps.googleusercontent.com';
const client = new OAuth2Client(GOOGLE_CLIENT_ID);

export const googleLogin = async (idToken) => {
  const ticket = await client.verifyIdToken({
      idToken,
      audience: GOOGLE_CLIENT_ID,
  });
  
  const payload = ticket.getPayload();
  const email = payload.email;

  let user = await findUserByEmail(email);

  if (!user) {
    const randomPass = Math.random().toString(36) + Math.random().toString(36);
    const hash = await bcrypt.hash(randomPass, 10);
    
    const userId = await createUser(email, hash, null);
    
    await setUserVerified(email);
    
    user = { id: userId, email: email };
  }

  const userIdForToken = user.id || user[0]?.id; 
  
  return jwt.sign({ id: userIdForToken }, ACCESS_SECRET, { expiresIn: '7d' });
};

export const registerUser = async (email, password) => {
  const existingUser = await findUserByEmail(email);
  if (existingUser) throw new Error('User already exists');

  const hash = await bcrypt.hash(password, 10);
  
  const verificationToken = jwt.sign({ email }, EMAIL_SECRET, { expiresIn: '1d' });

  const userId = await createUser(email, hash, verificationToken);
  
  await sendVerificationEmail(email, verificationToken);

  return userId;
};

export const confirmEmail = async (token) => {
  try {
    const decoded = jwt.verify(token, EMAIL_SECRET);
    
    const isUpdated = await setUserVerified(decoded.email);
    
    if (!isUpdated) throw new Error('User not found');
    return true;
  } catch (error) {
    throw new Error('Invalid or expired token');
  }
};

export const loginUser = async (email, password) => {
  const user = await findUserByEmail(email);

  if (!user) throw new Error('User not found');
  
  if (!user.verified) {
    throw new Error('Please verify your email first');
  }

  const isMatch = await bcrypt.compare(password, user.password_hash);
  if (!isMatch) throw new Error('Invalid password');

  return jwt.sign({ id: user.id }, ACCESS_SECRET, { expiresIn: '7d' });
};
