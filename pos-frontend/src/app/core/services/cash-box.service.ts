import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { GeneralCashBox } from '../../shared/models/general-cash-box.model';

@Injectable({ providedIn: 'root' })
export class CashBoxService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}${environment.posPrefix}/cash-box`;

  getAll(): Observable<GeneralCashBox[]> {
    return this.http.get<GeneralCashBox[]>(this.baseUrl);
  }

  getById(id: number): Observable<GeneralCashBox> {
    return this.http.get<GeneralCashBox>(`${this.baseUrl}/${id}`);
  }

  getOpen(): Observable<GeneralCashBox> {
    return this.http.get<GeneralCashBox>(`${this.baseUrl}/open`);
  }

  open(cashBox: GeneralCashBox): Observable<GeneralCashBox> {
    return this.http.post<GeneralCashBox>(`${this.baseUrl}/open`, cashBox);
  }

  close(id: number): Observable<GeneralCashBox> {
    return this.http.put<GeneralCashBox>(`${this.baseUrl}/close/${id}`, {});
  }
}
