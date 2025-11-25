import api from './api';
import authService from './authService';

const getAuthHeaders = () => ({
  'Authorization': `Basic ${authService.getCredentials()}`
});

const searchService = {
  searchMentors: async (filters = {}) => {
    try {
      const params = new URLSearchParams();
      if (filters.name) params.append('name', filters.name);
      if (filters.company) params.append('company', filters.company);
      if (filters.industry) params.append('industry', filters.industry);
      if (filters.expertise) params.append('expertise', filters.expertise);
      if (filters.minYearsExperience) params.append('minYearsExperience', filters.minYearsExperience);

      const response = await api.get(`/search/mentors?${params.toString()}`, {
        headers: getAuthHeaders()
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  searchTasks: async (filters = {}) => {
    try {
      const params = new URLSearchParams();
      if (filters.title) params.append('title', filters.title);
      if (filters.category) params.append('category', filters.category);
      if (filters.mentorId) params.append('mentorId', filters.mentorId);
      if (filters.minDuration) params.append('minDuration', filters.minDuration);
      if (filters.maxDuration) params.append('maxDuration', filters.maxDuration);

      const response = await api.get(`/search/tasks?${params.toString()}`, {
        headers: getAuthHeaders()
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  searchStudents: async (filters = {}) => {
    try {
      const params = new URLSearchParams();
      if (filters.name) params.append('name', filters.name);
      if (filters.major) params.append('major', filters.major);
      if (filters.graduationYear) params.append('graduationYear', filters.graduationYear);
      if (filters.minGraduationYear) params.append('minGraduationYear', filters.minGraduationYear);
      if (filters.careerInterests) params.append('careerInterests', filters.careerInterests);

      const response = await api.get(`/search/students?${params.toString()}`, {
        headers: getAuthHeaders()
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  searchMentorsWithTasks: async (filters = {}) => {
    try {
      const params = new URLSearchParams();
      if (filters.mentorName) params.append('mentorName', filters.mentorName);
      if (filters.expertise) params.append('expertise', filters.expertise);
      if (filters.taskCategory) params.append('taskCategory', filters.taskCategory);
      if (filters.maxDuration) params.append('maxDuration', filters.maxDuration);

      const response = await api.get(`/search/mentors-with-tasks?${params.toString()}`, {
        headers: getAuthHeaders()
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  getAllCategories: async () => {
    try {
      const response = await api.get('/search/categories', {
        headers: getAuthHeaders()
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  getAllCompanies: async () => {
    try {
      const response = await api.get('/search/companies', {
        headers: getAuthHeaders()
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  getAllMajors: async () => {
    try {
      const response = await api.get('/search/majors', {
        headers: getAuthHeaders()
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  getFilterOptions: async () => {
    try {
      const response = await api.get('/search/filter-options', {
        headers: getAuthHeaders()
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  }
};

export default searchService;