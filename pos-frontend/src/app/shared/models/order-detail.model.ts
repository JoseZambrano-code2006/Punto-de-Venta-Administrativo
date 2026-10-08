import { Product } from './product.model';
import { Order } from './order.model';

export interface OrderDetail {
  id?: number;
  order?: Order | { id: number };
  product?: Product | { id: number };
  quantity: number;
  kitchenNotes?: string;
  unitPrice?: number;
  subtotal?: number;
  creationDate?: string;
}
