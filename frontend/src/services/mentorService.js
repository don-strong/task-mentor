import api from './api';
import authService from './authService';

const getAuthHeaders = () => ({
  'Authorization': `Basic ${authService.getCredentials()}`
});

const mentorService = {
  getMyProfile: async () => {
    try {
      const response = await api.get('/mentors/me', {
        headers: getAuthHeaders()
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  createProfile: async (profileData) => {
    try {
      const response = await api.post('/mentors', profileData, {
        headers: getAuthHeaders()
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  getMentorById: async (id) => {
    try {
      const response = await api.get(`/mentors/${id}`);
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  getAllMentors: async () => {
    try {
      const response = await api.get('/mentors');
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  updateProfile: async (id, profileData) => {
    try {
      const response = await api.put(`/mentors/${id}`, profileData, {
        headers: getAuthHeaders()
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  deleteProfile: async (id) => {
    try {
      await api.delete(`/mentors/${id}`, {
        headers: getAuthHeaders()
      });
    } catch (error) {
      throw error.response?.data || error.message;
    }
  }
};

export default mentorService;