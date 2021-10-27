import { Injectable } from '@angular/core';
import {MatSnackBar} from "@angular/material/snack-bar";
import Swal, {SweetAlertIcon} from "sweetalert2";
import {RestService} from "./rest.service";

@Injectable({
  providedIn: 'root'
})
export class DialogService {

  constructor(private snackBar: MatSnackBar, private restService: RestService) { }

  snack(colorName, text,) {
    this.snackBar.open(text, '', {
      duration: 2000, verticalPosition: 'bottom', horizontalPosition: 'center', panelClass: 'snackbar-' + colorName,
    });
  }

  dialog(title: string, message: string, icon: SweetAlertIcon, yesNo: boolean) {
    let result: boolean
    Swal.fire({
      titleText: title,
      html: message,
      icon: icon,
      showConfirmButton: true,
      confirmButtonText: yesNo ? 'Confirmar' : 'Aceptar',
      showDenyButton: yesNo,
      denyButtonText: 'Cancelar'
    }).then(r => result = r.value);
    return result
  }

  dialogDelete(title: string, id: number, html: string, controlador: string) {
    Swal.fire({
      titleText: title,
      html: '¿Esta seguro de eliminar el registro: ' + id + '?<br>' + html,
      icon: 'warning',
      showConfirmButton: true,
      confirmButtonText: 'Confirmar',
      showDenyButton: true,
      denyButtonText: 'Cancelar',
      allowOutsideClick: false
    }).then(r => {
      if (r.value) {
        return this.restService.delete<string>(id, {}, controlador)
      }
    });
  }

}
