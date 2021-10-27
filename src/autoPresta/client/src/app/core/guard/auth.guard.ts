import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router, } from '@angular/router';
import {CookieService} from 'ngx-cookie-service';
import { Observable } from 'rxjs';
import {RouteInfo} from '../../layout/sidebar/sidebar.metadata';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  public sideMenu: RouteInfo[];
  constructor(private router: Router, private cookieService: CookieService) {}

  canActivate( route: ActivatedRouteSnapshot, state: RouterStateSnapshot ){
    if (this.checkUserLogin()) {
      return true;
    }
    this.router.navigate(['/Autenticacion/Login']);
    return false;
  }

  checkUserLogin(): boolean {
    const auth = this.cookieService.get('authToken');
    return auth !== undefined && auth !== '';
  }

  autheticate(token: string, username: string) {
    this.cookieService.set( 'authToken', token, undefined, '/');
    this.cookieService.set( 'username', username, undefined, '/');
  }

  authToken() {
    return this.cookieService.get('authToken');
  }

  username() {
    return this.cookieService.get('username');
  }

  role() {
    return this.cookieService.get('role');
  }
  puesto() {
    return this.cookieService.get('puesto');
  }

  avatar() {
    return this.cookieService.get('avatar');
  }

  nombre() {
    return this.cookieService.get('nombre');
  }

  destroySession() {
    this.cookieService.delete( 'authToken', '/');
  }

  saveCredentials(role: string, puesto: string, avatar: string, nombre: string, menu: RouteInfo[]){
    this.cookieService.set( 'role', role, undefined, '/');
    this.cookieService.set( 'puesto', puesto, undefined, '/');
    this.cookieService.set( 'avatar', avatar, undefined, '/');
    this.cookieService.set( 'nombre', nombre, undefined, '/');
    this.sideMenu = menu;
  }

  menu() {
    return this.sideMenu;
  }
}
