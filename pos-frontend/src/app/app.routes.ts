import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login.component').then((m) => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./features/auth/register/register.component').then((m) => m.RegisterComponent)
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./core/layout/main-layout.component').then((m) => m.MainLayoutComponent),
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/dashboard.component').then((m) => m.DashboardComponent)
      },
      {
        path: 'pos',
        loadComponent: () => import('./features/pos/pos.component').then((m) => m.PosComponent)
      },
      {
        path: 'orders',
        loadComponent: () =>
          import('./features/orders/orders.component').then((m) => m.OrdersComponent)
      },
      {
        path: 'cash-box',
        loadComponent: () =>
          import('./features/cash-box/cash-box.component').then((m) => m.CashBoxComponent)
      },
      {
        path: 'cash-movements',
        loadComponent: () =>
          import('./features/cash-movements/cash-movements.component').then(
            (m) => m.CashMovementsComponent
          )
      },
      {
        path: 'products',
        loadComponent: () =>
          import('./features/products/products.component').then((m) => m.ProductsComponent)
      },
      {
        path: 'categories',
        loadComponent: () =>
          import('./features/categories/categories.component').then((m) => m.CategoriesComponent)
      },
      {
        path: 'clients',
        loadComponent: () =>
          import('./features/clients/clients.component').then((m) => m.ClientsComponent)
      },
      {
        path: 'accounts',
        loadComponent: () =>
          import('./features/accounts/accounts.component').then((m) => m.AccountsComponent)
      },
      {
        path: 'reports',
        loadComponent: () =>
          import('./features/reports/reports.component').then((m) => m.ReportsComponent)
      }
    ]
  },
  { path: '**', redirectTo: 'dashboard' }
];
