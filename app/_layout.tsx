import { Stack } from 'expo-router';

export default function RootLayout() {
  return (
    <Stack>
      <Stack.Screen name="(tabs)" options={{ headerShown: false }} />
      <Stack.Screen name="capture" options={{ headerShown: false, presentation: 'modal' }} />
      <Stack.Screen name="crop" options={{ headerShown: false, presentation: 'modal' }} />
      <Stack.Screen name="result" options={{ headerShown: false, presentation: 'card' }} />
    </Stack>
  );
}