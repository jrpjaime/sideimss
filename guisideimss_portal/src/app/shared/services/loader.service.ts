import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class LoaderService {
  private loaderVisible = new BehaviorSubject<boolean>(false);
  public loader$ = this.loaderVisible.asObservable();

  private timeoutId: any;

  showLoader(duration?: number) {
    this.loaderVisible.next(true);

    // Si se pasó un tiempo, cerrar automáticamente después de ese tiempo
    if (duration) {
      clearTimeout(this.timeoutId);
      this.timeoutId = setTimeout(() => this.closeLoader(), duration);
    }
  }

  closeLoader() {
    this.loaderVisible.next(false);
  }
}
