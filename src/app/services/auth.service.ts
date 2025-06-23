import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Employee } from '../model/Employee';
import { BehaviorSubject, catchError, finalize, map, Observable, of, tap, throwError } from 'rxjs';
import { environment } from '../environment/environment';
import { JwtResponse } from '../model/JwtResponse';
import { Route, Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = environment.apiUrl;
  private jwtResponse: JwtResponse | null = null;
   private user: Employee |null = null;
   private currentUserSubject = new BehaviorSubject<Employee | null>(null);
  currentUser = this.currentUserSubject.asObservable();
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  constructor(private http: HttpClient, private router: Router) { }

  registerEmployee(emp : Employee): Observable<any> {
    console.log("Usao u metodu");
    return this.http.post(`${this.apiUrl}/auth/register/employee`, emp,
      {
        responseType: 'text'
      });
  }

  login(credentials: Partial<Employee>): Observable<any> {
    const body = {
      username: credentials.username,
      password: credentials.password
    }; 
    return this.http.post<any>(`${this.apiUrl}/auth/login`, body).pipe(
      tap((response) => {
        this.jwtResponse = new JwtResponse(response.accessToken, response.refreshToken);
        this.isAuthenticatedSubject.next(true);
      })

    );
  }


  refreshToken(): Observable<any> {
    console.log('pozvao se')
   // if (this.refreshInProgress) {
    //  console.log('refresh je vec u progresu');
     // return of(null); 
//    }

   // this.refreshInProgress = true;
    return this.http.post(`${this.apiUrl}/auth/refresh`, {}, {
      withCredentials: true
    }).pipe(
      tap(() => console.log('Token refreshed successfully')),
     // finalize(() => this.refreshInProgress = false),
      catchError(error => {
        this.router.navigate(['/login']);
        return throwError(() => error);
      })
    );
  }
  getCurrentUsername(): string | null {
    const token = this.getAccessToken();
    if (!token) return null;
    
    try {
      const payload = this.decodeToken(token);
      console.log(payload.sub);
      return payload.sub; 
    } catch (e) {
      console.error('Error decoding token', e);
      return null;
    }
  }

  private decodeToken(token: string): any {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    console.log(JSON.parse(window.atob(base64)));
    return JSON.parse(window.atob(base64));
  }

  getCurrentUser(): Employee | null {
    return this.currentUserSubject.value;
  }

  getAccessToken(): string | null {
    return this.jwtResponse?.accessToken || null;
  }

  getRefreshToken(): string | null {
    return this.jwtResponse?.refreshToken || null;
  }

checkAuthStatus(): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/auth/check`, { 
      withCredentials: true 
    }).pipe(
      tap(isAuthenticated => {
        this.isAuthenticatedSubject.next(isAuthenticated);
      }),
      catchError(() => {
        this.isAuthenticatedSubject.next(false);
        return of(false);
      })
    );
  }

  isAuthenticated(): Observable<boolean> {
    return this.isAuthenticatedSubject.asObservable();
  }

  logout(): void {
    this.http.post(`${this.apiUrl}/auth/logout`, {}, { 
      withCredentials: true 
    }).subscribe({
      next: () => {
        this.clearAuthState();
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.error('Logout error:', err);
        this.clearAuthState();
        this.router.navigate(['/login']);
      }
    });
  }

  setCurrentUser(user: Employee) {
    this.currentUserSubject.next(user);
  }

  private clearAuthState(): void {
    this.isAuthenticatedSubject.next(false);
    this.currentUserSubject.next(null);
    this.jwtResponse = null;
  }


}
