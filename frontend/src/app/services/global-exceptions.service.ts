import { ErrorHandler, inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FetchError } from '../../types/api';

export class GlobalExceptions implements ErrorHandler {
  private snackBar = inject(MatSnackBar);

  handleError(error: any): void {
    if (error instanceof FetchError) {
      this.snackBar.open(error.message, 'ok', {
        panelClass: ['error-snackbar'],
        duration: 2000,
      });
    }
  }
}
