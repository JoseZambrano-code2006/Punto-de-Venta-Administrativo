import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { PageHeaderComponent } from '../../shared/components/page-header.component';
import { CashBoxStateService } from '../../core/services/cash-box-state.service';
import { OrderService } from '../../core/services/order.service';
import { GeneralCashBox } from '../../shared/models/general-cash-box.model';
import { CashBoxStatus } from '../../shared/enums/cash-box-status.enum';
import { DecimalPipe } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterLink, MatCardModule, MatButtonModule, MatIconModule, PageHeaderComponent, DecimalPipe],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  private readonly cashBoxState = inject(CashBoxStateService);
  private readonly orderService = inject(OrderService);

  readonly openCashBox = signal<GeneralCashBox | null>(null);
  readonly todayTotal = signal(0);

  ngOnInit(): void {
    this.cashBoxState.refreshOpenCashBox().subscribe({
      next: (box) => this.openCashBox.set(box),
      error: () => this.openCashBox.set(null)
    });

    const today = new Date().toISOString().slice(0, 10);
    this.orderService.getReportTotal(today).subscribe({
      next: (total) => this.todayTotal.set(total ?? 0),
      error: () => this.todayTotal.set(0)
    });
  }

  isOpen(): boolean {
    return this.openCashBox()?.estado === CashBoxStatus.OPEN;
  }
}
