import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { catchError, map, of } from 'rxjs';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.checkAuthStatus()
  .pipe( map(isAuthenticated => {
      if (!isAuthenticated) {
        return router.createUrlTree(['/login'], {
          queryParams: { returnUrl: state.url }
        });
      }
      return true;
    }),
    catchError(() => {
      return of(router.createUrlTree(['/login'], {
        queryParams: { returnUrl: state.url }
      }));
    })
  );
};