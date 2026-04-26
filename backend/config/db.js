import mysql from 'mysql2/promise';

export const db = mysql.createPool({
  host: '127.0.0.1',
  user: 'root',
  password: 'AN0660193s',
  database: 'scripto_db',
  waitForConnections: true,   
  connectionLimit: 10,         
  queueLimit: 0,             
  enableKeepAlive: true,     
  keepAliveInitialDelay: 10000
});