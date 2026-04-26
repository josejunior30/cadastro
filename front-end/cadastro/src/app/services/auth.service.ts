import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { LoginResponse, JwtPayload } from '../models/auth';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = `${environment.apiUrl}/auth/login`;

  login(email: string, password: string): Observable<LoginResponse> {
    const body = {
      email,
      password,
    };

    return this.http.post<LoginResponse>(this.endpoint, body).pipe(
      tap((res) => {
        localStorage.setItem('access_token', res.token);
        localStorage.setItem('token_type', 'Bearer');
      })
    );
  }

  getToken(): string | null {
    return localStorage.getItem('access_token');
  }

  getAuthorizationHeader(): string | null {
    const token = this.getToken();

    if (!token) return null;

    return `Bearer ${token}`;
  }

  getPayload(): JwtPayload | null {
    const token = this.getToken();

    if (!token) return null;

    try {
      const payloadBase64 = token.split('.')[1];

      const normalizedPayload = payloadBase64
        .replace(/-/g, '+')
        .replace(/_/g, '/');

      const payloadJson = atob(normalizedPayload);

      return JSON.parse(payloadJson) as JwtPayload;
    } catch {
      return null;
    }
  }

  isLoggedIn(): boolean {
    const payload = this.getPayload();

    if (!payload?.exp) return false;

    const now = Math.floor(Date.now() / 1000);

    return payload.exp > now;
  }

  hasRole(role: string): boolean {
    const payload = this.getPayload();

    const authorities = payload?.authorities ?? payload?.roles ?? [];

    return authorities.includes(role);
  }

  logout(): void {
    localStorage.removeItem('access_token');
    localStorage.removeItem('token_type');
  }
}
