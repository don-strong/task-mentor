import api from './api';
import authService from './authService';

const getAuthHeaders = () => ({
  'Authorization': `Basic ${authService.getCredentials()}`
});

const taskService = {
  createTask: async (taskData, imageFile = null) => {
    try {
      // If no image, use the simpler JSON endpoint
      if (!imageFile) {
        const response = await api.post('/tasks/json', {
          mentorId: taskData.mentorId,
          title: taskData.title,
          description: taskData.description,
          durationMinutes: taskData.durationMinutes,
          category: taskData.category
        }, {
          headers: {
            ...getAuthHeaders(),
            'Content-Type': 'application/json'
          }
        });
        return response.data;
      }
      
      // With image, use multipart/form-data
      const formData = new FormData();
      
      const taskJson = {
        mentorId: taskData.mentorId,
        title: taskData.title,
        description: taskData.description,
        durationMinutes: taskData.durationMinutes,
        category: taskData.category
      };
      
      formData.append('task', new Blob([JSON.stringify(taskJson)], {
        type: 'application/json'
      }));
      formData.append('image', imageFile);
      
      const response = await api.post('/tasks', formData, {
        headers: {
          ...getAuthHeaders(),
        }
      });
      return response.data;
    } catch (error) {
      console.error('Create task error:', error.response?.data || error);
      throw error.response?.data || error.message;
    }
  },

  updateTask: async (taskId, taskData, imageFile = null) => {
    try {
      const formData = new FormData();
      
      const taskJson = {};
      if (taskData.title) taskJson.title = taskData.title;
      if (taskData.description) taskJson.description = taskData.description;
      if (taskData.durationMinutes) taskJson.durationMinutes = taskData.durationMinutes;
      if (taskData.category) taskJson.category = taskData.category;
      
      formData.append('task', new Blob([JSON.stringify(taskJson)], {
        type: 'application/json'
      }));
      
    
      if (imageFile) {
        formData.append('image', imageFile);
      }
      
      const response = await api.put(`/tasks/${taskId}`, formData, {
        headers: {
          ...getAuthHeaders(),
         
        }
      });
      return response.data;
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
  },

  getTasksByCategory: async (category) => {
    try {
      const response = await api.get(`/tasks?category=${category}`);
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
  }
};

export default taskService;