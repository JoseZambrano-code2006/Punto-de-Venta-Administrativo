import { Component, inject, OnInit, signal } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { PageHeaderComponent } from '../../shared/components/page-header.component';
import { CategoryService } from '../../core/services/category.service';
import { Category } from '../../shared/models/category.model';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog.component';
import { CategoryFormDialogComponent } from './category-form-dialog.component';
import { CategoryImageThumbComponent } from '../../shared/components/category-image-thumb.component';

@Component({
  selector: 'app-categories',
  standalone: true,
  imports: [
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    PageHeaderComponent,
    CategoryImageThumbComponent
  ],
  templateUrl: './categories.component.html',
  styles: `.full-width { width: 100%; } .image-col { width: 56px; }`
})
export class CategoriesComponent implements OnInit {
  private readonly categoryService = inject(CategoryService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  readonly displayedColumns = ['image', 'name', 'actions'];
  readonly categories = signal<Category[]>([]);

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.categoryService.getAll().subscribe({
      next: (data) => this.categories.set(data),
      error: () => this.snackBar.open('Error al cargar categorías', 'Cerrar', { duration: 3000 })
    });
  }

  openForm(category?: Category): void {
    this.dialog
      .open(CategoryFormDialogComponent, { width: '480px', data: category ?? null })
      .afterClosed()
      .subscribe((result) => {
        if (!result) return;
        const request$ = category?.id
          ? this.categoryService.update(category.id, result)
          : this.categoryService.create(result);
        request$.subscribe({
          next: () => {
            this.snackBar.open('Categoría guardada', 'OK', { duration: 2500 });
            this.load();
          },
          error: () => this.snackBar.open('Error al guardar', 'Cerrar', { duration: 3000 })
        });
      });
  }

  delete(category: Category): void {
    this.dialog
      .open(ConfirmDialogComponent, {
        data: { title: 'Eliminar categoría', message: `¿Eliminar "${category.name}"?` }
      })
      .afterClosed()
      .subscribe((confirmed) => {
        if (!confirmed || !category.id) return;
        this.categoryService.delete(category.id).subscribe({
          next: () => {
            this.snackBar.open('Categoría eliminada', 'OK', { duration: 2500 });
            this.load();
          },
          error: () => this.snackBar.open('Error al eliminar', 'Cerrar', { duration: 3000 })
        });
      });
  }
}
