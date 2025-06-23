import { Injectable } from '@angular/core';
import { environment } from '../environments/environments';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from './auth.service';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { Policy } from '../model/Policy';
import { Payment } from '../model/Payment';
import { User } from '../model/User';
import { UserPolicy } from '../model/UserPolicy';

@Injectable({
  providedIn: 'root'
})
export class PolicyServiceService {

  private readonly apiUrl = environment.apiUrl;
  
    constructor(private http: HttpClient, private authService: AuthService) {}
  
    getPolicies(): Observable<Policy[]> {
      const token = this.authService.getAccessToken();
      console.log('fetch poliicies from ', `${this.apiUrl}/client/user-policies` +token);
      return this.http.get<Policy[]>(`${this.apiUrl}/client/user-policies`,{
        withCredentials: true,
        headers: {
        Authorization: `Bearer ${token}`
      }
      }).pipe(
        tap(response => console.log('Fetched policies:', response)),
        catchError((error) => {
          if(error.status === 403) {
            console.log("nema")
            this.authService.logout();
            throw new Error('You dont have permission to access this resource');
          }
          throw error;
        })
      )
    }

    getUserPolicies(): Observable<UserPolicy[]> {
      const currentUser = this.authService.getCurrentUser();
      const userId=currentUser?.id;
      console.log('trenutni user ima id: ' +userId);
      const token = this.authService.getAccessToken();
      return this.http.get<UserPolicy[]>(`${this.apiUrl}/client/user-policies/${userId}`, {
        withCredentials: true, 
        headers: {
          Authorization: `Bearer ${token}`
        }
      }).pipe(tap(response => console.log('Fetched user policoes:', response)),
        catchError((error) => {
          if(error.status === 403) {
            console.log("nema")
            this.authService.logout();
            throw new Error('You dont have permission to access this resource');
          }
          throw error;
        })
      )
    }

  
    purchasePolicy(payment: Payment): Observable<any>{
      const token = this.authService.getAccessToken();
      const completePaymentData: Payment = { ...payment, paymentMethodId: payment.paymentMethodId || 'pm_card_visa' };

      return this.http.post(`${this.apiUrl}/client/payment/process`, completePaymentData, {
        withCredentials: true,
        headers: { 
          Authorization: `Bearer ${token}`
      }
      }).pipe(
        catchError((error) => {
          if(error.status === 403) {
           // this.authService.logout();
            //this.authService.blockUser();
            throw new Error('BLOCK:Your account has been blocked due to suspicious payment activity. Please contact administrator.');
        
        }
          throw error;
        })
      )
    }

    deletePolicy(policyId: number): Observable<void> {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser || !currentUser.id) {
      return throwError(() => new Error('User information not available'));
    }

    const token = this.authService.getAccessToken();
    console.log(token +" " +currentUser.id!);
        return this.http.delete<void>(
            `${this.apiUrl}/client/user-policies/${currentUser.id}/policy/${policyId}`, {
              withCredentials: true,
              headers: {
                Authorization: `Bearer ${token}`
              }
            }
        ).pipe(
          tap(() => console.log(`Policy ${policyId} deleted`)),
          catchError((error) => {
            if (error.status === 403) {
              console.log("No permission");
              this.authService.logout();
              throw new Error('You dont have permission to delete this policy');
            }
            throw error;
          })
    );
  }
}
