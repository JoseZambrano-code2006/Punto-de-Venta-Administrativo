import { Component, inject, input, OnDestroy, OnInit, output, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ProductImageService } from '../../core/services/product-image.service';
import { ProductImageMeta } from '../../shared/models/product-image.model';
import { ProductImageThumbComponent } from '../../shared/components/product-image-thumb.component';

@Component({
  selector: 'app-image-picker',
  standalone: true,
  imports: [
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    ProductImageThumbComponent
  ],
  template: `
    <div class="image-picker">
      <div class="actions">
        <button mat-stroked-button type="button" (click)="fileInput.click()" [disabled]="uploading()">
          <mat-icon>upload</mat-icon>
          Subir imagen
        </button>
        <input #fileInput type="file" accept="image/*" hidden (change)="onFileSelected($event)" />
      </div>

      @if (previewUrl() || selectedId()) {
        <div class="preview">
          @if (previewUrl()) {
            <img [src]="previewUrl()!" alt="Vista previa" />
          } @else if (selectedId()) {
            <app-product-image-thumb [imageId]="selectedId()" size="lg" alt="Vista previa" />
          }
          @if (uploading()) {
            <mat-spinner class="preview-spinner" diameter="24" />
          }
        </div>
      }

      @if (loading()) {
        <mat-spinner diameter="28" />
      } @else if (images().length) {
        <div class="gallery">
          @for (img of images(); track img.id) {
            <button
              type="button"
              class="thumb"
              [class.selected]="selectedId() === img.id"
              (click)="selectImage(img.id)"
              [title]="img.name ?? 'Imagen ' + img.id"
            >
              <app-product-image-thumb [imageId]="img.id" size="md" [alt]="img.name ?? ''" />
            </button>
          }
        </div>
      } @else if (!previewUrl()) {
        <p class="hint">Sube una imagen o selecciona una del catálogo.</p>
      }
    </div>
  `,
  styles: `
    .image-picker {
      display: flex;
      flex-direction: column;
      gap: 12px;
      margin-bottom: 8px;
    }

    .actions {
      display: flex;
      gap: 8px;
    }

    .preview {
      position: relative;
      display: inline-block;
      width: fit-content;
    }

    .preview img {
      max-width: 160px;
      max-height: 120px;
      border-radius: 8px;
      object-fit: cover;
      border: 2px solid var(--mat-sys-primary);
      display: block;
    }

    .preview-spinner {
      position: absolute;
      top: 8px;
      right: 8px;
    }

    .gallery {
      display: flex;
      gap: 8px;
      overflow-x: auto;
      padding-bottom: 4px;
    }

    .thumb {
      border: 2px solid transparent;
      border-radius: 8px;
      padding: 2px;
      background: var(--mat-sys-surface-container);
      cursor: pointer;
      flex: 0 0 auto;
    }

    .thumb.selected {
      border-color: var(--mat-sys-primary);
    }

    .hint {
      margin: 0;
      font-size: 0.875rem;
      color: var(--mat-sys-on-surface-variant);
    }
  `
})
export class ImagePickerComponent implements OnInit, OnDestroy {
  private readonly productImageService = inject(ProductImageService);
  private readonly snackBar = inject(MatSnackBar);

  readonly initialImageId = input<number | null>(null);
  readonly imageSelected = output<number | null>();

  readonly images = signal<ProductImageMeta[]>([]);
  readonly selectedId = signal<number | null>(null);
  readonly previewUrl = signal<string | null>(null);
  readonly loading = signal(false);
  readonly uploading = signal(false);

  private localPreviewUrl: string | null = null;

  ngOnInit(): void {
    const initial = this.initialImageId();
    if (initial) {
      this.selectedId.set(initial);
      this.imageSelected.emit(initial);
    }
    this.loadGallery();
  }

  ngOnDestroy(): void {
    this.revokeLocalPreview();
  }

  selectImage(id: number): void {
    this.revokeLocalPreview();
    this.previewUrl.set(null);
    this.selectedId.set(id);
    this.imageSelected.emit(id);
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) {
      return;
    }

    this.revokeLocalPreview();
    const localUrl = URL.createObjectURL(file);
    this.localPreviewUrl = localUrl;
    this.previewUrl.set(localUrl);
    this.selectedId.set(null);

    this.uploading.set(true);
    this.productImageService.upload(file, file.name).subscribe({
      next: (saved) => {
        this.images.update((list) => [...list, saved]);
        this.revokeLocalPreview();
        this.previewUrl.set(null);
        this.selectImage(saved.id);
        this.uploading.set(false);
        input.value = '';
      },
      error: () => {
        this.uploading.set(false);
        input.value = '';
        this.snackBar.open('No se pudo subir la imagen', 'Cerrar', { duration: 3000 });
      }
    });
  }

  private loadGallery(): void {
    this.loading.set(true);
    this.productImageService.list().subscribe({
      next: (data) => {
        this.images.set(data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  private revokeLocalPreview(): void {
    if (this.localPreviewUrl) {
      URL.revokeObjectURL(this.localPreviewUrl);
      this.localPreviewUrl = null;
    }
  }
}
