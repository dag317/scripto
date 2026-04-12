import { db } from "../config/db.js";
import bcrypt from "bcrypt";
import { sendOtpEmail } from "./mailService.js";

export const sendOtp = async (email) => {
  const otp = Math.floor(100000 + Math.random() * 900000).toString();

  const hash = await bcrypt.hash(otp, 10);
  const expires = Date.now() + 5 * 60 * 1000;

  await db.query("DELETE FROM codes WHERE email = ?", [email]);

  await db.query(
    "INSERT INTO codes (email, code_hash, expires_at, attempts) VALUES (?, ?, ?, 0)",
    [email, hash, expires]
  );

  await sendOtpEmail(email, otp);
  
};
export const updatePassword = async (email, newPassword) => {
  const hash = await bcrypt.hash(newPassword, 10);

  await db.query(
    "UPDATE users SET password_hash = ? WHERE email = ?",
    [hash, email]
  );
};
export const checkOtp = async (email, code) => {
  const [rows] = await db.query(
    "SELECT * FROM codes WHERE email = ?",
    [email]
  );

  if (!rows.length) throw new Error("Invalid code");

  const record = rows[0];

  if (Date.now() > record.expires_at) throw new Error("Expired");

  const valid = await bcrypt.compare(code, record.code_hash);

  if (!valid) {
    await db.query(
      "UPDATE codes SET attempts = attempts + 1 WHERE email = ?",
      [email]
    );
    throw new Error("Wrong code");
  }

  await db.query("DELETE FROM codes WHERE email = ?", [email]);

  return { success: true };
};