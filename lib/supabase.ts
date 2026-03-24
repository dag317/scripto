import AsyncStorage from '@react-native-async-storage/async-storage';
import { createClient } from '@supabase/supabase-js';

const supabaseUrl = 'https://wwoeigfmqwtrlmnxgenu.supabase.co';
const supabaseAnonKey = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Ind3b2VpZ2ZtcXd0cmxtbnhnZW51Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzMzOTY5NDgsImV4cCI6MjA4ODk3Mjk0OH0.xNw_k4DfpANPZGAl-7DKJ1XFC0Ahc_wEzsn3ZUVici0';

export const supabase = createClient(supabaseUrl, supabaseAnonKey, {
  auth: {
    storage: AsyncStorage,
    autoRefreshToken: true,
    persistSession: true,
    detectSessionInUrl: false,
  },
});
