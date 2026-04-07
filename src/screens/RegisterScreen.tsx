import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { useState } from 'react';
import {
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from 'react-native';
import { AuthStackParamList } from '../types/navigation';

type Props = NativeStackScreenProps<AuthStackParamList, 'Register'>;

export default function RegisterScreen({ navigation }: Props) {
  const [email, setEmail] = useState<string>('');
  const [password, setPassword] = useState<string>('');

  const [emailError, setEmailError] = useState<string>('');
  const [passwordError, setPasswordError] = useState<string>('');
  const [serverError, setServerError] = useState<string>('');

  // 📧 Валидация email
  const validateEmail = (value: string): string => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!value) return '';
    if (!emailRegex.test(value)) return 'Введите корректный email';

    return '';
  };

  // 🔒 Валидация пароля
  const validatePassword = (value: string): string => {
    if (!value) return '';
    if (value.length < 6) return 'Минимум 6 символов';

    return '';
  };

  const handleRegister = async (): Promise<void> => {
    const emailValidation = validateEmail(email);
    const passwordValidation = validatePassword(password);

    if (emailValidation || passwordValidation) {
      setEmailError(emailValidation);
      setPasswordError(passwordValidation);
      return;
    }

    setServerError('');

    try {
      const response = await fetch('http://192.168.1.6:3000/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      });

      const data = await response.json();

      if (!response.ok) {
        setServerError(data.message || 'Ошибка регистрации');
        return;
      }

      navigation.navigate('Login');
    } catch (err) {
      setServerError('Ошибка сети. Попробуйте позже');
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Scripto</Text>

      <View style={styles.inputContainer}>
        {/* 📧 EMAIL */}
        <TextInput
          placeholder="Email"
          value={email}
          onChangeText={(text) => {
            setEmail(text);
            setEmailError(validateEmail(text));
          }}
          style={[
            styles.input,
            emailError ? styles.inputError : null,
          ]}
          placeholderTextColor="#888"
        />

        <View style={styles.divider} />

        {/* 🔒 PASSWORD */}
        <TextInput
          placeholder="Пароль"
          secureTextEntry
          value={password}
          onChangeText={(text) => {
            setPassword(text);
            setPasswordError(validatePassword(text));
          }}
          style={[
            styles.input,
            passwordError ? styles.inputError : null,
          ]}
          placeholderTextColor="#888"
        />
      </View>

      {/* Ошибки под полями */}
      {emailError ? <Text style={styles.errorText}>{emailError}</Text> : null}
      {passwordError ? <Text style={styles.errorText}>{passwordError}</Text> : null}
      {serverError ? <Text style={styles.errorText}>{serverError}</Text> : null}

      <TouchableOpacity style={styles.mainButton} onPress={handleRegister}>
        <Text style={styles.mainText}>Зарегистрироваться</Text>
      </TouchableOpacity>

      <TouchableOpacity onPress={() => navigation.navigate('Login')}>
        <Text style={styles.linkText}>Уже есть аккаунт? Войти</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    paddingHorizontal: 20,
    backgroundColor: '#fff',
  },
  title: {
    fontSize: 32,
    textAlign: 'center',
    fontWeight: '500',
    marginTop: 100,
    marginBottom: 40,
  },
  inputContainer: {
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 12,
    overflow: 'hidden',
    marginBottom: 10,
  },
  input: {
    padding: 15,
    fontSize: 16,
  },
  inputError: {
    borderColor: 'red',
  },
  divider: {
    height: 1,
    backgroundColor: '#ccc',
  },
  errorText: {
    color: 'red',
    marginBottom: 5,
    textAlign: 'center',
  },
  mainButton: {
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 12,
    padding: 15,
    alignItems: 'center',
    marginTop: 10,
    marginBottom: 20,
  },
  mainText: {
    fontSize: 16,
    fontWeight: '500',
  },
  linkText: {
    textAlign: 'center',
    fontSize: 16,
  },
});