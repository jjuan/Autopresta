import {Inject, Injectable} from '@angular/core';
import {HttpHeaders} from '@angular/common/http';
import {AuthGuard} from "../guard/auth.guard";

@Injectable({
  providedIn: 'root'
})
export class GlobalService {
  constructor(@Inject('API_URL') private baseUrl: string, private auth: AuthGuard) {}
   BASE_API_URL = this.baseUrl;
  getHttpOptions() {
    return { headers: new HttpHeaders({
      'Content-Type':  'application/json',
      Authorization: 'Bearer=' + this.auth.authToken()
    })};
  }

  getHttpOptionsReport() {
    return {
      responseType: 'blob' as 'json',
      headers: new HttpHeaders({
        Authorization: 'Bearer=' + this.auth.authToken()
      })};
  }

  getAuthToken() { return this.auth.authToken(); }
  getUserName() { return this.auth.username(); }
  getRole() { return this.auth.role(); }
  getPuesto() { return this.auth.puesto(); }
  getName() { return this.auth.nombre(); }
  getAvatar() { return this.auth.avatar(); }
  getMenu() { return this.auth.menu(); }
  destroyToken() { this.auth.destroySession(); }

}
