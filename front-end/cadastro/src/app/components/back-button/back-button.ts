import { Location } from '@angular/common';
import { Component, Input, inject } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-back-button',
  standalone: true,
  templateUrl: './back-button.html',
})
export class BackButtonComponent {
  private readonly location = inject(Location);
  private readonly router = inject(Router);

  @Input() label = 'Voltar';
  @Input() fallbackUrl = '/';

  goBack(): void {
    if (window.history.length > 1) {
      this.location.back();
      return;
    }

    this.router.navigateByUrl(this.fallbackUrl);
  }
}
