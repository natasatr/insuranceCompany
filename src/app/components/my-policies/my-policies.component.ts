import { Component } from '@angular/core';
import { Policy } from '../../model/Policy';
import { PolicyServiceService } from '../../services/policy-service.service';
import { catchError, of, tap } from 'rxjs';
import { Router } from '@angular/router';
import { UserPolicy } from '../../model/UserPolicy';

@Component({
  selector: 'app-my-policies',
  templateUrl: './my-policies.component.html',
  styleUrl: './my-policies.component.css'
})
export class MyPoliciesComponent {

  policies: UserPolicy[] = [];
  filteredPolicies: UserPolicy[]= [];
  displayedPolicies: UserPolicy[] = [];
  error: string | null = null;
  searchTerm: string = '';
  currentPage: number=1;
  pageSize: number = 10;
  totalItems: number=0;
  isLoading: boolean = false;

  showPurchaseModal = false;
  purchasePolicy: UserPolicy | undefined;


  constructor(private poliicyService: PolicyServiceService, private router: Router) {}

  ngOnInit(): void {
      this.loadPolicies();
  }

  loadPolicies() {
    this.isLoading = true;
    this.error = null;

    this.poliicyService.getUserPolicies().pipe(
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
        (policy.user.firstName && policy.user.firstName.toLowerCase().includes(term)) ||
        (policy.user.lastName && policy.user.lastName.toLowerCase().includes(term)) ||
        (policy.policy.type && policy.policy.type.toLowerCase().includes(term)) ||
        (policy.policy.policyname && policy.policy.policyname.toLowerCase().includes(term)) ||
        (policy.policy.description && policy.policy.description.toLowerCase().includes(term))
);
    this.totalItems = this.filteredPolicies.length;
    this.updateDisplayedPolicy();

  }

  onPageChange(page: number) {
    const totalPages = Math.ceil(this.totalItems / this.pageSize);
    if (page > 0 && page <= totalPages) {
      this.currentPage = page;
      this.updateDisplayedPolicy();
    }
  }

deletePolicy(policy: Policy) {
  if (!policy.id) {
    this.error = 'Policy ID not available';
    return;
  }

  this.isLoading = true;
  this.error = null;

  this.poliicyService.deletePolicy(policy.id).pipe(
    tap(() => {
      this.policies = this.policies.filter(p => p.policy.id !== policy.id);
      this.filteredPolicies = this.filteredPolicies.filter(p => p.policy.id !== policy.id);
      this.totalItems = this.filteredPolicies.length;
      this.updateDisplayedPolicy();
      this.isLoading = false;
    }),
    catchError(err => {
      this.isLoading = false;
      this.error = err.message || 'Error deleting policy: Unknown error';
      return of(null);
    })
  ).subscribe();
}

  handlePurchaseConfirm() {
    //
  }
  handlePurchaseCancel() {
        this.showPurchaseModal = false;
        this.purchasePolicy = undefined;
  }
}