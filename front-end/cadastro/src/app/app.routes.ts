import { Routes } from '@angular/router';
import { authGuard } from './guard/guard';

export const routes: Routes = [
  
    {
    path: 'login',
    loadComponent: () =>
      import('./pages/login/login').then((m) => m.Login),
  },
];
