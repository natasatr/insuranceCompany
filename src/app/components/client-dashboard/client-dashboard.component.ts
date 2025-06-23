import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-client-dashboard',
  templateUrl: './client-dashboard.component.html',
  styleUrl: './client-dashboard.component.css'
})
export class ClientDashboardComponent {
constructor(private router: Router, private route: ActivatedRoute){}

  navigateTo(route: string) {
    this.router.navigate([`/dashboard-client/${route}`]);
  }
}
