import { Category } from './category.model';

export interface Product {
  id?: number;
  category?: Category | { id: number };
  name: string;
  description?: string;
  price: number;
  imageUrl?: string;
  image?: { id: number };
  creationDate?: string;
}
