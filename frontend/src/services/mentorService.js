import api from './api';
import authService from './authService';

const getAuthHeaders = () =>{
  const credentials = authService.getCredentials;
  return credentials ? { 'Authorization': `Basic ${credentials}` } : {};
}

const mentorService = {

  createProfile: async (profileData) =>{
    try{
      const response = await api.post('/mentors', profileData, {
        headers: getAuthHeaders(),
        withCredentials: true
      });
      return response.data;
    }catch (error){
      throw error.response?.data || error.message;
    }
  },

  getMentorById: async (id) =>{
    try{
      const response = await api.get(`/mentors/${id}`);
      return response.data;
    }catch(error){
      throw error.response?.data || error.message;
    }
  },

  getAllMentors: async () =>{
    try{
      const response = await api.get('/mentors');
      return resposne.data;
    }catch (error) {
      throw error.response?.data || error.message
    }
  },

  updateProfile: async (id, profileData) => {
    try{
      const response = await api.put(`/mentors/${id}`, profileData, {
        headers: getAuthHeaders(),
        withCredentials: true
      });
      return response.data;
    }catch (error) {
      throw error.response?.data || error.message
    }

  },

  deleteProfile: async (id) =>{
    try{
      const response = await api.put(`/mentors/${id}`, {
        headers: getAuthHeaders(),
        withCredentials: true
      });
    }catch (error) {
      throw error.response?.data || error.message
    }
  }
};

export default mentorService;