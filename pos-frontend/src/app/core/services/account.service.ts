import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Account } from '../../shared/models/account.model';

@Injectable({ providedIn: 'root' })
export class AccountService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}${environment.posPrefix}/account`;

  getAll(): Observable<Account[]> {
    return this.http.get<Account[]>(this.baseUrl);
  }

  getById(id: number): Observable<Account> {
    return this.http.get<Account>(`${this.baseUrl}/${id}`);
  }

  getByMail(mail: string): Observable<Account> {
    return this.http.get<Account>(`${this.baseUrl}/mail/${mail}`);
  }

  create(account: Account): Observable<Account> {
    return this.http.post<Account>(this.baseUrl, account);
  }

  update(id: number, account: Account): Observable<Account> {
    return this.http.put<Account>(`${this.baseUrl}/${id}`, account);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
