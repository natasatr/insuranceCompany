import { Injectable } from '@angular/core';
import { environment } from '../environment/environment';
import { HttpClient } from '@angular/common/http';
import { AuthService } from './auth.service';
import { Policy } from '../model/Policy';
import { catchError, Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PolicyService {

    private readonly apiUrl = environment.apiUrl;
  
    constructor(private http: HttpClient, private authService: AuthService) {}
  
    getPolicies(): Observable<Policy[]> {
      const token = this.authService.getAccessToken();
      console.log(token);
      return this.http.get<Policy[]>(`${this.apiUrl}/admin/policies`, {
        withCredentials: true,
        headers: {
        Authorization: `Bearer ${token}`
      }

      }).pipe(
        tap(response => console.log('Fetched clients:', response)),
        catchError((error) => {
          if(error.status === 403) {
            throw new Error('You dont have permission to access this resource');
          }
          throw error;
        })
      )
    }

    addPolicy(policy: Policy): Observable<Policy> {
      console.log('fetch policies from ', `${this.apiUrl}/admin/policies`);
      const token = this.authService.getAccessToken();
      return this.http.post<Policy>(`${this.apiUrl}/admin/policies`, policy, {
        withCredentials: true,
        headers: {
        Authorization: `Bearer ${token}`
      }
      }).pipe(
        tap(newPolicy => console.log('added policy', newPolicy)),
        catchError((error:any) => {
          console.error(' policy error', error);
          throw error;
        })
      );
    }
}
