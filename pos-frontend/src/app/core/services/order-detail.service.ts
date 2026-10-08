import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { OrderDetail } from '../../shared/models/order-detail.model';

@Injectable({ providedIn: 'root' })
export class OrderDetailService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}${environment.posPrefix}/order-detail`;

  getAll(): Observable<OrderDetail[]> {
    return this.http.get<OrderDetail[]>(this.baseUrl);
  }

  getByOrder(orderId: number): Observable<OrderDetail[]> {
    return this.http.get<OrderDetail[]>(`${this.baseUrl}/order/${orderId}`);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
