import {Component, Inject, OnInit} from '@angular/core';
import {AbstractControl, FormGroup, Validators} from "@angular/forms";
import {Combo} from "../../../../../core/models/data.interface";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {RestService} from "../../../../../core/service/rest.service";

@Component({
  selector: 'app-asignar-montos',
  templateUrl: './asignar-montos.component.html',
  styleUrls: ['./asignar-montos.component.sass']
})
export class AsignarMontosComponent implements OnInit {
  action: string;
  formulario: FormGroup;
  dialogTitle: string;
  estatusContratoCombo: Combo[] = [
    {id: 'Abono Mensual', descripcion: 'Abono Mensual'},
    {id: 'Interés Normal', descripcion: 'Interés Normal'},
    {id: 'Moratorios', descripcion: 'Moratorios'},
    {id: 'Penalizacion', descripcion: 'Penalizacion'},
    {id: 'Venta de autos', descripcion: 'Venta de autos'},
    {id: 'Aportación a capital', descripcion: 'Capital/20% extensión capital/Aportación a capital'},
    // {id: '20% extensión capital', descripcion: '20% extensión capital'}
  ];
  constructor(
    public dialogRef: MatDialogRef<AsignarMontosComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public restService: RestService,
  ) {}

  submit() {}

  onNoClick(): void {
    this.dialogRef.close();
  }
  public confirmAdd(): void {
    this.dialogRef.close(this.formulario.value);
  }

  ngOnInit(): void {
    this.action = this.data.action;
    this.dialogTitle = this.data.action + ' ' + this.data.title.toLowerCase();
    // console.log(this.data.data)
    this.formulario = this.restService.buildForm({
      id: [this.data.data.id ? this.data.data.id : ''],
      tipoConcepto: [this.data.data.tipoConcepto ? this.data.data.tipoConcepto : ''],
      monto: [this.data.data.monto ? this.data.data.monto : '', [Validators.required, (control: AbstractControl) => Validators.max((this.data.data.montoConciliado) -
        (this.data.data.subtotal + this.data.data.iva + this.data.data.moratorios + this.data.data.penalizacion + this.data.data.ventaDeAutos + this.data.data.aportacionAcapital))(control)]],
    });
  }
}
