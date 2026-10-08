import { Injectable } from '@angular/core';
import { jsPDF } from 'jspdf';
import { autoTable } from 'jspdf-autotable';
import { environment } from '../../../environments/environment';
import { Order } from '../../shared/models/order.model';
import { OrderDetail } from '../../shared/models/order-detail.model';
import { Product } from '../../shared/models/product.model';
import { ORDER_TYPE_LABELS } from '../../shared/enums/order-type.enum';

type JsPdfWithAutoTable = jsPDF & { lastAutoTable?: { finalY: number } };

@Injectable({ providedIn: 'root' })
export class OrderReceiptPdfService {
  private readonly receiptWidthMm = 80;
  private readonly marginMm = 4;

  export(order: Order, details: OrderDetail[]): void {
    if (!details.length) {
      throw new Error('La orden no tiene detalle para exportar.');
    }

    const estimatedHeight = Math.max(70 + details.length * 8, 120);
    const doc = new jsPDF({
      orientation: 'portrait',
      unit: 'mm',
      format: [this.receiptWidthMm, estimatedHeight]
    });

    const centerX = this.receiptWidthMm / 2;
    const contentWidth = this.receiptWidthMm - this.marginMm * 2;
    let y = this.marginMm;
    const { receipt } = environment;

    doc.setFont('helvetica', 'bold');
    doc.setFontSize(10);
    doc.text(receipt.businessName, centerX, y, { align: 'center' });
    y += 4;

    doc.setFont('helvetica', 'normal');
    doc.setFontSize(8);
    doc.text(`RUC: ${receipt.ruc}`, centerX, y, { align: 'center' });
    y += 3.5;

    const addressLines = doc.splitTextToSize(receipt.address, contentWidth);
    for (const line of addressLines) {
      doc.text(line, centerX, y, { align: 'center' });
      y += 3.5;
    }

    doc.text(`Tel: ${receipt.phone}`, centerX, y, { align: 'center' });
    y += 5;

    doc.setDrawColor(0);
    doc.line(this.marginMm, y, this.receiptWidthMm - this.marginMm, y);
    y += 4;

    doc.setFont('helvetica', 'bold');
    doc.setFontSize(9);
    doc.text('BOLETA DE VENTA', centerX, y, { align: 'center' });
    y += 4;

    doc.setFont('helvetica', 'normal');
    doc.setFontSize(8);
    const docNumber = this.formatDocumentNumber(order.orderNumber);
    doc.text(`Nro: ${docNumber}`, this.marginMm, y);
    y += 3.5;
    doc.text(`Fecha: ${this.formatDate(order.creationDate)}`, this.marginMm, y);
    y += 4;

    const customerLines = doc.splitTextToSize(
      `Cliente: ${order.customerName ?? '-'}`,
      contentWidth
    );
    for (const line of customerLines) {
      doc.text(line, this.marginMm, y);
      y += 3.5;
    }

    doc.text(`Doc: ${order.customerDocument || '-'}`, this.marginMm, y);
    y += 4;

    const tableBody = details.map((detail) => [
      String(detail.quantity),
      this.productName(detail),
      this.formatMoney(detail.unitPrice),
      this.formatMoney(detail.subtotal)
    ]);

    autoTable(doc, {
      startY: y,
      margin: { left: this.marginMm, right: this.marginMm },
      tableWidth: contentWidth,
      theme: 'plain',
      styles: { fontSize: 7, cellPadding: 0.8, overflow: 'linebreak' },
      headStyles: { fontStyle: 'bold', fillColor: [255, 255, 255], textColor: 0 },
      head: [['Cant', 'Descripcion', 'P.Unit', 'Importe']],
      body: tableBody,
      columnStyles: {
        0: { cellWidth: 8, halign: 'center' },
        1: { cellWidth: 34 },
        2: { cellWidth: 14, halign: 'right' },
        3: { cellWidth: 16, halign: 'right' }
      }
    });

    y = (doc as JsPdfWithAutoTable).lastAutoTable?.finalY ?? y;
    y += 3;
    doc.line(this.marginMm, y, this.receiptWidthMm - this.marginMm, y);
    y += 4;

    doc.setFont('helvetica', 'bold');
    doc.setFontSize(9);
    doc.text(
      `TOTAL: S/ ${this.formatMoney(order.totalAmount)}`,
      this.receiptWidthMm - this.marginMm,
      y,
      { align: 'right' }
    );
    y += 4;

    doc.setFont('helvetica', 'normal');
    doc.setFontSize(8);
    doc.text(`Tipo: ${ORDER_TYPE_LABELS[order.orderType]}`, this.marginMm, y);
    y += 5;

    doc.setFontSize(7);
    doc.text('Gracias por su compra', centerX, y, { align: 'center' });
    y += 3;

    const footerLines = doc.splitTextToSize(
      'Documento interno - no valido como comprobante fiscal',
      contentWidth
    );
    for (const line of footerLines) {
      doc.text(line, centerX, y, { align: 'center' });
      y += 3;
    }

    const fileName = `boleta-${order.orderNumber ?? order.id ?? 'venta'}.pdf`;
    doc.save(fileName);
  }

  private formatDocumentNumber(orderNumber?: number): string {
    const num = orderNumber ?? 0;
    return `B001-${String(num).padStart(8, '0')}`;
  }

  private formatDate(value?: string): string {
    if (!value) return '-';
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) return value;
    return date.toLocaleString('es-PE', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  private toNumber(value?: number | string): number {
    const n = Number(value);
    return Number.isFinite(n) ? n : 0;
  }

  private formatMoney(value?: number | string): string {
    const amount = this.toNumber(value);
    return amount.toLocaleString('es-PE', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  }

  private productName(detail: OrderDetail): string {
    const product = detail.product as Product | undefined;
    const baseName = product?.name ?? `Producto #${(detail.product as { id: number })?.id ?? '?'}`;
    return detail.kitchenNotes ? `${baseName} (${detail.kitchenNotes})` : baseName;
  }
}
