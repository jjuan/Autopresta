import {Component, OnInit} from '@angular/core';
import {RestService} from "../../../../core/service/rest.service";
import {ActivatedRoute} from "@angular/router";
import {GeneracionContratoComponent} from "../generacion-contrato/generacion-contrato.component";
import {Contrato} from "../../../../core/models/data.interface";
import {MatDialog} from "@angular/material/dialog";
import {MatSnackBar} from "@angular/material/snack-bar";
import {ExtensionFormComponent} from "../extension-form/extension-form.component";

@Component({
  selector: 'app-extension-contratos',
  templateUrl: './extension-contratos.component.html',
  styleUrls: ['./extension-contratos.component.sass']
})
export class ExtensionContratosComponent implements OnInit {
  public datos = {
    title: 'Extension de Contrato',
    modulo: 'Contrataciones',
    icono: 'fas fa-desktop',
    dominio: 'Contrato',
    componente: 'Extension de Contrato'
  };
  id;
  datosContrato

  constructor(private genericRestService: RestService, private route: ActivatedRoute, private dialog: MatDialog, private snack: MatSnackBar) {
  }

  ngOnInit(): void {
    this.id = this.route.snapshot.params.id
    this.genericRestService.edit<any>(this.id, this.datos.dominio, {}, 'extensionContrato').subscribe(datos => {
      this.datosContrato = datos
    })
  }

  generaExtension() {
    let dia = this.dialog.open(ExtensionFormComponent, {
      data: {disableClose: true, id: this.id,}, height: 'auto', width: '40%'
    });
    dia.afterClosed().subscribe(() => {
      this.ngOnInit()
    })
  }

  aplicar(p) {
    this.id = this.route.snapshot.params.id
    this.genericRestService.edit<any>(this.id, this.datos.dominio, {parcialidad: p}, 'aplicaExtensionContrato').subscribe(() => {
      this.ngOnInit()
    })
  }
}
