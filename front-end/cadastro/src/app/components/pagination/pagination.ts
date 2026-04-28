import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-pagination',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pagination.html',
})
export class PaginationComponent {
  @Input() page = 0;
  @Input() pageSize = 20;
  @Input() totalElements = 0;
  @Input() disabled = false;

  @Output() pageChange = new EventEmitter<number>();

  get totalPages(): number {
    if (this.totalElements === 0 || this.pageSize <= 0) {
      return 0;
    }

    return Math.ceil(this.totalElements / this.pageSize);
  }

  get currentPage(): number {
    return this.page + 1;
  }

  get hasPrevious(): boolean {
    return this.page > 0;
  }

  get hasNext(): boolean {
    return this.page + 1 < this.totalPages;
  }

  previousPage(): void {
    if (!this.hasPrevious || this.disabled) {
      return;
    }

    this.pageChange.emit(this.page - 1);
  }

  nextPage(): void {
    if (!this.hasNext || this.disabled) {
      return;
    }

    this.pageChange.emit(this.page + 1);
  }
}
