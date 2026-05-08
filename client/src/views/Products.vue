<template>
  <div class="products">
    <aside class="filters">
      <h3>Filters</h3>
      <!-- Filter controls -->
    </aside>
    <main class="product-grid">
      <div v-for="product in products" :key="product.id" class="product-card">
        {{ product.name }}
      </div>
      <div class="pagination">
        <!-- Pagination controls -->
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '../services/api'

const products = ref([])

onMounted(async () => {
  const response = await api.getProducts({})
  products.value = response.data
})
</script>

<style scoped>
.products { display: flex; gap: 2rem; }
.product-grid { flex: 1; display: grid; grid-template-columns: repeat(3, 1fr); gap: 1rem; }
</style>
