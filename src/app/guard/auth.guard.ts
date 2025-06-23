import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { catchError, map, of, switchMap } from 'rxjs';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.isAuthenticated().pipe(
    switchMap(isAuthenticated => {
      if (isAuthenticated) {
        return of(true);
      }
      return authService.checkAuthStatus().pipe(
        map(isAuth => {
          if (isAuth) {
            console.log("korisnik je auth" +isAuth)
            return true;
          } else {
            console.log("korisnik nije auth" +isAuth)
            router.navigate(['/login'], {
              queryParams: { returnUrl: state.url }
            });
            return false;
          }
        }),
        catchError(() => {
          router.navigate(['/login'], {
            queryParams: { returnUrl: state.url }
          });
          return of(false);
        })
      );
    }),
    catchError(() => {
      router.navigate(['/login'], {
        queryParams: { returnUrl: state.url }
      });
      return of(false);
    })
  );
};
