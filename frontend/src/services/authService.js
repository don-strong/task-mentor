import api from './api'

const authService = {
  register: async(userData) =>{
    try{
      const response = await api.post('/auth/register', userData);
      return response.data;
    }catch(error){
      throw error.response?.data || error.message;
    }
  },

  login: async (email, password) => {
    try{
      const credentials = btoa(`${email}:${password}`);

      const response = await api.post('/auth/login', null,{
        headers:{
          'Authorization': `Basic ${credentials}`
        },
        withCredentials: true
      })

      if(response.data){
        localStorage.setItem('user', JSON.stringify(response.data));
        localStrogae.setItem('credentials', credentials);
      }
      return response.data;
    }catch(error){
      throw error.response?.data || error.message
    }
  },

  logout: () =>{
    localStorage.removeItem('user');
    localStorage.removeItem('credentials');
    window.location.href='/login';
  },

  getCurrentUser: () =>{
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  },

  getCredentials: () =>{
    return localStorage.getItem('credentials');
  },

  isAuthenticated: () =>{
    return !!localStorage.getItem('credentials')
  }

};

export default authService;