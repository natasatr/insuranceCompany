import { Component, OnInit } from '@angular/core';
import { Policy } from '../../model/Policy';
import { PolicyService } from '../../services/policy.service';
import { Router } from '@angular/router';
import { catchError, of, tap } from 'rxjs';

@Component({
  selector: 'app-policy-management',
  templateUrl: './policy-management.component.html',
  styleUrl: './policy-management.component.css'
})
export class PolicyManagementComponent implements OnInit{


  policies: Policy[] = [];
  filteredPolicies: Policy[]= [];
  displayedPolicies: Policy[] = [];
  error: string | null = null;
  searchTerm: string = '';
  currentPage: number=1;
  pageSize: number = 10;
  totalItems: number=0;
  isLoading: boolean = false;

  selectedPolicy: Policy | undefined;
  showSavedForm: boolean = false;

  showFullDescription: boolean = false;
  showDescriptionPopup: boolean = false;

  constructor(private poliicyService: PolicyService, private router: Router) {}

  ngOnInit(): void {
      this.loadPolicies();
  }

  loadPolicies() {
    this.isLoading = true;
    this.error = null;

    this.poliicyService.getPolicies().pipe(
      tap(policies => {
        this.policies = policies;
        this.filteredPolicies = [...policies];
        this.totalItems = policies.length;
        this.updateDisplayedPolicy();
        this.isLoading = false;
      }),
      catchError(err => {
        this.isLoading = false;
        this.error = err.message || 'Error loading policy: Unknown error';
        return of([]);
      })
    ).subscribe();
  }

  updateDisplayedPolicy() {
    const startIndex = (this.currentPage - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.displayedPolicies = this.filteredPolicies.slice(startIndex, endIndex);
  }

  onSearch() {
    this.currentPage = 1;
    if(!this.searchTerm.trim()) {
      this.filteredPolicies = [...this.policies];
      this.totalItems = this.filteredPolicies.length;
      this.updateDisplayedPolicy();
    } else {
      this.filterPolicies();
    }
  }

  filterPolicies() {

    const term = this.searchTerm.toLowerCase().trim();
      this.filteredPolicies = this.policies.filter(policy =>
        (policy.policyname && policy.policyname.toLowerCase().includes(term)) ||
        (policy.type && policy.type.toLowerCase().includes(term)) ||
        (policy.price && policy.price.toString().includes(term)) ||
        (policy.description && policy.description.toLowerCase().includes(term))
      );
    this.totalItems = this.filteredPolicies.length;
    this.updateDisplayedPolicy();

  }
  toggleDescription(policy: Policy) {
    if (this.selectedPolicy === policy) {
      this.showFullDescription = !this.showFullDescription;
    } else {
      this.selectedPolicy = policy;
      this.showFullDescription = true;
    }
  }

  onPageChange(page: number) {
    const totalPages = Math.ceil(this.totalItems / this.pageSize);
    if (page > 0 && page <= totalPages) {
      this.currentPage = page;
      this.updateDisplayedPolicy();
    }
  }

    addNewPolicy() {
      this.selectedPolicy = {
      policyname: '',
      type: '',
      price: 0,
      description: ''
  };
    this.showSavedForm = true;
    }


    handleSave(event: { policy: Policy; }) {
        const newPolicy = event.policy;

        this.poliicyService.addPolicy(newPolicy).subscribe({
          next: (savedPolicy) => {
            this.policies.push(savedPolicy);
            this.filteredPolicies = [...this.policies];
            this.totalItems = this.filteredPolicies.length;
            this.updateDisplayedPolicy();
            this.showSavedForm = false;
          },
          error: (error) => {
            console.error('Greska pri dodavanju polise: ', error);
          }
        })
      }

    handleCancel() {
      this.showSavedForm = false;
      this.showDescriptionPopup = false;
    }

    showDescription(policy: Policy) {
      this.selectedPolicy = policy;
      this.showDescriptionPopup = true;
}

closeDescriptionPopup() {
  this.showDescriptionPopup = false;
  //this.selectedPolicy = null;
}

}
