import { Ionicons } from '@expo/vector-icons';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { useFocusEffect } from '@react-navigation/native';
import * as Speech from 'expo-speech';
import { useCallback, useState } from 'react';
import { Alert, FlatList, Image, StyleSheet, Text, TouchableOpacity, View } from 'react-native';

interface HistoryEntry {
  id: string;
  imageUri: string;
  text: string;
  date: string;
}

export default function HistoryScreen() {
  const [history, setHistory] = useState<HistoryEntry[]>([]);

  useFocusEffect(
    useCallback(() => {
      loadHistory();
    }, [])
  );

  const loadHistory = async () => {
    try {
      const json = await AsyncStorage.getItem('history');
      if (json) setHistory(JSON.parse(json));
    } catch (error) {
      Alert.alert('Ошибка', 'Не удалось загрузить историю');
    }
  };

  const deleteEntry = async (id: string) => {
    Alert.alert(
      'Удаление',
      'Вы уверены, что хотите удалить эту запись?',
      [
        { text: 'Отмена', style: 'cancel' },
        {
          text: 'Удалить',
          style: 'destructive',
          onPress: async () => {
            try {
              const newHistory = history.filter(item => item.id !== id);
              await AsyncStorage.setItem('history', JSON.stringify(newHistory));
              setHistory(newHistory);
            } catch (error) {
              Alert.alert('Ошибка', 'Не удалось удалить запись');
            }
          }
        }
      ]
    );
  };

  const speakText = (text: string) => {
    Speech.speak(text, { language: 'ru' });
  };

  const renderItem = ({ item }: { item: HistoryEntry }) => (
    <View style={styles.item}>
      <Image source={{ uri: item.imageUri }} style={styles.thumbnail} />
      <View style={styles.itemContent}>
        <Text numberOfLines={2} style={styles.itemText}>{item.text}</Text>
        <View style={styles.itemActions}>
          <TouchableOpacity onPress={() => speakText(item.text)}>
            <Ionicons name="volume-high" size={24} color="#007AFF" />
          </TouchableOpacity>
          <TouchableOpacity onPress={() => deleteEntry(item.id)}>
            <Ionicons name="trash" size={24} color="#ff3b30" />
          </TouchableOpacity>
        </View>
      </View>
    </View>
  );

  return (
    <View style={styles.container}>
      {history.length === 0 ? (
        <Text style={styles.empty}>История пуста</Text>
      ) : (
        <FlatList
          data={history}
          keyExtractor={item => item.id}
          renderItem={renderItem}
          contentContainerStyle={styles.list}
        />
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#fff' },
  list: { padding: 16 },
  item: {
    flexDirection: 'row',
    marginBottom: 16,
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 8,
    padding: 8,
  },
  thumbnail: { width: 80, height: 80, borderRadius: 4, marginRight: 12 },
  itemContent: { flex: 1, justifyContent: 'space-between' },
  itemText: { fontSize: 14, marginBottom: 8 },
  itemActions: {
    flexDirection: 'row',
    justifyContent: 'flex-end',
    gap: 16,
  },
  empty: { textAlign: 'center', marginTop: 50, fontSize: 16, color: '#888' },
});