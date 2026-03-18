import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { useState } from 'react';
import { Button, Text, TextInput, View } from 'react-native';
import { AuthStackParamList } from '../types/navigation';

type Props = NativeStackScreenProps<AuthStackParamList, 'Register'>;

export default function RegisterScreen({ navigation }: Props) {
  const [email, setEmail] = useState<string>('');
  const [password, setPassword] = useState<string>('');

  const handleRegister = async (): Promise<void> => {
    try {
      const response = await fetch('http://192.168.1.9:3000/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      });

      const data = await response.json();

      console.log(data);

      // после регистрации — возвращаемся на логин
      navigation.navigate('Login');
    } catch (error) {
      console.error('Register error:', error);
    }
  };

  return (
    <View style={{ padding: 20 }}>
      <Text>Регистрация</Text>

      <TextInput
        placeholder="Email"
        value={email}
        onChangeText={setEmail}
      />

      <TextInput
        placeholder="Пароль"
        secureTextEntry
        value={password}
        onChangeText={setPassword}
      />

      <Button title="Зарегистрироваться" onPress={handleRegister} />

      <Button
        title="Уже есть аккаунт? Войти"
        onPress={() => navigation.navigate('Login')}
      />
    </View>
  );
}