import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material/card';
import { DecimalPipe } from '@angular/common';
import { PageHeaderComponent } from '../../shared/components/page-header.component';
import { ProductService } from '../../core/services/product.service';
import { CategoryService } from '../../core/services/category.service';
import { ClienteService } from '../../core/services/cliente.service';
import { OrderService } from '../../core/services/order.service';
import { CashBoxStateService } from '../../core/services/cash-box-state.service';
import { Product } from '../../shared/models/product.model';
import { Category } from '../../shared/models/category.model';
import { Cliente } from '../../shared/models/cliente.model';
import { OrderType, ORDER_TYPE_LABELS } from '../../shared/enums/order-type.enum';
import { CashBoxStatus } from '../../shared/enums/cash-box-status.enum';
import { CategoryImageThumbComponent } from '../../shared/components/category-image-thumb.component';
import { ProductImageThumbComponent } from '../../shared/components/product-image-thumb.component';

interface CartItem {
  product: Product;
  quantity: number;
  kitchenNotes: string;
}

@Component({
  selector: 'app-pos',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatSnackBarModule,
    MatCardModule,
    PageHeaderComponent,
    DecimalPipe,
    CategoryImageThumbComponent,
    ProductImageThumbComponent
  ],
  templateUrl: './pos.component.html',
  styleUrl: './pos.component.scss'
})
export class PosComponent implements OnInit {
  private readonly productService = inject(ProductService);
  private readonly categoryService = inject(CategoryService);
  private readonly clienteService = inject(ClienteService);
  private readonly orderService = inject(OrderService);
  private readonly cashBoxState = inject(CashBoxStateService);
  private readonly snackBar = inject(MatSnackBar);

  readonly orderTypeLabels = ORDER_TYPE_LABELS;
  readonly orderTypes = Object.values(OrderType);
  readonly categories = signal<Category[]>([]);
  readonly products = signal<Product[]>([]);
  readonly clients = signal<Cliente[]>([]);
  readonly cart = signal<CartItem[]>([]);
  readonly selectedCategoryId = signal<number | null>(null);
  readonly hasOpenCashBox = signal(false);

  readonly clientControl = new FormControl<number>(1, { nonNullable: true });
  readonly orderTypeControl = new FormControl<OrderType>(OrderType.SALE, { nonNullable: true });

  readonly filteredProducts = computed(() => {
    const catId = this.selectedCategoryId();
    const all = this.products();
    return catId ? all.filter((p) => (p.category as Category)?.id === catId) : all;
  });

  readonly cartTotal = computed(() =>
    this.cart().reduce((sum, item) => sum + item.product.price * item.quantity, 0)
  );

  ngOnInit(): void {
    this.categoryService.getAll().subscribe((cats) => {
      this.categories.set(cats);
      if (cats.length) this.selectedCategoryId.set(cats[0].id ?? null);
    });
    this.productService.getAll().subscribe((prods) => this.products.set(prods));
    this.clienteService.getAll().subscribe((data) => this.clients.set(data));

    this.cashBoxState.refreshOpenCashBox().subscribe({
      next: (box) => this.hasOpenCashBox.set(box?.estado === CashBoxStatus.OPEN),
      error: () => this.hasOpenCashBox.set(false)
    });
  }

  selectCategory(id: number | null): void {
    this.selectedCategoryId.set(id);
  }

  addToCart(product: Product): void {
    const current = this.cart();
    const existing = current.find((i) => i.product.id === product.id);
    if (existing) {
      this.cart.set(
        current.map((i) =>
          i.product.id === product.id ? { ...i, quantity: i.quantity + 1 } : i
        )
      );
    } else {
      this.cart.set([...current, { product, quantity: 1, kitchenNotes: '' }]);
    }
  }

  updateQuantity(productId: number, delta: number): void {
    this.cart.set(
      this.cart()
        .map((i) =>
          i.product.id === productId ? { ...i, quantity: Math.max(1, i.quantity + delta) } : i
        )
        .filter((i) => i.quantity > 0)
    );
  }

  removeFromCart(productId: number): void {
    this.cart.set(this.cart().filter((i) => i.product.id !== productId));
  }

  updateNotes(productId: number, notes: string): void {
    this.cart.set(
      this.cart().map((i) => (i.product.id === productId ? { ...i, kitchenNotes: notes } : i))
    );
  }

  confirmSale(): void {
    if (!this.hasOpenCashBox()) {
      this.snackBar.open('Debe abrir una caja antes de vender', 'Cerrar', { duration: 3000 });
      return;
    }

    const items = this.cart();
    if (!items.length) {
      this.snackBar.open('El carrito está vacío', 'Cerrar', { duration: 3000 });
      return;
    }

    this.orderService
      .create({
        clienteId: this.clientControl.value,
        orderType: this.orderTypeControl.value,
        details: items.map((item) => ({
          product: { id: item.product.id! },
          quantity: item.quantity,
          kitchenNotes: item.kitchenNotes || undefined
        }))
      })
      .subscribe({
        next: (order) => {
          this.snackBar.open(
            `Venta #${order.orderNumber} registrada — S/ ${order.totalAmount}`,
            'OK',
            { duration: 4000 }
          );
          this.cart.set([]);
        },
        error: (err) =>
          this.snackBar.open(err.error?.message ?? 'Error al registrar venta', 'Cerrar', {
            duration: 4000
          })
      });
  }
}
