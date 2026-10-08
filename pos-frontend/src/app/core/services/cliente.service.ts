import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Cliente } from '../../shared/models/cliente.model';

@Injectable({ providedIn: 'root' })
export class ClienteService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}${environment.clientesPrefix}/api/clientes`;

  getAll(): Observable<Cliente[]> {
    return this.http.get<Cliente[]>(this.baseUrl);
  }

  getById(id: number): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.baseUrl}/${id}`);
  }

  create(cliente: Cliente): Observable<Cliente> {
    return this.http.post<Cliente>(this.baseUrl, cliente);
  }

  getByDocumento(numero: string): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.baseUrl}/documento/${numero}`);
  }

  searchByNombre(nombre: string): Observable<Cliente[]> {
    return this.http.get<Cliente[]>(`${this.baseUrl}/buscar`, { params: { nombre } });
  }
}
