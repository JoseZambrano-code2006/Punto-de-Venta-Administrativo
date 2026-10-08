import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, of, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CategoryImageMeta } from '../../shared/models/category-image.model';

@Injectable({ providedIn: 'root' })
export class CategoryImageService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}${environment.posPrefix}/category-image`;
  private readonly blobUrlCache = new Map<number, string>();

  list(): Observable<CategoryImageMeta[]> {
    return this.http.get<CategoryImageMeta[]>(this.baseUrl);
  }

  upload(file: File, name?: string): Observable<CategoryImageMeta> {
    const formData = new FormData();
    formData.append('file', file);
    if (name) {
      formData.append('name', name);
    }
    return this.http.post<CategoryImageMeta>(this.baseUrl, formData);
  }

  getImageUrl(id: number): string {
    return `${this.baseUrl}/${id}`;
  }

  getBlobUrl(id: number): Observable<string> {
    const cached = this.blobUrlCache.get(id);
    if (cached) {
      return of(cached);
    }

    return this.http.get(this.getImageUrl(id), { responseType: 'blob' }).pipe(
      map((blob) => {
        const url = URL.createObjectURL(blob);
        this.blobUrlCache.set(id, url);
        return url;
      })
    );
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`).pipe(
      tap(() => {
        const cached = this.blobUrlCache.get(id);
        if (cached) {
          URL.revokeObjectURL(cached);
          this.blobUrlCache.delete(id);
        }
      })
    );
  }
}
