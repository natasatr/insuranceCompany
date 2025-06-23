import { Component } from '@angular/core';
import { Employee } from '../../model/Employee';
import { AuthService } from '../../services/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  credentials: Partial<Employee> = { username: '', password: '' };

  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute 
  ) {}

  onLogin(form: NgForm) {
    if (form.valid) {
      this.authService.login(this.credentials).subscribe({
        next: (response) => {
          this.router.navigate(['/dashboard-admin']); 
        },
        error: (error) => {
          console.error('Login failed', error);
          window.alert('Login failed');
        }
      });
    } else {
      window.alert('Please fill in all fields!');
    }
  }

}