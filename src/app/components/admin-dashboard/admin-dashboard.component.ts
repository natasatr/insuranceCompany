import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css'
})
export class AdminDashboardComponent {
  constructor(private router: Router, private route: ActivatedRoute){}

  navigateTo(route: string) {
    this.router.navigate([`/dashboard-admin/${route}`]);
  }
}
