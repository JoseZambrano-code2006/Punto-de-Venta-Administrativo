import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { Cliente } from '../../shared/models/cliente.model';

@Component({
  selector: 'app-client-form-dialog',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule
  ],
  template: `
    <h2 mat-dialog-title>Nuevo cliente</h2>
    <form [formGroup]="form" (ngSubmit)="save()">
      <mat-dialog-content>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Nombre completo</mat-label>
          <input matInput formControlName="nombreCompleto" />
        </mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Tipo documento</mat-label>
          <mat-select formControlName="tipoDocumento">
            <mat-option value="DNI">DNI</mat-option>
            <mat-option value="RUC">RUC</mat-option>
            <mat-option value="S/D">S/D</mat-option>
          </mat-select>
        </mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Número documento</mat-label>
          <input matInput formControlName="numeroDocumento" />
        </mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Dirección</mat-label>
          <input matInput formControlName="direccion" />
        </mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Correo</mat-label>
          <input matInput type="email" formControlName="correo" />
        </mat-form-field>
      </mat-dialog-content>
      <mat-dialog-actions align="end">
        <button mat-button type="button" mat-dialog-close>Cancelar</button>
        <button mat-flat-button color="primary" type="submit">Guardar</button>
      </mat-dialog-actions>
    </form>
  `,
  styles: `.full-width { width: 100%; }`
})
export class ClientFormDialogComponent {
  private readonly fb = inject(FormBuilder);
  readonly dialogRef = inject(MatDialogRef<ClientFormDialogComponent, Cliente>);
  readonly data = inject<Cliente | null>(MAT_DIALOG_DATA, { optional: true });

  readonly form = this.fb.nonNullable.group({
    nombreCompleto: [this.data?.nombreCompleto ?? '', Validators.required],
    tipoDocumento: [this.data?.tipoDocumento ?? 'DNI', Validators.required],
    numeroDocumento: [this.data?.numeroDocumento ?? '', Validators.required],
    direccion: [this.data?.direccion ?? ''],
    correo: [this.data?.correo ?? '']
  });

  save(): void {
    if (this.form.invalid) return;
    this.dialogRef.close(this.form.getRawValue());
  }
}
