import { Component, inject, OnInit, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import { PageHeaderComponent } from '../../shared/components/page-header.component';
import { ClienteService } from '../../core/services/cliente.service';
import { Cliente } from '../../shared/models/cliente.model';
import { ClientFormDialogComponent } from './client-form-dialog.component';

@Component({
  selector: 'app-clients',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSnackBarModule,
    PageHeaderComponent
  ],
  templateUrl: './clients.component.html',
  styles: `.filters { display: flex; gap: 1rem; margin-bottom: 1rem; flex-wrap: wrap; } .filter { min-width: 220px; } .full-width { width: 100%; }`
})
export class ClientsComponent implements OnInit {
  private readonly clienteService = inject(ClienteService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  readonly displayedColumns = ['nombreCompleto', 'tipoDocumento', 'numeroDocumento', 'correo'];
  readonly clients = signal<Cliente[]>([]);
  readonly nameSearch = new FormControl('', { nonNullable: true });
  readonly docSearch = new FormControl('', { nonNullable: true });

  ngOnInit(): void {
    this.load();

    this.nameSearch.valueChanges
      .pipe(debounceTime(300), distinctUntilChanged())
      .subscribe((name) => {
        if (name.trim()) {
          this.clienteService.searchByNombre(name).subscribe({
            next: (data) => this.clients.set(data),
            error: () => this.clients.set([])
          });
        } else {
          this.load();
        }
      });
  }

  load(): void {
    this.clienteService.getAll().subscribe({
      next: (data) => this.clients.set(data),
      error: () => this.snackBar.open('Error al cargar clientes', 'Cerrar', { duration: 3000 })
    });
  }

  searchByDocument(): void {
    const doc = this.docSearch.value.trim();
    if (!doc) {
      this.load();
      return;
    }
    this.clienteService.getByDocumento(doc).subscribe({
      next: (cliente) => this.clients.set([cliente]),
      error: () => {
        this.clients.set([]);
        this.snackBar.open('Cliente no encontrado', 'Cerrar', { duration: 3000 });
      }
    });
  }

  openForm(): void {
    this.dialog
      .open(ClientFormDialogComponent, { width: '480px' })
      .afterClosed()
      .subscribe((result) => {
        if (!result) return;
        this.clienteService.create(result).subscribe({
          next: () => {
            this.snackBar.open('Cliente creado', 'OK', { duration: 2500 });
            this.load();
          },
          error: () => this.snackBar.open('Error al crear cliente', 'Cerrar', { duration: 3000 })
        });
      });
  }
}
