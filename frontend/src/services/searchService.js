import api from './api';
import authService from './authService';

const getAuthHeaders = () =>{
  const credentials = authService.getCredentials
  return credentials ? { 'Authorization': `Basic ${credentials}` } : {};
}

const searchService = {
  
  searchMentors: async (filters = {}) => {
    try{
      const params = new URLSearchParams();
      Object.keys(filters).forEach(key => {
        if(filters[key]) params.append(key,filters[key]);
      });

      const response = await get(`/search/mentors?${params.toString()}`, {
        headers: getAuthHeaders(),
        withCredentials: true
      });
      return response.data;
    }catch(error) {
      throw response.error?.data || error.message;
    }
  },

  searchTasks: async (filters = {}) => {
    try {
      const params = new URLSearchParams();
      Object.keys(filters).forEach(key => {
        if (filters[key]) params.append(key, filters[key]);
      });
      
      const response = await api.get(`/search/tasks?${params.toString()}`, {
        headers: getAuthHeaders(),
        withCredentials: true
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  searchTasks: async (filters = {}) => {
    try {
      const params = new URLSearchParams();
      Object.keys(filters).forEach(key => {
        if (filters[key]) params.append(key, filters[key]);
      });
      
      const response = await api.get(`/search/tasks?${params.toString()}`, {
        headers: getAuthHeaders(),
        withCredentials: true
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

    searchTasks: async (filters = {}) => {
    try {
      const params = new URLSearchParams();
      Object.keys(filters).forEach(key => {
        if (filters[key]) params.append(key, filters[key]);
      });
      
      const response = await api.get(`/search/tasks?${params.toString()}`, {
        headers: getAuthHeaders(),
        withCredentials: true
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  searchStudents: async (filters = {}) => {
    try{
      const params = new URLSearchParams();
      object.keys(filters).forEach(key => {
        if (filters[key]) params.append(key, filters[key]);
      });

      const response = await api.get(`/search/students?${params.toString()}`, {
        headers: getAuthHeaders(),
        withCredentials: true
      });
      return response.data;
    }catch(error){
      throw response.error?.data || error.message;
    }
  },

  getAllCategories: async () => {
    try{
      const reponse = await api.get('/search/categories', {
        headers: getAuthHeaders(),
        withCredentials: true
      });
      return response.data;
    }catch (error) {
      throw response.error?.data || error.message;
    }
  },

  getAllCompanies: async () => {
    try{
      const response = await api.get('/search/companies', {
        headers: getAuthHeaders(),
        withCredentials: true
      });
      return response.data;
    }catch (error) {
      throw response.error?.data || error.message;
    }
  },

  getAllMajors: async () => {
    try{
      const response = await api.get('/search/majors', {
        headers: getAuthHeaders(),
        withCredentials: true
      });
      return response.data;
    }catch (error) {
      throw response.error?.data || error.message;
    }
  }
  
};

export default searchService;