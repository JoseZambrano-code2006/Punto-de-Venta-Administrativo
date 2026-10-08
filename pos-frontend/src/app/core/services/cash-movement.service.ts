import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CashMovement } from '../../shared/models/cash-movement.model';

@Injectable({ providedIn: 'root' })
export class CashMovementService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}${environment.posPrefix}/cash-movement`;

  getAll(): Observable<CashMovement[]> {
    return this.http.get<CashMovement[]>(this.baseUrl);
  }

  getByCashBox(cashBoxId: number): Observable<CashMovement[]> {
    return this.http.get<CashMovement[]>(`${this.baseUrl}/cash-box/${cashBoxId}`);
  }

  create(movement: CashMovement): Observable<CashMovement> {
    return this.http.post<CashMovement>(this.baseUrl, movement);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
