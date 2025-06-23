import { Component, OnInit } from '@angular/core';
import { catchError, Observable, of, tap } from 'rxjs';
import { Employee } from '../../model/Employee';
import { UserService } from '../../services/user.service';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-client-management',
  templateUrl: './client-management.component.html',
  styleUrl: './client-management.component.css'
})
export class ClientManagementComponent implements OnInit{


  clients: Employee[] = [];
  filteredClients: Employee[]= [];
  displayedClients: Employee[] = [];
  error: string | null = null;
  searchTerm: string = '';
  currentPage: number=1;
  pageSize: number = 10;
  totalItems: number=0;
  isLoading: boolean = false;

  selectedClient: Employee | null = null;
  showUpdateForm: boolean = false;


  constructor(private userService: UserService, private router: Router) {}

  ngOnInit(): void {
      this.loadClients();
  }

  loadClients() {
    this.isLoading = true;
    this.error = null;

    this.userService.getActiveClients().pipe(
      tap(clients => {
        this.clients = clients;
        this.filteredClients = [...clients];
        this.totalItems = clients.length;
        this.updateDisplayedClients();
        this.isLoading = false;
      }),
      catchError(err => {
        this.isLoading = false;
        this.error = err.message || 'Error loading clients: Unknown error';
        return of([]);
      })
    ).subscribe();
  }

  updateDisplayedClients() {
    const startIndex = (this.currentPage - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.displayedClients = this.filteredClients.slice(startIndex, endIndex);
  }

  onSearch() {
    this.currentPage = 1;
    if(!this.searchTerm.trim()) {
      this.filteredClients = [...this.clients];
      this.totalItems = this.filteredClients.length;
      this.updateDisplayedClients();
    } else {
      this.filterClients();
    }
  }
  filterClients() {
    /*if (!this.searchTerm.trim()) {
      this.filteredClients = [...this.clients];
    } else {
      const term = this.searchTerm.toLowerCase();
      this.filteredClients = this.clients.filter(client =>
        client.username.toLowerCase().includes(term) ||
        (client.firstName && client.firstName.toLowerCase().includes(term)) ||
        (client.lastName && client.lastName.toLowerCase().includes(term)) ||
        (client.email && client.email.toLowerCase().includes(term))
      );
    }
    this.totalItems = this.filteredClients.length;
    this.updateDisplayedClients();*/

    const term = this.searchTerm.toLowerCase().trim();
      this.filteredClients = this.clients.filter(client =>
        client.username.toLowerCase().includes(term) ||
        (client.firstName && client.firstName.toLowerCase().includes(term)) ||
        (client.lastName && client.lastName.toLowerCase().includes(term)) ||
        (client.email && client.email.toLowerCase().includes(term))
      );
    this.totalItems = this.filteredClients.length;
    this.updateDisplayedClients();

  }

  onPageChange(page: number) {
    const totalPages = Math.ceil(this.totalItems / this.pageSize);
    if (page > 0 && page <= totalPages) {
      this.currentPage = page;
      this.updateDisplayedClients();
    }
  }

  viewClient(id: number) {
    console.log('Client Details:', id);
    // Implement navigation or modal logic
  }

  editClient(id: number) {
    console.log('UpdateClient:', id);
    // 
    const client = this.clients.find(c => c.id === id);
    if(client) {
      this.selectedClient = {...client};
      this.showUpdateForm = true;
    }
  }

    handleUpdateSave(event: {client: Employee, password?: string}) {
      const updateData = {
        ...event.client,
        ...(event.password ? { newPassword: event.password } : {}) 
      };

      this.userService.updateClient(event.client.id!!, updateData).subscribe({
        next: () => {
          this.showUpdateForm = false;
          this.loadClients();
        },
        error: (err) => this.error = 'Update failed: ' + err.message
      });
    }

    handleUpdateCancel() {
      this.showUpdateForm = false;
    }

  deleteClient(id: number) {
    if (confirm('Are you sure about deleting this client?')) {
      this.userService.deleteClient(id).subscribe({
        next: () => this.loadClients(),
        error: (err) => this.error = 'Error during deleting: ' + err.message
      });
    }
  }

  blockClient(id: number) {
    if (confirm('Are you sure for blocking this client?')) {
      this.userService.blockClient(id).subscribe({
        next: () => this.loadClients(),
        error: (err) => this.error = 'Error during blocking: ' + err.message
      });
    }
  }

  unblockClient(id: number) {
    if (confirm('Unblock client?')) {
      this.userService.unblockClient(id).subscribe({
        next: () => this.loadClients(),
        error: (err) => this.error = 'Error during unblock: ' + err.message
      });
    }
  }

  getPaginationPages(): number[] {
  const totalPages = Math.ceil(this.totalItems / this.pageSize);
  return Array.from({length: totalPages}, (_, i) => i + 1);
}
}

