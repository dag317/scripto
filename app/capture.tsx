import { Camera } from 'expo-camera';
import * as ImagePicker from 'expo-image-picker';
import { router } from 'expo-router';
import { useState } from 'react';
import { Alert, StyleSheet, Text, TouchableOpacity, View } from 'react-native';

export default function CaptureScreen() {
  const [permissionStatus, setPermissionStatus] = useState<boolean | null>(null);

  const checkCameraPermission = async () => {
    const { status } = await Camera.getCameraPermissionsAsync();
    setPermissionStatus(status === 'granted');
    return status === 'granted';
  };

  const requestCameraPermission = async () => {
    const { status } = await Camera.requestCameraPermissionsAsync();
    setPermissionStatus(status === 'granted');
    return status === 'granted';
  };

  const pickFromGallery = async () => {
    const { status } = await ImagePicker.requestMediaLibraryPermissionsAsync();
    if (status !== 'granted') {
      Alert.alert('Нужно разрешение', 'Разрешите доступ к галерее');
      return;
    }

    const result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.Images,
      allowsEditing: true, // системная обрезка (тёмная на Android)
      quality: 0.8,
    });

    if (!result.canceled) {
      router.push({ pathname: '/result', params: { imageUri: result.assets[0].uri } });
    }
  };

  const takePhoto = async () => {
    let granted = await checkCameraPermission();
    if (!granted) granted = await requestCameraPermission();
    if (!granted) {
      Alert.alert('Нужно разрешение', 'Разрешите доступ к камере');
      return;
    }

    const result = await ImagePicker.launchCameraAsync({
      allowsEditing: true,
      quality: 0.8,
    });

    if (!result.canceled) {
      router.push({ pathname: '/result', params: { imageUri: result.assets[0].uri } });
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Добавить фото</Text>
      <TouchableOpacity style={styles.button} onPress={takePhoto}>
        <Text style={styles.buttonText}>Сделать снимок</Text>
      </TouchableOpacity>
      <TouchableOpacity style={[styles.button, styles.galleryButton]} onPress={pickFromGallery}>
        <Text style={styles.buttonText}>Выбрать из галереи</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, justifyContent: 'center', alignItems: 'center', padding: 16 },
  title: { fontSize: 24, marginBottom: 32 },
  button: {
    backgroundColor: '#007AFF',
    paddingHorizontal: 24,
    paddingVertical: 12,
    borderRadius: 8,
    marginBottom: 16,
    width: '80%',
    alignItems: 'center',
  },
  galleryButton: { backgroundColor: '#34C759' },
  buttonText: { color: '#fff', fontSize: 18 },
});