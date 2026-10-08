import { Component, inject, OnInit, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { DecimalPipe } from '@angular/common';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import { PageHeaderComponent } from '../../shared/components/page-header.component';
import { ProductService } from '../../core/services/product.service';
import { CategoryService } from '../../core/services/category.service';
import { Product } from '../../shared/models/product.model';
import { Category } from '../../shared/models/category.model';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog.component';
import { ProductFormDialogComponent } from './product-form-dialog.component';
import { ProductImageThumbComponent } from '../../shared/components/product-image-thumb.component';

@Component({
  selector: 'app-products',
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
    ProductImageThumbComponent
  ],
  templateUrl: './products.component.html',
  styles: `.filters { display: flex; gap: 1rem; margin-bottom: 1rem; flex-wrap: wrap; } .filter { min-width: 200px; } .full-width { width: 100%; } .image-col { width: 56px; }`
})
export class ProductsComponent implements OnInit {
  private readonly productService = inject(ProductService);
  private readonly categoryService = inject(CategoryService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  readonly displayedColumns = ['image', 'name', 'category', 'price', 'actions'];
  readonly products = signal<Product[]>([]);
  readonly categories = signal<Category[]>([]);
  readonly searchControl = new FormControl('', { nonNullable: true });
  readonly categoryFilter = new FormControl<number | null>(null);

  ngOnInit(): void {
    this.categoryService.getAll().subscribe((cats) => this.categories.set(cats));
    this.load();

    this.searchControl.valueChanges
      .pipe(debounceTime(300), distinctUntilChanged())
      .subscribe((term) => {
        if (term.trim()) {
          this.productService.search(term).subscribe({
            next: (data) => this.products.set(data),
            error: () => this.load()
          });
        } else {
          this.load();
        }
      });

    this.categoryFilter.valueChanges.subscribe((catId) => {
      if (catId) {
        this.productService.getByCategory(catId).subscribe({
          next: (data) => this.products.set(data)
        });
      } else {
        this.load();
      }
    });
  }

  load(): void {
    this.productService.getAll().subscribe({
      next: (data) => this.products.set(data),
      error: () => this.snackBar.open('Error al cargar productos', 'Cerrar', { duration: 3000 })
    });
  }

  categoryName(product: Product): string {
    const cat = product.category as Category | undefined;
    return cat?.name ?? '—';
  }

  openForm(product?: Product): void {
    this.dialog
      .open(ProductFormDialogComponent, {
        width: '480px',
        data: { product, categories: this.categories() }
      })
      .afterClosed()
      .subscribe((result) => {
        if (!result) return;
        const request$ = product?.id
          ? this.productService.update(product.id, result)
          : this.productService.create(result);
        request$.subscribe({
          next: () => {
            this.snackBar.open('Producto guardado', 'OK', { duration: 2500 });
            this.load();
          },
          error: () => this.snackBar.open('Error al guardar', 'Cerrar', { duration: 3000 })
        });
      });
  }

  delete(product: Product): void {
    this.dialog
      .open(ConfirmDialogComponent, {
        data: { title: 'Eliminar producto', message: `¿Eliminar "${product.name}"?` }
      })
      .afterClosed()
      .subscribe((confirmed) => {
        if (!confirmed || !product.id) return;
        this.productService.delete(product.id).subscribe({
          next: () => {
            this.snackBar.open('Producto eliminado', 'OK', { duration: 2500 });
            this.load();
          },
          error: () => this.snackBar.open('Error al eliminar', 'Cerrar', { duration: 3000 })
        });
      });
  }
}
