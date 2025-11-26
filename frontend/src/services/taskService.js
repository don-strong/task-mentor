import api from './api';
import authService from './authService';

const getAuthHeaders = () => ({
  'Authorization': `Basic ${authService.getCredentials()}`
});

const taskService = {
  createTask: async (taskData, imageFile = null) => {
    try {
      const formData = new FormData();
      formData.append('task', new Blob([JSON.stringify(taskData)], { type: 'application/json' }));
      
      if (imageFile) {
        formData.append('image', imageFile);
      }

      const response = await api.post('/tasks', formData, {
        headers: {
          ...getAuthHeaders(),
          'Content-Type': 'multipart/form-data'
        }
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  updateTask: async (taskId, taskData, imageFile = null) => {
    try {
      const formData = new FormData();
      formData.append('task', new Blob([JSON.stringify(taskData)], { type: 'application/json' }));
      
      if (imageFile) {
        formData.append('image', imageFile);
      }

      const response = await api.put(`/tasks/${taskId}`, formData, {
        headers: {
          ...getAuthHeaders(),
          'Content-Type': 'multipart/form-data'
        }
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  deleteTask: async (taskId, mentorId) => {
    try {
      await api.delete(`/tasks/${taskId}?mentorId=${mentorId}`, {
        headers: getAuthHeaders()
      });
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  deleteTaskImage: async (taskId) => {
    try {
      await api.delete(`/tasks/${taskId}/image`, {
        headers: getAuthHeaders()
      });
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  getTaskById: async (taskId) => {
    try {
      const response = await api.get(`/tasks/${taskId}`);
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  getAllTasks: async (filters = {}) => {
    try {
      const params = new URLSearchParams();
      if (filters.category) params.append('category', filters.category);
      if (filters.mentorId) params.append('mentorId', filters.mentorId);
      if (filters.minDuration) params.append('minDuration', filters.minDuration);
      if (filters.maxDuration) params.append('maxDuration', filters.maxDuration);
      if (filters.search) params.append('search', filters.search);

      const response = await api.get(`/tasks?${params.toString()}`);
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  getTasksByMentor: async (mentorId) => {
    try {
      const response = await api.get(`/tasks/mentor/${mentorId}`);
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  }
};

export default taskService;