import { GeneralCashBox } from './general-cash-box.model';
import { MovementType } from '../enums/movement-type.enum';

export interface CashMovement {
  id?: number;
  cashBox?: GeneralCashBox | { id: number };
  type: MovementType;
  amount: number;
  description?: string;
  creationDate?: string;
}
