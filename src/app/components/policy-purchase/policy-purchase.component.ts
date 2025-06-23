import { Component, Input, Output, EventEmitter, ChangeDetectorRef } from '@angular/core';
import { Policy } from '../../model/Policy';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { PolicyServiceService } from '../../services/policy-service.service';
import { Payment } from '../../model/Payment';


@Component({
  selector: 'app-policy-purchase',
  templateUrl: './policy-purchase.component.html',
  styleUrl: './policy-purchase.component.css'
})
export class PolicyPurchaseComponent {

  @Input() policy!: Policy;
  @Output() confirm = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();
  
  isProcessing = false;
  errorMessage = '';
  showPaymentForm = false;
  isBlocked = false;
  showBlockedMessage = false;
  
  amount: number = 0;
  cardNumber: string = '';

  constructor(private policyService: PolicyServiceService, private authService: AuthService, private router: Router, private cdr: ChangeDetectorRef) {
    
  }
  onConfirmPurchase() {
    this.showPaymentForm = true;
    this.amount = this.policy.price; 
  }

  onPaymentSubmit() {

    if (this.isBlocked) {
      this.errorMessage = 'Your account is blocked. Please contact administrator.';
      return;
    }
    if (!this.validatePayment()) {
      return;
    }

    const currentUser = this.authService.getCurrentUser();
    if (!currentUser || !currentUser.id) {
      this.errorMessage = 'Morate biti prijavljeni da biste izvršili kupovinu';
    return;
  }
    this.isProcessing = true;
    
    const paymentData: Payment = {
      userId: currentUser.id,
      policyId: this.policy.id!,
      amount: this.amount,
      policyName: this.policy.policyname,
      cardNumber: this.cardNumber.replace(/\s/g, ''),
      paymentMethodId: 'pm_card_visa' 
    };
    console.log(paymentData.userId);
    this.policyService.purchasePolicy(paymentData).subscribe({
      next: () => {
        this.isProcessing = false;
        this.confirm.emit();
        console.log('uspjesna kupovina');
      },
      error: (error) => {
      this.isProcessing = false;

      if (error.message.includes('BLOCK:')) {
        const blockedMessage = error.message.split('BLOCK:')[1];
        this.showBlockedMessage = true;
        this.errorMessage = blockedMessage;
        this.cdr.detectChanges();
        setTimeout(() => {
          this.authService.logout(); 
        }, 5000);

      } else {
        this.errorMessage = 'Payment failed. Please try again.';
      }
    }
  })
}

  private validatePayment(): boolean {
    if (this.amount <= 0) {
      this.errorMessage = 'Unesite validan iznos';
      return false;
    }
    
    if (!this.cardNumber || this.cardNumber.replace(/\s/g, '').length < 16) {
      this.errorMessage = 'Unesite validan broj kartice (16 cifara)';
      return false;
    }
    
    this.errorMessage = '';
    return true;
  }

  formatCardNumber(event: Event) {
    const input = event.target as HTMLInputElement;
    let value = input.value.replace(/\s+/g, '');
    
    value = value.replace(/\D/g, '');
    
    let formattedValue = '';
    for (let i = 0; i < value.length; i++) {
      if (i > 0 && i % 4 === 0) {
        formattedValue += ' ';
      }
      formattedValue += value[i];
    }
    
    this.cardNumber = formattedValue;
    input.value = formattedValue;
  }

  onCancel() {
    this.cancel.emit();
  }
}
