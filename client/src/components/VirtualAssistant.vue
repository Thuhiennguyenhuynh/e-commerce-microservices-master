<template>
  <div class="assistant-chat">
    <div class="chat-window">
      <div v-for="msg in messages" :key="msg.id" :class="['message', msg.sender]">
        {{ msg.text }}
      </div>
    </div>
    <div class="input-area">
      <input v-model="userInput" @keyup.enter="sendMessage" placeholder="Ask about products..." />
      <button @click="sendMessage">Send</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import api from '../services/api'

const messages = ref([{ id: 1, text: 'Hello! How can I help you today?', sender: 'bot' }])
const userInput = ref('')

const sendMessage = async () => {
  if (!userInput.value.trim()) return
  
  messages.value.push({ id: Date.now(), text: userInput.value, sender: 'user' })
  const query = userInput.value
  userInput.value = ''

  try {
    const response = await api.askAssistant(query)
    messages.value.push({ id: Date.now() + 1, text: response.data.reply, sender: 'bot' })
  } catch (error) {
    messages.value.push({ id: Date.now() + 1, text: 'Sorry, I am having trouble connecting.', sender: 'bot' })
  }
}
</script>

<style scoped>
.assistant-chat { position: fixed; bottom: 20px; right: 20px; width: 300px; border: 1px solid #ccc; background: white; border-radius: 8px; }
.chat-window { height: 300px; overflow-y: auto; padding: 1rem; }
.message.user { text-align: right; color: blue; }
.message.bot { text-align: left; color: green; }
.input-area { display: flex; padding: 0.5rem; }
</style>
