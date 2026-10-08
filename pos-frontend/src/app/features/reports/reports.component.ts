import { Component, inject, OnInit, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { DecimalPipe, DatePipe } from '@angular/common';
import { PageHeaderComponent } from '../../shared/components/page-header.component';
import { OrderService } from '../../core/services/order.service';
import { Order } from '../../shared/models/order.model';
import { ORDER_TYPE_LABELS } from '../../shared/enums/order-type.enum';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatTableModule,
    MatCardModule,
    PageHeaderComponent,
    DecimalPipe,
    DatePipe
  ],
  templateUrl: './reports.component.html',
  styles: `.toolbar { display: flex; gap: 1rem; align-items: center; margin-bottom: 1rem; } .total-card { margin-bottom: 1rem; } .full-width { width: 100%; }`
})
export class ReportsComponent implements OnInit {
  private readonly orderService = inject(OrderService);

  readonly typeLabels = ORDER_TYPE_LABELS;
  readonly dateControl = new FormControl(new Date().toISOString().slice(0, 10), { nonNullable: true });
  readonly orders = signal<Order[]>([]);
  readonly total = signal(0);
  readonly displayedColumns = ['orderNumber', 'customerName', 'orderType', 'totalAmount', 'creationDate'];

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    const date = this.dateControl.value;
    this.orderService.getReportList(date).subscribe({
      next: (data) => this.orders.set(data),
      error: () => this.orders.set([])
    });
    this.orderService.getReportTotal(date).subscribe({
      next: (t) => this.total.set(t ?? 0),
      error: () => this.total.set(0)
    });
  }

  typeLabel(type: Order['orderType']): string {
    return this.typeLabels[type];
  }
}
