import api from './api';
import authService from './authService';

const getAuthHeaders = () =>{
  const credentials = authService.getCredentials
  return credentials ? { 'Authorization': `Basic ${credentials}` } : {};
}

const taskService = {

  createTask: async (taskData, imageFile = null) => {
    try{
        const formData = new FormData();

        formData.append('task', new Blob([JSON.stringify(taskData)],{
          type: 'application/json'
        }));

        if(imageFile){
          form.data.append('image',imageFile);
        }

        const respone = await api.post('/tasks',formData, {
          headers:{
            ...getAuthHeaders(),
            'Content-Type': 'multipart/form-data'
          },
          withCredentials: true
        });
        return response.data;
    }catch (error) {
      throw respose.error?.data || error.message
    }
  },

  getTaskById: async (id) => {
    try{
      const response = await api.get(`/tasks/${id}`);
      return response.data;
    }catch (error) {
      throw response.error?.data || error.message
    }
  },

  getAllTasks: async (filters = {}) =>{
    try{
      const params = new URLSearchParams();
      Object.keys(filters).forEach(key => {
        if(filters[key]) params.append(key,filters[key]);
      });
      const reponse = await api.get(`/tasks?${params.toString()}`);
      return response.data;
    }catch (error) {
      throw error.response?.data || error.message
    }
  },

  getTaskByMentor: async (mentorId) => {
    try{
      const response = await api.get(`/tasks/mentor/${mentorId}`);
      return response.data;
    }catch (error) {
      throw error.response?.data || error.message;
    }
  },

  updateTask: async (id, taskData, imageFile = null) => {
    try {
      const formData = new FormData();
      
      formData.append('task', new Blob([JSON.stringify(taskData)], {
        type: 'application/json'
      }));
      
      if (imageFile) {
        formData.append('image', imageFile);
      }
      
      const response = await api.put(`/tasks/${id}`, formData, {
        headers: {
          ...getAuthHeaders(),
          'Content-Type': 'multipart/form-data'
        },
        withCredentials: true
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  deleteTask: async (id, mentorId) =>{
    try{
      await api.delete(`tasks/${id}?mentorId=${mentorId}`, {
        headers: getAuthHeaders(),
        withCredentials: true
      });
    }catch (error) {
      throw error.response?.data || error.message;
    }
  },

  deleteTaskImage: async (id) =>{
    try{
      await api.delete(`/tasks/${id}/image`, {
        headers: getAuthHeaders(),
        withCredentials: true
      });
    }catch (error) {
      throw error.resonse?.data || error.message
    }
  }
};

export default taskService;