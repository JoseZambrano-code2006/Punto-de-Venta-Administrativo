import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-page-header',
  standalone: true,
  template: `
    <div class="page-header">
      <div>
        <h1>{{ title }}</h1>
        @if (subtitle) {
          <p>{{ subtitle }}</p>
        }
      </div>
      <div class="actions">
        <ng-content />
      </div>
    </div>
  `,
  styles: `
    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      gap: 1rem;
      margin-bottom: 1.5rem;
    }
    h1 {
      margin: 0;
      font-size: 1.75rem;
    }
    p {
      margin: 0.25rem 0 0;
      color: rgba(0, 0, 0, 0.6);
    }
    .actions {
      display: flex;
      gap: 0.5rem;
      flex-wrap: wrap;
    }
  `
})
export class PageHeaderComponent {
  @Input({ required: true }) title!: string;
  @Input() subtitle?: string;
}
