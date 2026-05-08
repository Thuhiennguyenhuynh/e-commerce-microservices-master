import axios from 'axios'

const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api', // Pointing to the API Gateway
  headers: {
    'Content-Type': 'application/json'
  }
})

export default {
  // Products
  getProducts(params: any) {
    return apiClient.get('/products', { params })
  },
  
  // Articles
  getArticles(params: any) {
    return apiClient.get('/articles', { params })
  },
  
  // User
  getUserProfile(id: string) {
    return apiClient.get(`/users/${id}`)
  },
  
  // Payments/Assistant
  askAssistant(query: string) {
    return apiClient.post('/assistant', { query })
  }
}
