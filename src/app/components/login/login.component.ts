import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { LoginRequest } from '../../model/LoginRequest';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { threadId } from 'worker_threads';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
loginData: LoginRequest = {username: '', password: ''};
error: string | null = null;

constructor(private authService: AuthService, private router: Router) {}

onLogin(form: NgForm) {
  if (form.valid) {
    this.authService.login(this.loginData).subscribe({
      next: (response) => {
        console.log('Login success response:', response);
        this.router.navigate(['/verify-2fa'], { queryParams: { username: this.loginData.username } });
      },
      error: (err) => {
        console.error('Login error:', err);
        this.error = err.error || 'An error occurred during login.';
      }
    });
  } else {
    this.error = 'Please fill out the form correctly.';
  }
}

}
