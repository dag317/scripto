import { useContext } from 'react';
import { Button, Text, View } from 'react-native';
import { AuthContext } from '../context/AuthContext';

export default function HomeScreen() {
  const { logout } = useContext(AuthContext);

  return (
    <View style={{ padding: 20 }}>
      <Text>Добро пожаловать в Scripto 🚀</Text>

      <Button title="Сканировать текст" onPress={() => {}} />

      <Button title="Выйти" onPress={logout} />
    </View>
  );
}