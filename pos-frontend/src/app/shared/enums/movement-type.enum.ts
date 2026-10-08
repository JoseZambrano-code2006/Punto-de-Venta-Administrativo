export enum MovementType {
  INCOME = 'INCOME',
  EXPENSE = 'EXPENSE'
}

export const MOVEMENT_TYPE_LABELS: Record<MovementType, string> = {
  [MovementType.INCOME]: 'Ingreso',
  [MovementType.EXPENSE]: 'Egreso'
};
