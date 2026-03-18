import { Ionicons } from '@expo/vector-icons';
import { useState } from 'react';
import { StyleSheet, TouchableOpacity, View, useWindowDimensions } from 'react-native';
import { SceneMap, TabView } from 'react-native-tab-view';
import HistoryScreen from './history';
import HomeScreen from './index';
import SettingsScreen from './settings';

// Тип для имени иконки из Ionicons
type IconName = React.ComponentProps<typeof Ionicons>['name'];

// Явно указываем тип для маршрутов, чтобы иконки были корректными
const routes: { key: string; title: string; icon: IconName }[] = [
  { key: 'history', title: 'История', icon: 'time' },
  { key: 'home', title: 'Главная', icon: 'home' },
  { key: 'settings', title: 'Настройки', icon: 'settings' },
];

const renderScene = SceneMap({
  history: HistoryScreen,
  home: HomeScreen,
  settings: SettingsScreen,
});

export default function TabsLayout() {
  const layout = useWindowDimensions();
  const [index, setIndex] = useState(1); // 0 – история, 1 – главная, 2 – настройки

  // Отключаем встроенный таб-бар
  const renderTabBar = () => null;

  return (
    <View style={{ flex: 1 }}>
      {/* Контент с поддержкой свайпов */}
      <TabView
        navigationState={{ index, routes }}
        renderScene={renderScene}
        onIndexChange={setIndex}
        initialLayout={{ width: layout.width }}
        renderTabBar={renderTabBar}
        swipeEnabled={true}
      />
      {/* Кастомный таб-бар снизу */}
      <View style={styles.bottomTabBar}>
        {routes.map((route, i) => (
          <TouchableOpacity
            key={route.key}
            style={styles.tabItem}
            onPress={() => setIndex(i)}
          >
            <Ionicons
              name={route.icon}
              size={24}
              color={i === index ? '#007AFF' : '#888'}
            />
          </TouchableOpacity>
        ))}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  bottomTabBar: {
    flexDirection: 'row',
    height: 60,
    backgroundColor: '#fff',
    borderTopWidth: 1,
    borderTopColor: '#eee',
    alignItems: 'center',
    justifyContent: 'space-around',
  },
  tabItem: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
});