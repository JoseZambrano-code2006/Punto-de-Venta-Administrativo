export enum CashBoxStatus {
  OPEN = 'OPEN',
  CLOSED = 'CLOSED'
}

export const CASH_BOX_STATUS_LABELS: Record<CashBoxStatus, string> = {
  [CashBoxStatus.OPEN]: 'Abierta',
  [CashBoxStatus.CLOSED]: 'Cerrada'
};
