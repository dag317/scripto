import sqlite3
import hashlib
import re

# Настройка базы данных
def init_db():
    db = sqlite3.connect('users.db')
    cursor = db.cursor()
    cursor.execute('''CREATE TABLE IF NOT EXISTS users 
                      (id INTEGER PRIMARY KEY, email TEXT UNIQUE, password TEXT)''')
    db.commit()
    return db

# Проверки (Валидация)
def is_valid_email(email):
    return re.match(r"[^@]+@[^@]+\.[^@]+", email)

def is_strong_password(password):
    # Минимум 8 символов, одна цифра и одна заглавная буква
    return (len(password) >= 8 and 
            any(char.isdigit() for char in password) and 
            any(char.isupper() for char in password))

# Хеширование
def hash_password(password):
    return hashlib.sha256(password.encode()).hexdigest()

# Регистрация
def register_user(email, password):
    if not email or not password:
        return "Ошибка: Поля не могут быть пустыми."
    if not is_valid_email(email):
        return "Ошибка: Неверный формат email."
    if not is_strong_password(password):
        return "Ошибка: Пароль слишком простой (нужно 8+ симв., цифра и заглавная буква)."

    hashed_pw = hash_password(password)
    
    try:
        db = sqlite3.connect('users.db')
        cursor = db.cursor()
        cursor.execute("INSERT INTO users (email, password) VALUES (?, ?)", (email, hashed_pw))
        db.commit()
        return "Успешная регистрация!"
    except sqlite3.IntegrityError:
        return "Ошибка: Пользователь с таким email уже существует."

# Авторизация
def login_user(email, password):
    hashed_pw = hash_password(password)
    db = sqlite3.connect('users.db')
    cursor = db.cursor()
    cursor.execute("SELECT * FROM users WHERE email = ? AND password = ?", (email, hashed_pw))
    
    if cursor.fetchone():
        return "Вход выполнен успешно!"
    else:
        return "Ошибка: Неверный email или пароль."