import { Component } from '@angular/core';
import { RegisterUserRequest } from '../../model/RegisterUserRequest';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {

  registerData: RegisterUserRequest = {firstName: '', lastName: '', username: '', email: '', password: ''};
  repeatPassword: string = '';
  error: string | null = null;


  constructor(private authService: AuthService, private router: Router) {}

  onRegister(form: NgForm) {
    if(form.valid && this.registerData.password === this.repeatPassword) {
      console.log('register admin');
    this.authService.registerClient(this.registerData)
        .subscribe({
          next: () => {
            this.router.navigate(['/login'])
        },
        error: (err: any) => {
          this.error = err.error;
        }
    });
  } else {
    this.error = 'Please fill out the form correctly and ensure password match.';
  }
}
}

