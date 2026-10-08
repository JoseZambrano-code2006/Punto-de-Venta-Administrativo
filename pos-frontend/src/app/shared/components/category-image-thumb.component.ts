import {
  Component,
  effect,
  inject,
  input,
  signal
} from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { CategoryImageService } from '../../core/services/category-image.service';

@Component({
  selector: 'app-category-image-thumb',
  standalone: true,
  imports: [MatIconModule],
  template: `
    <div class="thumb-wrap" [class]="'size-' + size()">
      @if (src()) {
        <img [src]="src()!" [alt]="alt()" />
      } @else {
        <mat-icon>image</mat-icon>
      }
    </div>
  `,
  styles: `
    .thumb-wrap {
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 8px;
      overflow: hidden;
      background: var(--mat-sys-surface-container);
      color: var(--mat-sys-on-surface-variant);
      flex-shrink: 0;
    }

    .thumb-wrap img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      display: block;
    }

    .size-sm {
      width: 40px;
      height: 40px;
    }

    .size-sm mat-icon {
      font-size: 20px;
      width: 20px;
      height: 20px;
    }

    .size-md {
      width: 72px;
      height: 72px;
    }

    .size-md mat-icon {
      font-size: 28px;
      width: 28px;
      height: 28px;
    }

    .size-lg {
      width: 120px;
      height: 90px;
    }

    .size-lg mat-icon {
      font-size: 36px;
      width: 36px;
      height: 36px;
    }
  `
})
export class CategoryImageThumbComponent {
  private readonly categoryImageService = inject(CategoryImageService);

  readonly imageId = input<number | null>(null);
  readonly size = input<'sm' | 'md' | 'lg'>('sm');
  readonly alt = input('');

  readonly src = signal<string | null>(null);

  constructor() {
    effect(() => {
      const id = this.imageId();
      this.loadImage(id);
    });
  }

  private loadImage(id: number | null): void {
    this.src.set(null);

    if (id == null) {
      return;
    }

    this.categoryImageService.getBlobUrl(id).subscribe({
      next: (url) => this.src.set(url),
      error: () => this.src.set(null)
    });
  }
}
