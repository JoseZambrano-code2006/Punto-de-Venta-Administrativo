export enum OrderType {
  SALE = 'SALE',
  CREDIT = 'CREDIT',
  COURTESY = 'COURTESY',
  DAMAGED = 'DAMAGED'
}

export const ORDER_TYPE_LABELS: Record<OrderType, string> = {
  [OrderType.SALE]: 'Venta',
  [OrderType.CREDIT]: 'Crédito',
  [OrderType.COURTESY]: 'Cortesía',
  [OrderType.DAMAGED]: 'Dañado'
};
