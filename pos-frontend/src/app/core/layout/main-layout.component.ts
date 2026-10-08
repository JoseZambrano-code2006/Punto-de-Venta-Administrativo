import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AuthService } from '../auth/auth.service';
import { CashBoxStateService } from '../services/cash-box-state.service';
import { CashBoxStatus } from '../../shared/enums/cash-box-status.enum';

interface NavItem {
  label: string;
  route: string;
  icon: string;
}

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    MatButtonModule
  ],
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.scss'
})
export class MainLayoutComponent implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly cashBoxState = inject(CashBoxStateService);
  private readonly breakpointObserver = inject(BreakpointObserver);

  readonly username = this.auth.getUsername() ?? 'Usuario';
  readonly sidenavOpened = signal(true);
  readonly hasOpenCashBox = signal(false);

  readonly navItems: NavItem[] = [
    { label: 'Dashboard', route: '/dashboard', icon: 'dashboard' },
    { label: 'Punto de venta', route: '/pos', icon: 'point_of_sale' },
    { label: 'Órdenes', route: '/orders', icon: 'receipt_long' },
    { label: 'Caja', route: '/cash-box', icon: 'account_balance_wallet' },
    { label: 'Movimientos', route: '/cash-movements', icon: 'swap_horiz' },
    { label: 'Productos', route: '/products', icon: 'inventory_2' },
    { label: 'Categorías', route: '/categories', icon: 'category' },
    { label: 'Clientes', route: '/clients', icon: 'groups' },
    { label: 'Cuentas', route: '/accounts', icon: 'manage_accounts' },
    { label: 'Reportes', route: '/reports', icon: 'bar_chart' }
  ];

  ngOnInit(): void {
    this.breakpointObserver.observe([Breakpoints.Handset]).subscribe((result) => {
      this.sidenavOpened.set(!result.matches);
    });

    this.cashBoxState.refreshOpenCashBox().subscribe({
      next: (box) => this.hasOpenCashBox.set(box?.estado === CashBoxStatus.OPEN),
      error: () => this.hasOpenCashBox.set(false)
    });

    this.cashBoxState.openCashBox$.subscribe((box) => {
      this.hasOpenCashBox.set(box?.estado === CashBoxStatus.OPEN);
    });
  }

  logout(): void {
    this.auth.logout();
  }
}
