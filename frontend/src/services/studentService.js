import api from './api';
import authService from './authService';

const getAuthHeaders = () => {
  const credentials = authService.getCredentials
  return credentials ? { 'Authorization': `Basic ${credentials}`} : {};
};

const studentService = {
  createProfile: async (profileData) =>{
    try{
      const response = await api.post('/students', profileData, {
        headers: getAuthHeaders(),
        withCredentials: true
      });
      return response.data;
    }catch(error){
      throw error.response?.data || error.message;
    }
  },

  getMyProfile: async() =>{
    try{
      const response = await api.get('/students/me', {
        headers: getAuthHeaders(),
        withCredentials: true
      });
      return response.data;
    }catch(error){
      throw error.response?.data || error.message;
    }
  },


  updateProfile: async (profileData) =>{
    try{
      const response = await api.put('/students/me', profileData, {
        headers: getAuthHeaders(),
        withCredentials: true
      });
      return response.data;
    }catch(error){
      throw error.response?.data || error.message;
    }
  },

  getStudentById: async (id) => {
    try{
      const response = await api.get(`/students/${id}`);
      return response.data;
    }catch(error){
      throw error.response?.data || error.messasge;
    }
  },

  getAllStudents: async () =>{
    try{
      const response = await api.get('/students');
      return response.data;
    }catch(error){
      throw error.response?.data || error.message
    }
  }

};

export default studentService;