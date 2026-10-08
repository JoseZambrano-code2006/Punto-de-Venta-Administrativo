import { Account } from './account.model';
import { CashBoxStatus } from '../enums/cash-box-status.enum';

export interface GeneralCashBox {
  id?: number;
  account?: Account | { id: number };
  opening?: string;
  closing?: string;
  saldoInicial: number;
  saldoFinal?: number;
  estado?: CashBoxStatus;
  creationDate?: string;
}
