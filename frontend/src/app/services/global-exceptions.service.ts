import { ErrorHandler, inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

export class GlobalExceptions implements ErrorHandler {
  private snackBar = inject(MatSnackBar);

  handleError(error: any): void {
    if (error instanceof Error) {
      this.snackBar.open(error.message, 'ok', {
        panelClass: ['error-snackbar'],
        duration: 2000,
      });
    }
  }
}
