import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginRequest, TokenResponse } from '../../shared/models/auth.model';
import { RegisteredUser } from '../../shared/models/registered-user.model';

const TOKEN_KEY = 'pos_access_token';
const USERNAME_KEY = 'pos_username';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  login(credentials: LoginRequest): Observable<TokenResponse> {
    return this.http
      .post<TokenResponse>(`${environment.apiUrl}${environment.authPrefix}/login`, credentials)
      .pipe(
        tap((response) => {
          sessionStorage.setItem(TOKEN_KEY, response.accessToken);
          sessionStorage.setItem(USERNAME_KEY, credentials.username);
        })
      );
  }

  register(credentials: LoginRequest): Observable<TokenResponse> {
    return this.http
      .post<TokenResponse>(`${environment.apiUrl}${environment.authPrefix}/register`, credentials)
      .pipe(
        tap((response) => {
          sessionStorage.setItem(TOKEN_KEY, response.accessToken);
          sessionStorage.setItem(USERNAME_KEY, credentials.username);
        })
      );
  }

  logout(): void {
    sessionStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(USERNAME_KEY);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return sessionStorage.getItem(TOKEN_KEY);
  }

  getUsername(): string | null {
    return sessionStorage.getItem(USERNAME_KEY);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  getRegisteredUsers(): Observable<RegisteredUser[]> {
    return this.http.get<RegisteredUser[]>(`${environment.apiUrl}${environment.authPrefix}/users`);
  }
}
