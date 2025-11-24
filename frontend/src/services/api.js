import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers:{
    'Content-Type':'application/json',
  },
});

api.interceptors.request.use(
  (config) => {
    return config;
  },
  (error) =>{
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if(error.response){
      switch(error.response.status){
        case 401:
          window.location.href='/login';
          break;
        case 403:
          console.error('Access Not Allowed');
          break;
        case 404:
          console.error('Resource Not Found');
          break;
        case 500:
          console.error('Server Error');
          break;
        default:
          console.error('An Error Occurred:', error.response.data);
      }
    }
    return Promise.reject(error);
  }
);

export default api;