import { Injectable, inject } from '@angular/core';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { GeneralCashBox } from '../../shared/models/general-cash-box.model';
import { CashBoxService } from './cash-box.service';

@Injectable({ providedIn: 'root' })
export class CashBoxStateService {
  private readonly cashBoxService = inject(CashBoxService);
  private readonly openCashBoxSubject = new BehaviorSubject<GeneralCashBox | null>(null);

  readonly openCashBox$ = this.openCashBoxSubject.asObservable();

  refreshOpenCashBox(): Observable<GeneralCashBox | null> {
    return this.cashBoxService.getOpen().pipe(
      tap({
        next: (box) => this.openCashBoxSubject.next(box),
        error: () => this.openCashBoxSubject.next(null)
      })
    );
  }

  setOpenCashBox(box: GeneralCashBox | null): void {
    this.openCashBoxSubject.next(box);
  }

  getOpenCashBox(): GeneralCashBox | null {
    return this.openCashBoxSubject.value;
  }
}
