import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { Product } from '../../shared/models/product.model';
import { Category } from '../../shared/models/category.model';
import { ImagePickerComponent } from './image-picker.component';

export interface ProductFormDialogData {
  product?: Product;
  categories: Category[];
}

@Component({
  selector: 'app-product-form-dialog',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    ImagePickerComponent
  ],
  template: `
    <h2 mat-dialog-title>{{ data.product ? 'Editar' : 'Nuevo' }} producto</h2>
    <form [formGroup]="form" (ngSubmit)="save()">
      <mat-dialog-content>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Nombre</mat-label>
          <input matInput formControlName="name" />
        </mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Descripción</mat-label>
          <textarea matInput rows="2" formControlName="description"></textarea>
        </mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Precio</mat-label>
          <input matInput type="number" formControlName="price" />
        </mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Categoría</mat-label>
          <mat-select formControlName="categoryId">
            @for (cat of data.categories; track cat.id) {
              <mat-option [value]="cat.id">{{ cat.name }}</mat-option>
            }
          </mat-select>
        </mat-form-field>

        <p class="section-label">Imagen del producto</p>
        <app-image-picker
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
export class ProductFormDialogComponent {
  private readonly fb = inject(FormBuilder);
  readonly data = inject<ProductFormDialogData>(MAT_DIALOG_DATA);
  readonly dialogRef = inject(MatDialogRef<ProductFormDialogComponent, Product>);

  readonly selectedImageId = signal<number | null>(
    this.data.product?.image?.id ?? null
  );

  readonly form = this.fb.nonNullable.group({
    name: [this.data.product?.name ?? '', Validators.required],
    description: [this.data.product?.description ?? ''],
    price: [this.data.product?.price ?? 0, [Validators.required, Validators.min(0)]],
    categoryId: [this.data.product?.category?.id ?? null as number | null, Validators.required]
  });

  onImageSelected(imageId: number | null): void {
    this.selectedImageId.set(imageId);
  }

  save(): void {
    if (this.form.invalid) return;
    const raw = this.form.getRawValue();
    const imageId = this.selectedImageId();
    this.dialogRef.close({
      ...this.data.product,
      name: raw.name,
      description: raw.description,
      price: raw.price,
      category: { id: raw.categoryId! },
      image: imageId ? { id: imageId } : undefined
    });
  }
}
