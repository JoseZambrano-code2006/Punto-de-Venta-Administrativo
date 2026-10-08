import { Component, inject, OnInit, signal } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { DecimalPipe, DatePipe } from '@angular/common';
import { PageHeaderComponent } from '../../shared/components/page-header.component';
import { OrderService } from '../../core/services/order.service';
import { CashBoxStateService } from '../../core/services/cash-box-state.service';
import { Order } from '../../shared/models/order.model';
import { OrderDetail } from '../../shared/models/order-detail.model';
import { ORDER_TYPE_LABELS } from '../../shared/enums/order-type.enum';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog.component';
import { OrderDetailDialogComponent } from './order-detail-dialog.component';
import { OrderDetailService } from '../../core/services/order-detail.service';
import { OrderReceiptPdfService } from '../../core/services/order-receipt-pdf.service';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    PageHeaderComponent,
    DecimalPipe,
    DatePipe
  ],
  templateUrl: './orders.component.html',
  styles: `.full-width { width: 100%; }`
})
export class OrdersComponent implements OnInit {
  private readonly orderService = inject(OrderService);
  private readonly orderDetailService = inject(OrderDetailService);
  private readonly receiptPdfService = inject(OrderReceiptPdfService);
  private readonly cashBoxState = inject(CashBoxStateService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  readonly typeLabels = ORDER_TYPE_LABELS;
  readonly orders = signal<Order[]>([]);
  readonly displayedColumns = ['orderNumber', 'customerName', 'orderType', 'totalAmount', 'creationDate', 'actions'];

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.cashBoxState.refreshOpenCashBox().subscribe({
      next: (box) => {
        if (box?.id) {
          this.orderService.getByCashBox(box.id).subscribe({
            next: (data) => this.orders.set(data),
            error: () => this.loadAll()
          });
        } else {
          this.loadAll();
        }
      },
      error: () => this.loadAll()
    });
  }

  loadAll(): void {
    this.orderService.getAll().subscribe({
      next: (data) => this.orders.set(data),
      error: () => this.snackBar.open('Error al cargar órdenes', 'Cerrar', { duration: 3000 })
    });
  }

  view(order: Order): void {
    this.dialog.open(OrderDetailDialogComponent, { width: '520px', data: order });
  }

  delete(order: Order): void {
    this.dialog
      .open(ConfirmDialogComponent, {
        data: { title: 'Eliminar orden', message: `¿Eliminar orden #${order.orderNumber}?` }
      })
      .afterClosed()
      .subscribe((confirmed) => {
        if (!confirmed || !order.id) return;
        this.orderService.delete(order.id).subscribe({
          next: () => {
            this.snackBar.open('Orden eliminada', 'OK', { duration: 2500 });
            this.load();
          },
          error: (err) =>
            this.snackBar.open(err.error?.message ?? 'Error al eliminar', 'Cerrar', { duration: 3000 })
        });
      });
  }

  typeLabel(type: Order['orderType']): string {
    return this.typeLabels[type];
  }

  exportReceipt(order: Order): void {
    const exportPdf = (details: OrderDetail[]) => {
      try {
        this.receiptPdfService.export(order, details);
      } catch {
        this.snackBar.open('No se pudo generar la boleta PDF', 'Cerrar', { duration: 3000 });
      }
    };

    if (order.details?.length) {
      exportPdf(order.details);
      return;
    }

    if (!order.id) {
      this.snackBar.open('La orden no tiene identificador', 'Cerrar', { duration: 3000 });
      return;
    }

    this.orderDetailService.getByOrder(order.id).subscribe({
      next: (details) => {
        if (!details.length) {
          this.snackBar.open('La orden no tiene detalle para exportar', 'Cerrar', { duration: 3000 });
          return;
        }
        exportPdf(details);
      },
      error: () => this.snackBar.open('Error al cargar el detalle de la orden', 'Cerrar', { duration: 3000 })
    });
  }
}
