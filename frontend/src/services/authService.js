import api from './api';

const authService = {
  // Register a new user
  register: async (userData) => {
    try {
      const response = await api.post('/auth/register', userData);
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  // Login user
  login: async (email, password) => {
    try {
      // Create base64 encoded credentials
      const credentials = btoa(`${email}:${password}`);
      
      console.log('🔐 Attempting login for:', email);
      
      const response = await api.post('/auth/login', null, {
        headers: {
          'Authorization': `Basic ${credentials}`
        },
        withCredentials: true
      });
      
      console.log('✅ Login successful, response:', response.data);
      
      // Store user info in localStorage
      if (response.data) {
        localStorage.setItem('user', JSON.stringify(response.data));
        localStorage.setItem('credentials', credentials);
        console.log('✅ User data stored in localStorage');
      }
      
      return response.data;
    } catch (error) {
      console.error('❌ Login failed:', error);
      console.error('Error response:', error.response?.data);
      throw error.response?.data || error.message;
    }
  },

  // Logout user
  logout: () => {
    localStorage.removeItem('user');
    localStorage.removeItem('credentials');
    window.location.href = '/login';
  },

  // Get current user from localStorage
  getCurrentUser: () => {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      try {
        return JSON.parse(userStr);
      } catch (e) {
        console.error('Failed to parse user from localStorage:', e);
        return null;
      }
    }
    return null;
  },

  // Get stored credentials
  getCredentials: () => {
    return localStorage.getItem('credentials');
  },

  // Check if user is authenticated
  isAuthenticated: () => {
    return !!localStorage.getItem('credentials') && !!localStorage.getItem('user');
  }
};

export default authService;