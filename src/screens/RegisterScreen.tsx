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
  const [error, setError] = useState<string>('');

  const handleRegister = async (): Promise<void> => {
    setError('');

    if (!email.includes('@')) {
      setError('Введите корректный email');
      return;
    }

    if (password.length < 6) {
      setError('Пароль должен быть не менее 6 символов');
      return;
    }

    try {
      const response = await fetch('http://192.168.1.6:3000/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      });

      const data = await response.json();

      if (!response.ok) {
        setError(data.message || 'Ошибка регистрации');
        return;
      }

      navigation.navigate('Login');
    } catch (err) {
      setError('Ошибка сети. Попробуйте позже');
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Scripto</Text>

      <View style={styles.inputContainer}>
        <TextInput
          placeholder="Email"
          value={email}
          onChangeText={setEmail}
          style={styles.input}
          placeholderTextColor="#888"
        />
        <View style={styles.divider} />
        <TextInput
          placeholder="Пароль"
          secureTextEntry
          value={password}
          onChangeText={setPassword}
          style={styles.input}
          placeholderTextColor="#888"
        />
      </View>

      {error ? <Text style={styles.errorText}>{error}</Text> : null}

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
  divider: {
    height: 1,
    backgroundColor: '#ccc',
  },
  errorText: {
    color: 'red',
    marginBottom: 10,
    textAlign: 'center',
  },
  mainButton: {
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 12,
    padding: 15,
    alignItems: 'center',
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