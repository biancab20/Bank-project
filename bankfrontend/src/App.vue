<script setup>
import { RouterLink, RouterView } from 'vue-router'
import Navigation from './components/Navigation.vue'
</script>

<template>
  <header>
    <div class="wrapper">
      <Navigation />

    </div>
  </header>

  <RouterView />
</template>

