<template>
    <nav class="navbar">
      <div class="navbar-brand">
        <RouterLink to="/" class="navbar-item">B-Bank</RouterLink>
      </div>
      <div class="navbar-menu">
        <RouterLink to="/login" class="navbar-item">Login</RouterLink>
        <RouterLink to="/register" class="navbar-item">Sign Up</RouterLink>
      </div>
    </nav>
  </template>
  
  <script setup>
  import { RouterLink } from 'vue-router'
  </script>
  
  <style scoped>
  .navbar {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem 2rem;
    background-color: #42b983;
    color: white;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    z-index: 1000;
  }
  
  .navbar-brand {
    font-size: 1.5rem;
  }
  
  .navbar-menu {
    display: flex;
    gap: 1rem;
  }
  
  .navbar-item {
    color: white;
    text-decoration: none;
    font-size: 1rem;
  }
  
  .navbar-item:hover {
    text-decoration: underline;
  }
  </style>
  