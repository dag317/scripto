import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { useContext, useState } from 'react';
import { Button, Text, TextInput, View } from 'react-native';
import { AuthContext } from '../context/AuthContext';
import { AuthStackParamList } from '../types/navigation';

type Props = NativeStackScreenProps<AuthStackParamList, 'Login'>;

export default function LoginScreen({ navigation }: Props) {
  const [email, setEmail] = useState<string>('');
  const [password, setPassword] = useState<string>('');
  const { login } = useContext(AuthContext);

  const handleLogin = async (): Promise<void> => {
    const response = await fetch('http://192.168.1.9:3000/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
    });

    const data: { token: string } = await response.json();
    login(data.token);
  };

  return (
    <View style={{ padding: 20 }}>
      <Text>Вход</Text>

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

      <Button title="Войти" onPress={handleLogin} />

      <Button
        title="Регистрация"
        onPress={() => navigation.navigate('Register')}
      />
    </View>
  );
}