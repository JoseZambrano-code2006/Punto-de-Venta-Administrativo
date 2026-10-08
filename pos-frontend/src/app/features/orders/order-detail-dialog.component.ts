import { Component, inject, OnInit, signal } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { DecimalPipe, DatePipe } from '@angular/common';
import { Order } from '../../shared/models/order.model';
import { OrderDetail } from '../../shared/models/order-detail.model';
import { Product } from '../../shared/models/product.model';
import { ORDER_TYPE_LABELS } from '../../shared/enums/order-type.enum';
import { OrderDetailService } from '../../core/services/order-detail.service';
import { OrderReceiptPdfService } from '../../core/services/order-receipt-pdf.service';

@Component({
  selector: 'app-order-detail-dialog',
  standalone: true,
  imports: [MatDialogModule, MatButtonModule, MatIconModule, DecimalPipe, DatePipe],
  template: `
    <h2 mat-dialog-title>Orden #{{ data.orderNumber }}</h2>
    <mat-dialog-content>
      <p><strong>Cliente:</strong> {{ data.customerName }}</p>
      <p><strong>Documento:</strong> {{ data.customerDocument || '—' }}</p>
      <p><strong>Tipo:</strong> {{ typeLabels[data.orderType] }}</p>
      <p><strong>Total:</strong> S/ {{ data.totalAmount | number:'1.2-2' }}</p>
      <p><strong>Fecha:</strong> {{ data.creationDate | date:'short' }}</p>
      <h3>Detalle</h3>
      <ul>
        @for (d of details(); track d.id) {
          <li>
            {{ productName(d) }} x {{ d.quantity }} — S/ {{ d.subtotal | number:'1.2-2' }}
            @if (d.kitchenNotes) { <em>({{ d.kitchenNotes }})</em> }
          </li>
        }
      </ul>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="exportReceipt()" [disabled]="!details().length">
        <mat-icon>picture_as_pdf</mat-icon>
        Exportar boleta PDF
      </button>
      <button mat-button mat-dialog-close>Cerrar</button>
    </mat-dialog-actions>
  `
})
export class OrderDetailDialogComponent implements OnInit {
  readonly data = inject<Order>(MAT_DIALOG_DATA);
  private readonly orderDetailService = inject(OrderDetailService);
  private readonly receiptPdfService = inject(OrderReceiptPdfService);
  readonly typeLabels = ORDER_TYPE_LABELS;
  readonly details = signal<OrderDetail[]>([]);

  ngOnInit(): void {
    if (this.data.details?.length) {
      this.details.set(this.data.details);
    } else if (this.data.id) {
      this.orderDetailService.getByOrder(this.data.id).subscribe((d) => this.details.set(d));
    }
  }

  productName(detail: OrderDetail): string {
    const p = detail.product as Product | undefined;
    return p?.name ?? `Producto #${(detail.product as { id: number })?.id}`;
  }

  exportReceipt(): void {
    try {
      this.receiptPdfService.export(this.data, this.details());
    } catch {
      // El boton permanece deshabilitado sin detalle cargado.
    }
  }
}
