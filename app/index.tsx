import { Stack } from 'expo-router';
import React, { useState } from 'react';
import {
  ActivityIndicator,
  StyleSheet, Text, TextInput,
  TouchableOpacity,
  View
} from 'react-native';
import { supabase } from '../lib/supabase';

export default function Auth() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);

  async function signUpWithEmail() {
    setLoading(true);
    const { error } = await supabase.auth.signUp({ email, password });
    if (error) alert(error.message);
    else alert('Check your inbox!');
    setLoading(false);
  }

  return (
    <View style={styles.mainContainer}>
      <Stack.Screen options={{ headerShown: false }} />
      
      <View style={styles.contentContainer}>
        <View style={styles.header}>
          <Text style={styles.title}>Create Account</Text>
          <Text style={styles.subtitle}>Enter your details to get started</Text>
        </View>

        <View style={styles.form}>
          <View style={styles.inputContainer}>
            <TextInput
              style={styles.input}
              onChangeText={setEmail}
              value={email}
              placeholder="Email address"
              placeholderTextColor="#444"
              autoCapitalize="none"
            />
          </View>

          <View style={styles.inputContainer}>
            <TextInput
              style={styles.input}
              onChangeText={setPassword}
              value={password}
              secureTextEntry
              placeholder="Password"
              placeholderTextColor="#444"
            />
          </View>

          <TouchableOpacity 
            style={styles.primaryButton} 
            onPress={signUpWithEmail}
            disabled={loading}
          >
            {loading ? (
              <ActivityIndicator color="#000" />
            ) : (
              <Text style={styles.buttonText}>Continue</Text>
            )}
          </TouchableOpacity>

          <TouchableOpacity style={styles.secondaryButton}>
            <Text style={styles.secondaryButtonText}>I already have an account</Text>
          </TouchableOpacity>
        </View>
      </View>

      {/* ФИКСИРОВАННАЯ НИЖНЯЯ ПАНЕЛЬ (Синяя) */}
      <View style={styles.fixedBottomBar}>
        <View style={styles.cameraCircle}>
          <View style={styles.cameraDot} />
        </View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  mainContainer: {
    flex: 1,
    backgroundColor: '#000000',
  },
  contentContainer: {
    flex: 1,
    paddingHorizontal: 30,
    justifyContent: 'center',
    marginBottom: 100,
  },
  header: {
    marginBottom: 40,
  },
  title: {
    fontSize: 34,
    fontWeight: '700',
    color: '#FFFFFF',
    letterSpacing: -1,
  },
  subtitle: {
    fontSize: 16,
    color: '#666',
    marginTop: 8,
  },
  form: {
    gap: 15,
  },
  inputContainer: {
    backgroundColor: '#0A0A0A',
    borderRadius: 18,
    height: 64,
    justifyContent: 'center',
    paddingHorizontal: 20,
    borderWidth: 1,
    borderColor: '#151515',
  },
  input: {
    color: '#FFFFFF',
    fontSize: 16,
  },
  primaryButton: {
    backgroundColor: '#FFFFFF', // Кнопка белая для минимализма
    height: 60,
    borderRadius: 30,
    alignItems: 'center',
    justifyContent: 'center',
    marginTop: 20,
  },
  buttonText: {
    color: '#000',
    fontSize: 16,
    fontWeight: '700',
  },
  secondaryButton: {
    marginTop: 15,
    alignItems: 'center',
  },
  secondaryButtonText: {
    color: '#444',
    fontSize: 14,
  },
  fixedBottomBar: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    height: 100,
    backgroundColor: 'rgba(5, 5, 5, 0.95)',
    alignItems: 'center',
    justifyContent: 'center',
    borderTopWidth: 1,
    borderTopColor: '#111',
  },
  cameraCircle: {
     width: 54,
     height: 54,
     borderRadius: 27,
     backgroundColor: '#2A5CFF', // СИНИЙ АКЦЕНТ
     alignItems: 'center',
     justifyContent: 'center',
     shadowColor: '#2A5CFF',
     shadowRadius: 15,
     shadowOpacity: 0.4,
  },
  cameraDot: {
     width: 16,
     height: 16,
     borderRadius: 8,
     backgroundColor: '#FFFFFF' // Белая точка внутри синего
  }
});
