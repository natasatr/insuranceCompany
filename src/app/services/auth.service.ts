import { Injectable } from '@angular/core';
import { environment } from '../environments/environments';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, catchError, Observable, of, tap } from 'rxjs';
import { RegisterUserRequest } from '../model/RegisterUserRequest';
import { LoginRequest } from '../model/LoginRequest';
import { VerifyRequest } from '../model/VerifyRequest';
import { JwtResponse } from '../model/JwtResponse';
import { User } from '../model/User';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly apiUrl = environment.apiUrl;
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  private jwtResponse: JwtResponse | null = null;
  private user: User |null = null;

  private currentUserSubject = new BehaviorSubject<User | null>(null);
  currentUser = this.currentUserSubject.asObservable();
  private blockedUserSubject = new BehaviorSubject<boolean>(false);
  blockedUser$ = this.blockedUserSubject.asObservable();

  
  constructor(private http: HttpClient, private router: Router) { }

  registerClient(data: RegisterUserRequest): Observable<string> {
    return this.http.post(`${this.apiUrl}/auth/register/client`, data, {responseType: 'text'});
  }

  login(data: LoginRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/login`, data).pipe(
    catchError((error) => {
      if (error.status === 403) {
        this.blockedUserSubject.next(true);
      }
      throw error;
    })
  );
  }

  blockUser(): void {
    this.blockedUserSubject.next(true);
    this.clearAuthState();
    this.router.navigate(['/login']);
  }
 /* verify2FA(data: VerifyRequest): Observable<JwtResponse> {
    console.log(this.currentUser)
    return this.http.post<JwtResponse>(`${this.apiUrl}/auth/verify-2fa`, data)
      .pipe(
        tap((response: any) => {
          this.jwtResponse = new JwtResponse(response.accessToken, response.refreshToken);
          this.isAuthenticatedSubject.next(true);
          this.router.navigate(['/dashboard-client']);
        })
      );
  }
  verify2FA(data: VerifyRequest): Observable<any> {
    console.log(this.user);
    return this.http.post<any>(`${this.apiUrl}/auth/verify-2fa`, data).pipe(
      tap((response) => {
        this.jwtResponse = new JwtResponse(response.accessToken, response.refreshToken);
        this.isAuthenticatedSubject.next(true);
        this.fetchUserProfile().subscribe({
          next: (user) => {
            this.currentUserSubject.next(user);
            this.router.navigate(['/dashboard-client']);
          },
          error: (err) => {
            console.error('Failed to get user profile', err);
            this.router.navigate(['/dashboard-client']);
          }
        });
      })
    );
  }
*/

  verify2FA(data: VerifyRequest): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/auth/verify-2fa`, data).pipe(
      tap((response) => {
        this.jwtResponse = new JwtResponse(response.accessToken, response.refreshToken);
        this.isAuthenticatedSubject.next(true);
        this.fetchUserProfile().subscribe({
          next: (user) => {
            this.currentUserSubject.next(user);
            this.router.navigate(['/dashboard-client']);
          },
          error: (err) => {
            console.error('Failed to get user profile', err);
            this.router.navigate(['/dashboard-client']);
          }
        });
      }),
      catchError((error) => {
        if (error.status === 403) {
          this.blockedUserSubject.next(true);
          this.clearAuthState();
        }
        throw error;
      })
    );
  }
  private fetchUserProfile(): Observable<User> {
    const token = this.getAccessToken();
    const username = this.getCurrentUsername();
    console.log(token + " ------ "+ this.user?.id);
    if (!username) {
      throw new Error('Username not available');
    }
    return this.http.get<User>(`${this.apiUrl}/client/profiles/${username}`, {
        withCredentials: true,
        headers: {
        Authorization: `Bearer ${token}`
      }
    });
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

  getCurrentUser(): User | null {
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

  setCurrentUser(user: User) {
    this.currentUserSubject.next(user);
  }

  private clearAuthState(): void {
    this.isAuthenticatedSubject.next(false);
    this.currentUserSubject.next(null);
    this.jwtResponse = null;
  }

}
