<template>
    <div class="signup-container">
      <div class="signup-card">
        <h2>Sign Up</h2>
        <form @submit.prevent="onSubmit">
          <div class="form-group">
            <label for="firstName">First Name:</label>
            <input v-model="firstName" id="firstName" type="text" required />
          </div>
          <div class="form-group">
            <label for="lastName">Last Name:</label>
            <input v-model="lastName" id="lastName" type="text" required />
          </div>
          <div class="form-group">
            <label for="bsn">BSN:</label>
            <input v-model="bsn" id="bsn" type="text" required />
          </div>
          <div class="form-group">
            <label for="phoneNumber">Phone Number:</label>
            <input v-model="phoneNumber" id="phoneNumber" type="text" required />
          </div>
          <div class="form-group">
            <label for="email">Email:</label>
            <input v-model="email" id="email" type="email" required />
          </div>
          <div class="form-group">
            <label for="password">Password:</label>
            <input v-model="password" id="password" type="password" required />
          </div>
          <button type="submit" name="action" id="submit">Sign Up</button>
        </form>
      </div>
    </div>
  </template>
  
  <script>
export default {
  name: 'SignUpPage',
  data() {
    return {
      firstName: '',
      lastName: '',
      bsn: '',
      phoneNumber: '',
      email: '',
      password: ''
    }
  },
  methods: {
    async onSubmit() {

      const formData = {
        firstName: this.firstName,
        lastName: this.lastName,
        bsn: this.bsn,
        phoneNumber: this.phoneNumber,
        email: this.email,
        password: this.password,
      };

      // Check for empty fields
      if (!this.checkText(formData)) {
        this.showAlert("Please fill in all fields.");
        return;
      }

      try {
        const response = await fetch('http://localhost:8080/auth/register', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(formData)
        });

        const data = await this.handleApiResponse(response);

        if (data.success) {
          this.$router.push('/login');
        } else if (data.error) {
          this.handleRegistrationError(data.error);
          this.showAlert(data.error);
        }
      } catch (error) {
        this.logError(error, "SignUpPage", "SignUpPage.vue");
        this.showAlert("An error occurred while registering, please try again later!");
      }
    },
    checkText(fields) {
      return Object.values(fields).every(value => value.trim() !== '');
    },
    async handleApiResponse(response) {
      const data = await response.json();
      if (response.ok) {
        return data;
      } else {
        return Promise.reject(data);
      }
    },
    handleRegistrationError(error) {
      if (error.includes('email')) {
        this.showAlert("The email address is already in use. Please use a different email.");
      } else if (error.includes('bsn')) {
        this.showAlert("The BSN is already registered. Please use a different BSN.");
      } else {
        this.showAlert(error);
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
  .signup-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: calc(100vh - 60px); /* Adjust height to account for the navbar */
    background-color: #f5f5f5;
    padding: 2rem;
  }
  
  .signup-card {
    background-color: white;
    padding: 2rem;
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    width: 100%;
    max-width: 400px;
    margin-top: 7rem;
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
  </style>
  