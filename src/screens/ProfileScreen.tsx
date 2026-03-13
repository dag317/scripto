import React from 'react';
import { Button, StyleSheet, Text, View } from 'react-native';
import { supabase } from '../../lib/supabase';

export default function ProfileScreen({ route }: any) {
  const userEmail = route.params?.userEmail;

  return (
    <View style={styles.container}>
      <Text style={styles.text}>Вы вошли как:</Text>
      <Text style={styles.email}>{userEmail}</Text>
      <Button title="Выйти" color="red" onPress={() => supabase.auth.signOut()} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, justifyContent: 'center', alignItems: 'center' },
  text: { fontSize: 18 },
  email: { fontSize: 20, fontWeight: 'bold', marginVertical: 20 }
});