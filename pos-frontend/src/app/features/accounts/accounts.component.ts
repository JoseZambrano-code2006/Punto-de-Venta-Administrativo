import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { NavigationEnd, Router, RouterLink } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { filter, Subscription } from 'rxjs';
import { PageHeaderComponent } from '../../shared/components/page-header.component';
import { AuthService } from '../../core/auth/auth.service';
import { RegisteredUser } from '../../shared/models/registered-user.model';

@Component({
  selector: 'app-accounts',
  standalone: true,
  imports: [MatTableModule, MatButtonModule, MatSnackBarModule, PageHeaderComponent, RouterLink],
  templateUrl: './accounts.component.html',
  styles: `.full-width { width: 100%; } .empty { color: var(--mat-sys-on-surface-variant); margin: 1rem 0; }`
})
export class AccountsComponent implements OnInit, OnDestroy {
  private readonly authService = inject(AuthService);
  private readonly snackBar = inject(MatSnackBar);
  private readonly router = inject(Router);

  private routerSub?: Subscription;

  readonly displayedColumns = ['username'];
  readonly users = signal<RegisteredUser[]>([]);

  ngOnInit(): void {
    this.load();
    this.routerSub = this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event) => {
        const url = (event as NavigationEnd).urlAfterRedirects;
        if (url === '/accounts' || url.startsWith('/accounts?')) {
          this.load();
        }
      });
  }

  ngOnDestroy(): void {
    this.routerSub?.unsubscribe();
  }

  load(): void {
    this.authService.getRegisteredUsers().subscribe({
      next: (data) => this.users.set(data),
      error: () => this.snackBar.open('Error al cargar usuarios', 'Cerrar', { duration: 3000 })
    });
  }
}
