<template>
    <div class="login-page">
      <div class="branding">
        <h1>B-Bank</h1>
        <p>We take care of your needs</p>
      </div>
      <div class="login-container">
        <div class="login-card">
          <h2>Login</h2>
          <form @submit.prevent="onSubmit">
            <div class="form-group">
              <label for="email">Email:</label>
              <input v-model="email" id="email" type="email" required />
            </div>
            <div class="form-group">
              <label for="password">Password:</label>
              <input v-model="password" id="password" type="password" required />
            </div>
            <button type="submit">Login</button>
          </form>
          <p class="signup-link">
            Don't have an account yet? <router-link to="/register">Sign up!</router-link>
          </p>
        </div>
      </div>
    </div>
  </template>
  
  <script>
    import { useRouter } from 'vue-router';
  export default {
    name: 'LoginPage',
    data() {
      return {
        email: '',
        password: ''
      }
    },
    setup() {
      const router = useRouter();
      return { router };
    },
    methods: {
      async onSubmit() {
      const formData = {
        email: this.email,
        password: this.password
      };

      try {
        const response = await fetch('http://localhost:8080/auth/login', {
          method: 'POST',
          headers: { 
            'Content-Type': 'application/json',
            'Authorization': 'Bearer NCC-1701'
           },
          body: JSON.stringify(formData)
        });

        const data = await this.handleApiResponse(response);

        if (data === "Login successful!") {
          localStorage.setItem('userId', data.userId);
        localStorage.setItem('userRole', data.role);

        // Redirect based on role
        if (data.role === 'EMPLOYEE') {
          this.router.push("/employee-dashboard");
        } else if (data.role === 'CUSTOMER') {
          this.router.push("/customer-dashboard");
        } else {
          this.showAlert("Unknown user role");
        }
      } else {
        this.showAlert(data.message);
      }
    } catch (error) {
      this.logError(error, "LoginPage", "LoginPage.vue");
      this.showAlert("An error occurred while logging in, please try again later!");
    }
  },
    async handleApiResponse(response) {
      const data = await response.text();
      if (response.ok) {
        return data;
      } else {
        return Promise.reject(data);
      }
    },
    showAlert(message) {
      alert(message);
    },
    logError(error, context, location) {
      console.error(`Error in ${context} at ${location}:`, error);
    }
  }
  }
  </script>
  
  <style scoped>
  html, body {
    height: 100%;
    margin: 0;
  }
  
  .login-page {
    display: flex;
    width: 100%;
    height: 100vh; /* Full screen height */
    background-color: #f5f5f5;
    padding: 2rem;
    box-sizing: border-box; /* Ensures padding is included in the height */
  }
  
  .branding {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
    text-align: left;
  }
  
  .branding h1 {
    font-size: 3rem;
    margin: 0;
  }
  
  .branding p {
    font-size: 1.2rem;
    margin-top: 0.5rem;
  }
  
  .login-container {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
  .login-card {
    background-color: white;
    padding: 2rem;
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    width: 100%;
    max-width: 400px;
  }
  
  .form-group {
    margin-bottom: 1.5rem;
  }
  
  form label {
    display: block;
    margin-bottom: 0.5rem;
  }
  
  form input {
    width: 100%;
    padding: 0.5rem;
    border: 1px solid #ccc;
    border-radius: 4px;
  }
  
  button {
    width: 100%;
    padding: 0.5rem;
    border: none;
    border-radius: 4px;
    background-color: #42b983;
    color: white;
    font-size: 1rem;
    cursor: pointer;
  }
  
  button:hover {
    background-color: #369a6e;
  }
  
  .signup-link {
    margin-top: 1rem;
    text-align: center;
  }
  
  .signup-link a {
    color: #42b983;
    text-decoration: none;
  }
  
  .signup-link a:hover {
    text-decoration: underline;
  }
  
  @media (max-width: 768px) {
    .login-page {
      flex-direction: column;
      justify-content: center;
      align-items: center;
    }
  
    .branding {
      text-align: center;
      margin-bottom: 2rem;
    }
  
    .login-container {
      justify-content: center;
      align-items: center;
    }
  
    .login-card {
      max-width: 400px;
    }
  }
  </style>
  