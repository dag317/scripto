import React, { useState } from 'react';
import { Alert, AppState, Button, StyleSheet, Text, TextInput, View } from 'react-native';
import { supabase } from '../lib/supabase';

// Позволяет Supabase обновлять сессию в фоновом режиме
AppState.addEventListener('change', (state) => {
  if (state === 'active') {
    supabase.auth.startAutoRefresh();
  } else {
    supabase.auth.stopAutoRefresh();
  }
});

export default function Auth() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);

  async function signUpWithEmail() {
    setLoading(true);
    const { error } = await supabase.auth.signUp({
      email: email,
      password: password,
    });

    if (error) Alert.alert('Ошибка!', error.message);
    else Alert.alert('Успех!', 'Проверь почту для подтверждения регистрации!');
    setLoading(false);
  }

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Регистрация</Text>
      <TextInput
        style={styles.input}
        onChangeText={(text) => setEmail(text)}
        value={email}
        placeholder="email@address.com"
        autoCapitalize={'none'}
      />
      <TextInput
        style={styles.input}
        onChangeText={(text) => setPassword(text)}
        value={password}
        secureTextEntry={true}
        placeholder="Пароль"
        autoCapitalize={'none'}
      />
      <Button title="Зарегистрироваться" disabled={loading} onPress={() => signUpWithEmail()} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { marginTop: 40, padding: 20 },
  title: { fontSize: 20, fontWeight: 'bold', marginBottom: 20, textAlign: 'center' },
  input: {
    height: 50,
    borderColor: 'gray',
    borderWidth: 1,
    marginBottom: 12,
    padding: 10,
    borderRadius: 8,
  },
});