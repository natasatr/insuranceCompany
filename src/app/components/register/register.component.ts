import { Component } from '@angular/core';
import { Employee } from '../../model/Employee';
import { AuthService } from '../../services/auth.service';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {

  employee: Employee = {firstName: '', lastName:'', username:'', password:''};
  repeatPassword: string = '';

  constructor(private authService: AuthService) {
    
  }

  onRegister(form: NgForm) {
    if (form.valid && this.employee.password === this.repeatPassword) {
      console.log('registracija za ', this.employee);
      this.authService.registerEmployee(this.employee).subscribe({
        next: (response) => {
          console.log('Registration successful', response);
          window.alert('Registration successful! Please log in.');
        },
        error: (error) => {
          console.error('Registration failed', error);
          let errorMessage = 'Registration failed. ';
          if (error.status === 400) {
            errorMessage += 'Username may already exist.';
          } else if (error.status === 0) {
            errorMessage += 'Unable to connect to the server. Please check if the backend is running at https://localhost:8443.';
          }
          else if(error.message.includes('CORS')){
            errorMessage+='cors issue';
          }
          else {
            errorMessage += 'An unexpected error occurred.';
          }
          window.alert(errorMessage);
        }
      });
    } else {
      let errorMessage = 'Please ensure all fields are filled correctly:';
      if (!form.valid) {
        errorMessage += 'All required fields must be filled.';
      }
      if (this.employee.password !== this.repeatPassword) {
        errorMessage += 'Passwords do not match.';
      }
      window.alert(errorMessage);
    }

}
}
