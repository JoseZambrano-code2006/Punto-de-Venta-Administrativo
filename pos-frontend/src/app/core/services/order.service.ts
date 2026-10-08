import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Order } from '../../shared/models/order.model';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}${environment.posPrefix}/order`;

  getAll(): Observable<Order[]> {
    return this.http.get<Order[]>(this.baseUrl);
  }

  getById(id: number): Observable<Order> {
    return this.http.get<Order>(`${this.baseUrl}/${id}`);
  }

  getByCashBox(cashBoxId: number): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.baseUrl}/cash-box/${cashBoxId}`);
  }

  create(order: Order): Observable<Order> {
    return this.http.post<Order>(this.baseUrl, order);
  }

  update(id: number, order: Order): Observable<Order> {
    return this.http.put<Order>(`${this.baseUrl}/${id}`, order);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getReportList(date: string): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.baseUrl}/report/list`, { params: { date } });
  }

  getReportTotal(date: string): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/report/total`, { params: { date } });
  }
}
