import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { Account } from '../../shared/models/account.model';

@Component({
  selector: 'app-account-form-dialog',
  standalone: true,
  imports: [ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  template: `
    <h2 mat-dialog-title>{{ data ? 'Editar' : 'Nueva' }} cuenta</h2>
    <form [formGroup]="form" (ngSubmit)="save()">
      <mat-dialog-content>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Nombre</mat-label>
          <input matInput formControlName="name" />
        </mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Correo</mat-label>
          <input matInput type="email" formControlName="mail" />
        </mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Contraseña</mat-label>
          <input matInput type="password" formControlName="password" [placeholder]="data ? 'Dejar vacío para no cambiar' : ''" />
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
export class AccountFormDialogComponent {
  private readonly fb = inject(FormBuilder);
  readonly data = inject<Account | null>(MAT_DIALOG_DATA, { optional: true });
  readonly dialogRef = inject(MatDialogRef<AccountFormDialogComponent, Account>);

  readonly isEdit = !!this.data?.id;

  readonly form = this.fb.nonNullable.group({
    name: [this.data?.name ?? '', Validators.required],
    mail: [this.data?.mail ?? '', [Validators.required, Validators.email]],
    password: ['', this.isEdit ? [] : Validators.required]
  });

  save(): void {
    if (this.form.invalid) return;
    const raw = this.form.getRawValue();
    const payload: Account = {
      ...this.data,
      name: raw.name,
      mail: raw.mail
    };
    if (raw.password) {
      payload.password = raw.password;
    }
    this.dialogRef.close(payload);
  }
}
