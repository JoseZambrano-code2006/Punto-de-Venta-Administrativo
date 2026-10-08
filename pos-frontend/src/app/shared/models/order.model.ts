import { GeneralCashBox } from './general-cash-box.model';
import { OrderDetail } from './order-detail.model';
import { OrderType } from '../enums/order-type.enum';

export interface Order {
  id?: number;
  cashBox?: GeneralCashBox;
  clienteId?: number;
  customerName?: string;
  customerDocument?: string;
  totalAmount?: number;
  creationDate?: string;
  orderNumber?: number;
  orderType: OrderType;
  details?: OrderDetail[];
}
