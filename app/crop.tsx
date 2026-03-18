import * as ImageManipulator from 'expo-image-manipulator';
import { router, useLocalSearchParams } from 'expo-router';
import { useEffect, useState } from 'react';
import { Dimensions, Image, StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import { GestureHandlerRootView, PanGestureHandler, PinchGestureHandler, State } from 'react-native-gesture-handler';
import Animated, { useAnimatedStyle, useSharedValue, withSpring } from 'react-native-reanimated';

const { width: SCREEN_WIDTH, height: SCREEN_HEIGHT } = Dimensions.get('window');

export default function CropScreen() {
  const { imageUri } = useLocalSearchParams();
  const [originalImage, setOriginalImage] = useState<{ width: number; height: number } | null>(null);
  
  // Позиция и масштаб для зума/перемещения
  const scale = useSharedValue(1);
  const translateX = useSharedValue(0);
  const translateY = useSharedValue(0);

  useEffect(() => {
    Image.getSize(imageUri as string, (width, height) => {
      setOriginalImage({ width, height });
    });
  }, []);

  const handleCrop = async () => {
    try {
      // Здесь нужно применить трансформации к изображению
      // Для простоты пока просто сохраняем оригинал
      const result = await ImageManipulator.manipulateAsync(
        imageUri as string,
        [
          // Применить масштаб и смещение (сложно, требует расчетов)
          // Для демо просто ресайз
          { resize: { width: 800 } },
        ],
        { compress: 0.8, format: ImageManipulator.SaveFormat.JPEG }
      );
      
      router.push({
        pathname: '/result',
        params: { imageUri: result.uri },
      });
    } catch (error) {
      console.error(error);
    }
  };

  const handleCancel = () => {
    router.back();
  };

  const animatedStyle = useAnimatedStyle(() => ({
    transform: [
      { scale: scale.value },
      { translateX: translateX.value },
      { translateY: translateY.value },
    ],
  }));

  return (
    <GestureHandlerRootView style={styles.container}>
      <View style={styles.header}>
        <TouchableOpacity onPress={handleCancel}>
          <Text style={styles.cancelText}>Отмена</Text>
        </TouchableOpacity>
        <Text style={styles.title}>Обрезка</Text>
        <TouchableOpacity onPress={handleCrop}>
          <Text style={styles.doneText}>Готово</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.imageContainer}>
        <PanGestureHandler
          onGestureEvent={(event) => {
            translateX.value = event.nativeEvent.translationX;
            translateY.value = event.nativeEvent.translationY;
          }}
          onHandlerStateChange={(event) => {
            if (event.nativeEvent.oldState === State.ACTIVE) {
              translateX.value = withSpring(translateX.value);
              translateY.value = withSpring(translateY.value);
            }
          }}
        >
          <PinchGestureHandler
            onGestureEvent={(event) => {
              scale.value = event.nativeEvent.scale;
            }}
            onHandlerStateChange={(event) => {
              if (event.nativeEvent.oldState === State.ACTIVE) {
                scale.value = withSpring(scale.value);
              }
            }}
          >
            <Animated.Image
              source={{ uri: imageUri as string }}
              style={[styles.image, animatedStyle]}
              resizeMode="contain"
            />
          </PinchGestureHandler>
        </PanGestureHandler>
      </View>

      <View style={styles.overlay}>
        <View style={styles.cropArea} />
      </View>
    </GestureHandlerRootView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: 16,
    borderBottomWidth: 1,
    borderBottomColor: '#eee',
  },
  cancelText: {
    fontSize: 16,
    color: '#888',
  },
  title: {
    fontSize: 18,
    fontWeight: '600',
  },
  doneText: {
    fontSize: 16,
    color: '#007AFF',
    fontWeight: '600',
  },
  imageContainer: {
    flex: 1,
    overflow: 'hidden',
  },
  image: {
    width: '100%',
    height: '100%',
  },
  overlay: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'rgba(0,0,0,0.5)',
  },
  cropArea: {
    width: SCREEN_WIDTH * 0.8,
    height: SCREEN_WIDTH * 0.8,
    borderWidth: 2,
    borderColor: '#fff',
    backgroundColor: 'transparent',
  },
});