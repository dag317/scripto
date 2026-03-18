import AsyncStorage from '@react-native-async-storage/async-storage';
import { router, useLocalSearchParams } from 'expo-router';
import * as Speech from 'expo-speech';
import { useEffect, useState } from 'react';
import { ActivityIndicator, Alert, Image, StyleSheet, Text, TouchableOpacity, View } from 'react-native';

export default function ResultScreen() {
  const { imageUri } = useLocalSearchParams();
  const [recognizedText, setRecognizedText] = useState('');
  const [loading, setLoading] = useState(false);

  const recognizeText = async () => {
    setLoading(true);
    try {
      // Имитация распознавания – замените на вызов вашего бэкенда
      await new Promise(resolve => setTimeout(resolve, 2000));
      const mockText = 'А.П. Чехов — рассказ «Человек в футляре»\nцентральный персонаж — учитель греческого языка Беликов...';
      setRecognizedText(mockText);
    } catch {
      Alert.alert('Ошибка', 'Не удалось распознать текст');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { recognizeText(); }, []);

  const speak = () => {
    if (recognizedText) Speech.speak(recognizedText, { language: 'ru' });
  };

  const saveToHistory = async () => {
    try {
      const newEntry = {
        id: Date.now().toString(),
        imageUri: imageUri as string,
        text: recognizedText,
        date: new Date().toISOString(),
      };
      const historyJson = await AsyncStorage.getItem('history');
      const history = historyJson ? JSON.parse(historyJson) : [];
      history.push(newEntry);
      await AsyncStorage.setItem('history', JSON.stringify(history));
      Alert.alert('Успешно', 'Запись сохранена');
      router.push('/history');
    } catch {
      Alert.alert('Ошибка', 'Не удалось сохранить');
    }
  };

  return (
    <View style={styles.container}>
      <Image source={{ uri: imageUri as string }} style={styles.image} />
      {loading ? (
        <View style={styles.loading}>
          <ActivityIndicator size="large" />
          <Text>Распознаём текст...</Text>
        </View>
      ) : (
        <>
          <Text style={styles.text}>{recognizedText}</Text>
          <View style={styles.buttons}>
            <TouchableOpacity style={styles.button} onPress={speak}>
              <Text style={styles.buttonText}>Озвучить</Text>
            </TouchableOpacity>
            <TouchableOpacity style={styles.button} onPress={saveToHistory}>
              <Text style={styles.buttonText}>Сохранить</Text>
            </TouchableOpacity>
          </View>
        </>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 16, alignItems: 'center' },
  image: { width: '100%', height: 200, resizeMode: 'cover', borderRadius: 8, marginBottom: 16 },
  loading: { flex: 1, justifyContent: 'center', alignItems: 'center' },
  text: { fontSize: 16, lineHeight: 24, marginBottom: 16 },
  buttons: { flexDirection: 'row', justifyContent: 'space-around', width: '100%' },
  button: { backgroundColor: '#007AFF', paddingHorizontal: 24, paddingVertical: 12, borderRadius: 8 },
  buttonText: { color: '#fff', fontSize: 16 },
});