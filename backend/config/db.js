import mysql from 'mysql2/promise';

export const db = mysql.createPool({
  host: 'localhost',
  user: 'david',
  password: 'AN0660193s',
  database: 'scripto'
});