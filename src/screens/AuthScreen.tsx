import React, { useState } from 'react';
import { Alert, Button, StyleSheet, Text, TextInput, View } from 'react-native';
import { supabase } from '../../lib/supabase';

export default function AuthScreen() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleSignIn = async () => {
    const { error } = await supabase.auth.signInWithPassword({ email, password });
    if (error) Alert.alert('Ошибка', error.message);
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Вход</Text>
      <TextInput style={styles.input} placeholder="Email" value={email} onChangeText={setEmail} autoCapitalize="none" />
      <TextInput style={styles.input} placeholder="Пароль" value={password} onChangeText={setPassword} secureTextEntry />
      <Button title="Войти" onPress={handleSignIn} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, justifyContent: 'center', padding: 20 },
  title: { fontSize: 24, textAlign: 'center', marginBottom: 20 },
  input: { borderWidth: 1, borderColor: '#ccc', padding: 10, marginBottom: 15, borderRadius: 5 }
});