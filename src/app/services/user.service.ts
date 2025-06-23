import { Injectable } from '@angular/core';
import { environment } from '../environment/environment';
import { HttpClient, HttpInterceptorFn } from '@angular/common/http';
import { catchError, Observable, tap } from 'rxjs';
import { Employee } from '../model/Employee';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly apiUrl = environment.apiUrl;

  constructor(private http: HttpClient, private authService: AuthService) {}

  getActiveClients(): Observable<Employee[]> {
    console.log('fetch clients from ', `${this.apiUrl}/admin/dashboard/client`);
    return this.http.get<Employee[]>(`${this.apiUrl}/admin/dashboard/client`, {
      withCredentials: true
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

  getClientByUsername(username: string): Observable<Employee> {
    const token = this.authService.getAccessToken();
    return this.http.get<Employee>(`${this.apiUrl}/admin/dashboard/client/${username}`,{
      withCredentials: true,
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
    
  }

  updateClient(id: number, user: Employee): Observable<Employee> {
    const token = this.authService.getAccessToken();
    return  this.http.put<Employee>(`${this.apiUrl}/admin/dashboard/client/${id}`, user,{
      withCredentials: true,
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  deleteClient(id: number): Observable<void> {
    const token = this.authService.getAccessToken();
    console.log('brisanje klijenta sa id'+id);
    return this.http.delete<void>(`${this.apiUrl}/admin/dashboard/client/${id}`,{
      withCredentials: true,
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  blockClient(id: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/admin/dashboard/client/${id}/block`,{}, {
      withCredentials: true
    });
  }

  unblockClient(id: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/admin/dashboard/client/${id}/unblock`,{}, {
      withCredentials: true
    });
  }

}
