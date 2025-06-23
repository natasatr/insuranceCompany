import { Component, OnInit } from '@angular/core';
import { AuthService } from './services/auth.service';
import { Event, NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit{
  title = 'insurance-admin-app';
  showHeader=true;

  constructor(private router: Router, private authService: AuthService) {
    this.router.events.pipe(
      filter((event: Event): event is NavigationEnd => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
    });
  }

  ngOnInit(): void {
      this.authService.checkAuthStatus().subscribe({
        next: (authentication) => {
          if(!authentication) {
            console.log('not auth, redirection to login');
            this.router.navigate(['/login']);
          }
        },
        error: () => {
          this.router.navigate(['/login']);
        }
      });
    }
}
