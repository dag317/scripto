import AsyncStorage from '@react-native-async-storage/async-storage';
import { createContext, ReactNode, useEffect, useState } from 'react';

type AuthContextType = {
  userToken: string | null;
  login: (token: string) => Promise<void>;
  logout: () => Promise<void>;
  isLoading: boolean;
};

export const AuthContext = createContext<AuthContextType>(
  {} as AuthContextType
);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [userToken, setUserToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  useEffect(() => {
    const loadToken = async () => {
      const token = await AsyncStorage.getItem('token');
      setUserToken(token);
      setIsLoading(false);
    };

    loadToken();
  }, []);

  const login = async (token: string) => {
    await AsyncStorage.setItem('token', token);
    setUserToken(token);
  };

  const logout = async () => {
    await AsyncStorage.removeItem('token');
    setUserToken(null);
  };

  return (
    <AuthContext.Provider value={{ userToken, login, logout, isLoading }}>
      {children}
    </AuthContext.Provider>
  );
};