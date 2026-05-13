import { ErrorHandler, inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { ApiError } from '../../types/api';

export class GlobalExceptions implements ErrorHandler {
  private snackBar = inject(MatSnackBar);
  private router = inject(Router);

  handleError(error: any): void {
    if (error instanceof ApiError) {
      if (error.status === 404) {
        this.router.navigateByUrl('/not-found', {
          skipLocationChange: true,
        });
        return;
      }

      this.snackBar.open(error.message, 'ok', {
        panelClass: ['error-snackbar'],
        duration: 2000,
      });
    }
  }
}
