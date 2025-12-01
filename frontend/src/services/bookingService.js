import api from './api';
import authService from './authService';

const getAuthHeaders = () => ({
  'Authorization': `Basic ${authService.getCredentials()}`
});

const bookingService = {
  createBooking: async (bookingData) => {
    try {
      const response = await api.post('/bookings', bookingData, {
        headers: getAuthHeaders()
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  acceptBooking: async (bookingId, mentorId) => {
    try {
      const response = await api.put(`/bookings/${bookingId}/accept?mentorId=${mentorId}`, null, {
        headers: getAuthHeaders()
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  declineBooking: async (bookingId, mentorId) => {
    try {
      const response = await api.put(`/bookings/${bookingId}/decline?mentorId=${mentorId}`, null, {
        headers: getAuthHeaders()
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  cancelBooking: async (bookingId, userId, userType) => {
    try {
      const response = await api.put(
        `/bookings/${bookingId}/cancel?userId=${userId}&userType=${userType}`,
        null,
        {
          headers: getAuthHeaders()
        }
      );
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  getBookingById: async (bookingId) => {
    try {
      const response = await api.get(`/bookings/${bookingId}`, {
        headers: getAuthHeaders()
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  getBookingsByStudent: async (studentId) => {
    try {
      const response = await api.get(`/bookings/student/${studentId}`, {
        headers: getAuthHeaders()
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  getBookingsByMentor: async (mentorId, status = null) => {
    try {
      const url = status 
        ? `/bookings/mentor/${mentorId}?status=${status}`
        : `/bookings/mentor/${mentorId}`;
      
      const response = await api.get(url, {
        headers: getAuthHeaders()
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  getBookingsByTask: async (taskId) => {
    try {
      const response = await api.get(`/bookings/task/${taskId}`, {
        headers: getAuthHeaders()
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  deleteBooking: async (bookingId) => {
    try {
      await api.delete(`/bookings/${bookingId}`, {
        headers: getAuthHeaders()
      });
    } catch (error) {
      throw error.response?.data || error.message;
    }
  }
};

export default bookingService;