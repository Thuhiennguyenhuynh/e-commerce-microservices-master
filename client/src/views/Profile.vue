<template>
  <div class="profile">
    <h1>User Profile</h1>
    <div v-if="user">
      <p>Name: {{ user.name }}</p>
      <p>Email: {{ user.email }}</p>
      <button @click="deleteAccount" class="delete-btn">Delete Account</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '../services/api'

const user = ref(null)

onMounted(async () => {
  // In a real app, get ID from authenticated session/store
  const userId = '1' 
  const response = await api.getUserProfile(userId)
  user.value = response.data
})

const deleteAccount = () => {
  if (confirm('Are you sure you want to delete your account?')) {
    // Implement delete API call
  }
}
</script>

<style scoped>
.delete-btn { background: red; color: white; border: none; padding: 0.5rem 1rem; cursor: pointer; }
</style>
