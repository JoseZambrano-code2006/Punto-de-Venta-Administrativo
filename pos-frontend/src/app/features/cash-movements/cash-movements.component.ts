import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { DecimalPipe, DatePipe } from '@angular/common';
import { PageHeaderComponent } from '../../shared/components/page-header.component';
import { CashMovementService } from '../../core/services/cash-movement.service';
import { CashBoxStateService } from '../../core/services/cash-box-state.service';
import { CashMovement } from '../../shared/models/cash-movement.model';
import { MovementType, MOVEMENT_TYPE_LABELS } from '../../shared/enums/movement-type.enum';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog.component';

@Component({
  selector: 'app-cash-movements',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatSnackBarModule,
    PageHeaderComponent,
    DecimalPipe,
    DatePipe
  ],
  templateUrl: './cash-movements.component.html',
  styles: `.form-row { display: flex; gap: 1rem; flex-wrap: wrap; margin-bottom: 1rem; align-items: flex-start; } .field { min-width: 180px; } .full-width { width: 100%; }`
})
export class CashMovementsComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly movementService = inject(CashMovementService);
  private readonly cashBoxState = inject(CashBoxStateService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  readonly typeLabels = MOVEMENT_TYPE_LABELS;
  readonly MovementType = MovementType;
  readonly movements = signal<CashMovement[]>([]);
  readonly cashBoxId = signal<number | null>(null);
  readonly displayedColumns = ['type', 'amount', 'description', 'creationDate', 'actions'];

  readonly form = this.fb.nonNullable.group({
    type: [MovementType.INCOME, Validators.required],
    amount: [0, [Validators.required, Validators.min(0.01)]],
    description: ['']
  });

  ngOnInit(): void {
    this.cashBoxState.refreshOpenCashBox().subscribe({
      next: (box) => {
        if (box?.id) {
          this.cashBoxId.set(box.id);
          this.load(box.id);
        }
      },
      error: () => this.cashBoxId.set(null)
    });
  }

  load(cashBoxId: number): void {
    this.movementService.getByCashBox(cashBoxId).subscribe({
      next: (data) => this.movements.set(data),
      error: () => this.snackBar.open('Error al cargar movimientos', 'Cerrar', { duration: 3000 })
    });
  }

  create(): void {
    const boxId = this.cashBoxId();
    if (!boxId || this.form.invalid) {
      this.snackBar.open('Debe haber una caja abierta', 'Cerrar', { duration: 3000 });
      return;
    }

    const raw = this.form.getRawValue();
    this.movementService
      .create({
        cashBox: { id: boxId },
        type: raw.type,
        amount: raw.amount,
        description: raw.description
      })
      .subscribe({
        next: () => {
          this.snackBar.open('Movimiento registrado', 'OK', { duration: 2500 });
          this.form.patchValue({ amount: 0, description: '' });
          this.load(boxId);
        },
        error: (err) =>
          this.snackBar.open(err.error?.message ?? 'Error al registrar', 'Cerrar', { duration: 3000 })
      });
  }

  delete(movement: CashMovement): void {
    this.dialog
      .open(ConfirmDialogComponent, {
        data: { title: 'Eliminar movimiento', message: '¿Eliminar este movimiento?' }
      })
      .afterClosed()
      .subscribe((confirmed) => {
        if (!confirmed || !movement.id) return;
        this.movementService.delete(movement.id).subscribe({
          next: () => {
            this.snackBar.open('Movimiento eliminado', 'OK', { duration: 2500 });
            if (this.cashBoxId()) this.load(this.cashBoxId()!);
          },
          error: () => this.snackBar.open('Error al eliminar', 'Cerrar', { duration: 3000 })
        });
      });
  }

  typeLabel(type: MovementType): string {
    return this.typeLabels[type];
  }
}
