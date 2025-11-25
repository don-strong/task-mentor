import api from './api';
import authService from './authService';

const getAuthHeaders = () => ({
  'Authorization': `Basic ${authService.getCredentials()}`
});

const studentService = {
  
  createProfile: async (profileData) => {
    try {
      const response = await api.post('/students', profileData, {
        headers: getAuthHeaders()
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  getMyProfile: async () => {
    try {
      const response = await api.get('/students/me', {
        headers: getAuthHeaders()
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },


  updateProfile: async (profileData) => {
    try {
      const response = await api.put('/students/me', profileData, {
        headers: getAuthHeaders()
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  
  getStudentById: async (id) => {
    try {
      const response = await api.get(`/students/${id}`);
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

 
  getAllStudents: async () => {
    try {
      const response = await api.get('/students');
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  }
};

export default studentService;