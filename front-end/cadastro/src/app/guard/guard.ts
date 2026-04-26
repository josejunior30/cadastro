import { inject } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";
import { AuthService } from "../services/auth.service";

export const authGuard: CanActivateFn = (route) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isLoggedIn()) {
    authService.logout();
    router.navigate(['/']);
    return false;
  }

  const requiredRoles = route.data?.['roles'] as string[] | undefined;

  if (requiredRoles?.length) {
    const temRole = requiredRoles.some((role) => authService.hasRole(role));

    if (!temRole) {
      router.navigate(['/publico']);
      return false;
    }
  }

  return true;
};
