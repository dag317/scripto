import { useContext } from 'react';
import { ActivityIndicator, View } from 'react-native';
import { AuthContext } from '../context/AuthContext';
import AppStack from './AppStack';
import AuthStack from './AuthStack';

export default function RootNavigator() {
  const { userToken, isLoading } = useContext(AuthContext);

  if (isLoading) {
    return (
      <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
        <ActivityIndicator size="large" />
      </View>
    );
  }

  return userToken ? <AppStack /> : <AuthStack />;
}