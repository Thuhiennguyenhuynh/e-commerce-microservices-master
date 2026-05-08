<template>
  <div class="articles">
    <h1>Latest Articles</h1>
    <div class="article-list">
      <article v-for="article in articles" :key="article.id" class="article-card">
        <h3>{{ article.title }}</h3>
        <p>{{ article.summary }}</p>
      </article>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '../services/api'

const articles = ref([])

onMounted(async () => {
  const response = await api.getArticles({ sort: 'latest' })
  articles.value = response.data
})
</script>

<style scoped>
.article-list { display: grid; gap: 1rem; padding: 1rem; }
.article-card { border: 1px solid #ccc; padding: 1rem; border-radius: 4px; }
</style>
