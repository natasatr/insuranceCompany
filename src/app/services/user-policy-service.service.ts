import { Injectable } from '@angular/core';
import { environment } from '../environments/environments';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';
import { Observable } from 'rxjs';
import { User } from '../model/User';

@Injectable({
  providedIn: 'root'
})
export class UserPolicyServiceService {

  private readonly  apiUrl = environment.apiUrl;

  constructor(private http: HttpClient, private router: Router, private authService: AuthService  ) { }

    getCurrentUserProfile(): Observable<User> {
        const token = this.authService.getAccessToken();
    
        const username = this.authService.getCurrentUsername();
        console.log(username);
        if (!username) {
          throw new Error('Username not available');
        }
      
        return this.http.get<User>(`${this.apiUrl}/user/${username}`, {
          withCredentials: true,
          headers: { 
              Authorization: `Bearer ${token}`
          }
        });
    }
}
