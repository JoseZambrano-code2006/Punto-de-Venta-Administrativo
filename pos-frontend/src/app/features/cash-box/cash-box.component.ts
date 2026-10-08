import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { DecimalPipe, DatePipe } from '@angular/common';
import { PageHeaderComponent } from '../../shared/components/page-header.component';
import { CashBoxService } from '../../core/services/cash-box.service';
import { AccountService } from '../../core/services/account.service';
import { CashBoxStateService } from '../../core/services/cash-box-state.service';
import { GeneralCashBox } from '../../shared/models/general-cash-box.model';
import { Account } from '../../shared/models/account.model';
import { CashBoxStatus, CASH_BOX_STATUS_LABELS } from '../../shared/enums/cash-box-status.enum';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog.component';

@Component({
  selector: 'app-cash-box',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatTableModule,
    MatSnackBarModule,
    PageHeaderComponent,
    DecimalPipe,
    DatePipe
  ],
  templateUrl: './cash-box.component.html',
  styleUrl: './cash-box.component.scss'
})
export class CashBoxComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly cashBoxService = inject(CashBoxService);
  private readonly accountService = inject(AccountService);
  private readonly cashBoxState = inject(CashBoxStateService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  readonly statusLabels = CASH_BOX_STATUS_LABELS;
  readonly CashBoxStatus = CashBoxStatus;
  readonly openBox = signal<GeneralCashBox | null>(null);
  readonly history = signal<GeneralCashBox[]>([]);
  readonly accounts = signal<Account[]>([]);
  readonly displayedColumns = ['id', 'account', 'saldoInicial', 'saldoFinal', 'estado', 'opening'];

  readonly openForm = this.fb.nonNullable.group({
    accountId: [null as number | null, Validators.required],
    saldoInicial: [0, [Validators.required, Validators.min(0)]]
  });

  ngOnInit(): void {
    this.accountService.getAll().subscribe((data) => this.accounts.set(data));
    this.refresh();
  }

  refresh(): void {
    this.cashBoxService.getAll().subscribe((data) => this.history.set(data));
    this.cashBoxState.refreshOpenCashBox().subscribe({
      next: (box) => {
        this.openBox.set(box);
        this.cashBoxState.setOpenCashBox(box);
      },
      error: () => {
        this.openBox.set(null);
        this.cashBoxState.setOpenCashBox(null);
      }
    });
  }

  openCashBox(): void {
    if (this.openForm.invalid) return;
    const raw = this.openForm.getRawValue();
    this.cashBoxService
      .open({
        account: { id: raw.accountId! },
        saldoInicial: raw.saldoInicial
      })
      .subscribe({
        next: (box) => {
          this.snackBar.open('Caja abierta', 'OK', { duration: 2500 });
          this.openBox.set(box);
          this.cashBoxState.setOpenCashBox(box);
          this.refresh();
        },
        error: (err) =>
          this.snackBar.open(err.error?.message ?? 'Error al abrir caja', 'Cerrar', { duration: 3000 })
      });
  }

  closeCashBox(): void {
    const box = this.openBox();
    if (!box?.id) return;

    this.dialog
      .open(ConfirmDialogComponent, {
        data: { title: 'Cerrar caja', message: '¿Confirmar cierre de caja?' }
      })
      .afterClosed()
      .subscribe((confirmed) => {
        if (!confirmed) return;
        this.cashBoxService.close(box.id!).subscribe({
          next: (closed) => {
            this.snackBar.open(`Caja cerrada. Saldo final: S/ ${closed.saldoFinal}`, 'OK', {
              duration: 4000
            });
            this.openBox.set(null);
            this.cashBoxState.setOpenCashBox(null);
            this.refresh();
          },
          error: (err) =>
            this.snackBar.open(err.error?.message ?? 'Error al cerrar', 'Cerrar', { duration: 3000 })
        });
      });
  }

  accountName(box: GeneralCashBox): string {
    const acc = box.account as Account | undefined;
    return acc?.name ?? `#${(box.account as { id: number })?.id}`;
  }

  statusLabel(estado: CashBoxStatus): string {
    return this.statusLabels[estado];
  }
}
