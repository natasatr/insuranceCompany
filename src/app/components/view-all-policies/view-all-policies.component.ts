import { Component } from '@angular/core';
import { Policy } from '../../model/Policy';
import { Router } from '@angular/router';
import { PolicyServiceService } from '../../services/policy-service.service';
import { catchError, of, tap } from 'rxjs';

@Component({
  selector: 'app-view-all-policies',
  templateUrl: './view-all-policies.component.html',
  styleUrl: './view-all-policies.component.css'
})
export class ViewAllPoliciesComponent {

  policies: Policy[] = [];
  filteredPolicies: Policy[]= [];
  displayedPolicies: Policy[] = [];
  error: string | null = null;
  searchTerm: string = '';
  currentPage: number=1;
  pageSize: number = 10;
  totalItems: number=0;
  isLoading: boolean = false;

  showPurchaseModal = false;
  purchasePolicy: Policy | undefined;


  constructor(private poliicyService: PolicyServiceService, private router: Router) {}

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

  onPageChange(page: number) {
    const totalPages = Math.ceil(this.totalItems / this.pageSize);
    if (page > 0 && page <= totalPages) {
      this.currentPage = page;
      this.updateDisplayedPolicy();
    }
  }

  buyPolicy(policy: Policy) {
    this.purchasePolicy = policy;
    this.showPurchaseModal = true;
  }

  handlePurchaseConfirm() {
    //
  }
  handlePurchaseCancel() {
        this.showPurchaseModal = false;
        this.purchasePolicy = undefined;
  }
}
