import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { useContext, useState } from 'react';
import {
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from 'react-native';
import { AuthContext } from '../context/AuthContext';
import { AuthStackParamList } from '../types/navigation';

type Props = NativeStackScreenProps<AuthStackParamList, 'Login'>;

export default function LoginScreen({ navigation }: Props) {
  const [email, setEmail] = useState<string>('');
  const [password, setPassword] = useState<string>('');
  const [error, setError] = useState<string>('');
  const { login } = useContext(AuthContext);

  const handleLogin = async (): Promise<void> => {
  setError('');
  try {
    const response = await fetch('http://192.168.1.6:3000/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
    });

    const data = await response.json();

    if (data.error) {
      if (data.error === 'User not found') {
        setError('Такого пользователя нет');
      } else if (data.error === 'Invalid password') {
        setError('Неверный пароль');
      } else {
        setError(data.error);
      }
      return;
    }

    if (!response.ok) {
      setError('Такого пользователя нет или неверный пароль');
      return;
    }

    // Если всё ок
    if (data.token) {
      login(data.token);
    } else {
      setError('Ошибка при логине');
    }
    } catch (err) {
      setError('Сетевая ошибка. Попробуйте позже.');
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

      <TouchableOpacity style={styles.loginButton} onPress={handleLogin}>
        <Text style={styles.loginText}>Войти</Text>
      </TouchableOpacity>

      <View style={styles.orContainer}>
        <View style={styles.line} />
        <Text style={styles.orText}>или</Text>
        <View style={styles.line} />
      </View>

      <TouchableOpacity
        style={styles.registerButton}
        onPress={() => navigation.navigate('Register')}
      >
        <Text style={styles.registerText}>Создать аккаунт</Text>
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
    marginBottom: 20,
  },
  input: {
    padding: 15,
    fontSize: 16,
  },
  divider: {
    height: 1,
    backgroundColor: '#ccc',
  },
  loginButton: {
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 12,
    padding: 15,
    alignItems: 'center',
    marginBottom: 20,
  },
  loginText: {
    fontSize: 16,
    fontWeight: '500',
  },
  orContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 20,
  },
  line: {
    flex: 1,
    height: 1,
    backgroundColor: '#ccc',
  },
  orText: {
    marginHorizontal: 10,
    color: '#888',
  },
  registerButton: {
    alignItems: 'center',
    padding: 15,
  },
  registerText: {
    fontSize: 16,
  },
  errorText: {
    color: 'red',
    marginBottom: 10,
    textAlign: 'center',
  },
});