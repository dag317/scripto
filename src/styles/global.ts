import { StyleSheet } from 'react-native';

export const COLORS = {
  black: '#000000',
  dark: '#0A0A0A',
  grey: '#1A1A1A',
  textGrey: '#888',
  white: '#FFFFFF',
  accent: '#2A5CFF', // Твой синий
};

export const globalStyles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: COLORS.black,
    paddingHorizontal: 30,
    justifyContent: 'center',
  },
  inputContainer: {
    backgroundColor: COLORS.dark,
    borderRadius: 16,
    height: 60,
    justifyContent: 'center',
    paddingHorizontal: 20,
    borderWidth: 1,
    borderColor: COLORS.grey,
    marginBottom: 15,
  },
  inputText: {
    color: COLORS.white,
    fontSize: 16,
  },
  primaryButton: {
    backgroundColor: COLORS.white,
    height: 60,
    borderRadius: 30,
    alignItems: 'center',
    justifyContent: 'center',
    marginTop: 20,
  },
  buttonText: {
    color: COLORS.black,
    fontSize: 16,
    fontWeight: '600',
  }
});