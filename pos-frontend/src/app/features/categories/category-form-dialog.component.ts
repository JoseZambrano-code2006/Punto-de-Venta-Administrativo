import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialogRef,
  MatDialogModule
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { Category } from '../../shared/models/category.model';
import { CategoryImagePickerComponent } from './category-image-picker.component';

@Component({
  selector: 'app-category-form-dialog',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    CategoryImagePickerComponent
  ],
  template: `
    <h2 mat-dialog-title>{{ data ? 'Editar' : 'Nueva' }} categoría</h2>
    <form [formGroup]="form" (ngSubmit)="save()">
      <mat-dialog-content>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Nombre</mat-label>
          <input matInput formControlName="name" />
        </mat-form-field>

        <p class="section-label">Imagen de la categoría</p>
        <app-category-image-picker
          [initialImageId]="selectedImageId()"
          (imageSelected)="onImageSelected($event)"
        />
      </mat-dialog-content>
      <mat-dialog-actions align="end">
        <button mat-button type="button" mat-dialog-close>Cancelar</button>
        <button mat-flat-button color="primary" type="submit">Guardar</button>
      </mat-dialog-actions>
    </form>
  `,
  styles: `
    .full-width { width: 100%; }
    .section-label {
      margin: 0 0 8px;
      font-weight: 500;
      color: var(--mat-sys-on-surface);
    }
  `
})
export class CategoryFormDialogComponent {
  private readonly fb = inject(FormBuilder);
  readonly data = inject<Category | null>(MAT_DIALOG_DATA, { optional: true });
  readonly dialogRef = inject(MatDialogRef<CategoryFormDialogComponent, Category>);

  readonly selectedImageId = signal<number | null>(this.data?.imagenCat?.id ?? null);

  readonly form = this.fb.nonNullable.group({
    name: [this.data?.name ?? '', Validators.required]
  });

  onImageSelected(imageId: number | null): void {
    this.selectedImageId.set(imageId);
  }

  save(): void {
    if (this.form.invalid) return;
    const imageId = this.selectedImageId();
    this.dialogRef.close({
      ...this.data,
      ...this.form.getRawValue(),
      imagenCat: imageId ? { id: imageId } : undefined
    });
  }
}
