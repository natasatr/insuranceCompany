import { Component } from '@angular/core';
import { VerifyRequest } from '../../model/VerifyRequest';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-verify',
  templateUrl: './verify.component.html',
  styleUrl: './verify.component.css'
})
export class VerifyComponent {

  verifyData: VerifyRequest = {username: '', code: ''};
  error: string | null = null;
  isBlocked = false;

  failedAttempts = 0;
  constructor(
    private authService: AuthService, 
    private router: Router
  ) {
    const username = this.router.getCurrentNavigation()?.extractedUrl.queryParams['username'];
    if(username) {
      this.verifyData.username = username;
    } else {
      this.router.navigate(['/login']);
    }
    this.authService.blockedUser$.subscribe(blocked => {
      if (blocked) {
        this.isBlocked = true;
        this.error = 'Your account has been blocked. Please contact administrator.';
      }
    });
  }

  onVerify(form: NgForm) {
    if (this.isBlocked) {
      return;
    }

    if(form.valid) {
      this.error = null;
      this.authService.verify2FA(this.verifyData).subscribe({
        next: (response) => {
          console.log('2fa verification success', response);
          this.router.navigate(['/dashboard-client']);
        },
        error: (err) => {
          this.failedAttempts++; // Increment failed attempts
          
          if (err.status === 403) {
            this.isBlocked = true;
            this.error = 'Your account has been blocked due to multiple failed attempts. Please contact administrator.';
          } else if (this.failedAttempts === 1) {
            this.error = 'Invalid verification code. Please try again.';
          } else {
            this.error = err.error?.message || 'Verification failed. Please check the code and try again.';
          }
        }
      });
    } else {
      this.error = 'Please enter a valid verification code';
    }
  }
}
