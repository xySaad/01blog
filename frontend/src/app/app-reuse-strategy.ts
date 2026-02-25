import { ActivatedRouteSnapshot, BaseRouteReuseStrategy } from '@angular/router';

export class AppReuseStrategy extends BaseRouteReuseStrategy {
  override shouldReuseRoute(future: ActivatedRouteSnapshot, curr: ActivatedRouteSnapshot) {
    if (future.routeConfig?.component != null && curr.routeConfig?.component != null) {
      return future.routeConfig.component === curr.routeConfig.component;
    }
    return future.routeConfig === curr.routeConfig;
  }
}
