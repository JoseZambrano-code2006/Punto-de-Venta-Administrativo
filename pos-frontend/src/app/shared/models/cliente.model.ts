export interface Cliente {
  id?: number;
  nombreCompleto: string;
  tipoDocumento: string;
  numeroDocumento: string;
  direccion?: string;
  correo?: string;
}
