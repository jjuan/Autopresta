import {DOCUMENT} from '@angular/common';
import {Component, Inject, ElementRef, OnInit, Renderer2, HostListener, AfterViewInit} from '@angular/core';
import {Router} from '@angular/router';
import {ConfigService} from 'src/app/config/config.service';
import {RightSidebarService} from 'src/app/core/service/rightsidebar.service';
import {WINDOW} from 'src/app/core/service/window.service';
import {HttpClient} from "@angular/common/http";
import {GlobalService} from "../../core/service/global.service";
import {ruteo} from "../../core/models/config.interface";
import {Combo, Portafolios} from "../../core/models/data.interface";
import {FormGroup, Validators} from "@angular/forms";
import {RestService} from "../../core/service/rest.service";
import {DialogService} from "../../core/service/dialog.service";
import {DateAdapter} from "@angular/material/core";

const document: any = window.document;

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.sass']
})
export class HeaderComponent implements OnInit, AfterViewInit {
  rutas: ruteo[] = [
    {path: 'Inicio/Tablero', descripcion: 'Tablero'},
    {path: 'Catalogos/razonesSociales', descripcion: 'Razones Sociales'},
    {path: 'Catalogos/Usuarios', descripcion: 'Usuarios'},
    {path: 'Catalogos/Roles', descripcion: 'Roles'},
    {path: 'Catalogos/Bancos', descripcion: 'Bancos'},
    {path: 'Catalogos/Divisas', descripcion: 'Divisas'},
    {path: 'Catalogos/Cuentas-Bancarias', descripcion: 'Cuentas Bancarias'},
    {path: 'Catalogos/Tipo-de-Cambio', descripcion: 'Tipo de Cambio'},
    {path: 'Catalogos/Forma-de-Liquidacion', descripcion: 'Forma de Liquidacion'},
    {path: 'Catalogos/Categorias', descripcion: 'Categorias'},
    {path: 'Catalogos/Subconceptos', descripcion: 'Subconceptos'},
    {path: 'Egresos/Proveedores', descripcion: 'Proveedores'},
    {path: 'Egresos/Instrucciones-de-Pago', descripcion: 'Instrucciones de Pago'},
    {path: 'Egresos/Terminos-de-Pago', descripcion: 'Términos de Pago'},
    {path: 'Ingresos/Clientes', descripcion: 'Clientes'},
    {path: 'Ingresos/Terminos-de-Cobro', descripcion: 'Términos de Cobro'},
    {path: 'Procesos/Pago-a-Proveedores', descripcion: 'Pago a Proveedores'},
    {path: 'Procesos/Requisiciones', descripcion: 'Requisiciones'},
    {path: 'Procesos/Traspasos', descripcion: 'Traspasos'},
    {path: 'Procesos/Control-de-Cheques', descripcion: 'Control de Cheques'},
    {path: 'Procesos/Autorizacion-de-Pagos/Revisar-Pagos', descripcion: 'Revisar'},
    {path: 'Procesos/Autorizacion-de-Pagos/Liberar-Pagos', descripcion: 'Liberar'},
    {path: 'Procesos/Autorizacion-de-Pagos/Estatus-de-Pagos', descripcion: 'Estatus de Pagos'},
    {path: 'Procesos/Importaciones', descripcion: 'Importaciones'},
    {path: 'Procesos/Registro-de-Cobranza', descripcion: 'Registro de Cobranza'},
    {path: 'Procesos/Propuesta-de-Pagos', descripcion: 'Propuesta de Pagos'},
    {path: 'Procesos/Facturacion/Facturas', descripcion: 'Facturas'},
    {path: 'Procesos/Facturacion/Timbrar', descripcion: 'Timbrar'},
    {path: 'Procesos/Conciliacion/Conciliacion-de-Cobros', descripcion: 'Conciliacion de Cobros'},
    {path: 'Procesos/Conciliacion/Conciliacion-de-Pagos', descripcion: 'Conciliacion de Pagos'},
    {path: 'Procesos/Comprobantes-de-Pago', descripcion: 'Comprobantes de Pago'},
    {path: 'Procesos/Genera-Liquidacion', descripcion: 'Genera Liquidacion'},
    {path: 'Procesos/Recepcion', descripcion: 'Recepción'},
    {path: 'Reportes/Saldos', descripcion: 'Saldos'},
    {path: 'Reportes/Historico-de-Pagos', descripcion: 'Histórico de Pagos'},
    {path: 'Reportes/Cuentas-por-Cobrar/Antiguedad-de-Saldos', descripcion: 'Antigüedad de Saldos'},
    {path: 'Reportes/Cuentas-por-Cobrar/Facturas-por-Cobrar', descripcion: 'Facturas por Cobrar'},
    {path: 'Reportes/Cuentas-por-Pagar/Antiguedad-de-Saldos', descripcion: 'Antigüedad de Saldos'},
    {path: 'Reportes/Cuentas-por-Pagar/Facturas-por-Pagar', descripcion: 'Facturas por Pagar'},
    {path: 'Reportes/Tablero-de-Control/Flujo-de-Caja-Anual', descripcion: 'Flujo de Caja Anual'},
  ];
  public config: any = {};
  isNavbarCollapsed = true;
  isNavbarShow: boolean;
  logoName = 'Autopresta';
  langStoreValue: string;
  defaultFlag: string;
  public avatar: string;
  formulario: FormGroup

  constructor(
    @Inject(DOCUMENT) private document: Document,
    @Inject(WINDOW) private window: Window,
    private renderer: Renderer2,
    public elementRef: ElementRef,
    private dataService: RightSidebarService,
    private configService: ConfigService,
    private router: Router,
    private http: HttpClient,
    private globalService: GlobalService,
    private restService: RestService,
    private dialogService: DialogService,
    private dateAdapter: DateAdapter<Date>
  ) {
    this.dateAdapter.setLocale('en-GB'); //dd/MM/yyyy
  }

  listLang = [
    {text: 'English', flag: 'assets/images/flags/us.jpg', lang: 'en'},
    {text: 'Spanish', flag: 'assets/images/flags/spain.jpg', lang: 'es'},
    {text: 'German', flag: 'assets/images/flags/germany.jpg', lang: 'de'}
  ];
  notifications: any[];

  @HostListener('window:scroll', [])
  onWindowScroll() {
    const offset =
      this.window.pageYOffset ||
      this.document.documentElement.scrollTop ||
      this.document.body.scrollTop ||
      0;
    this.isNavbarShow = offset > 50;
  }

  ngOnInit() {
    this.config = this.configService.configData;
    this.avatar = this.globalService.getAvatar();
    this.formulario = this.restService.buildForm({
      cvePortafolio: [''],
      descripcion: [''],
      fecha: [''],
      mercados: [''],
    });
    this.cargarP()
  }

  ngAfterViewInit() {
    if (localStorage.getItem('theme')) {
      this.renderer.removeClass(this.document.body, this.config.layout.variant);
      this.renderer.addClass(this.document.body, localStorage.getItem('theme'));
    } else {
      this.renderer.addClass(this.document.body, this.config.layout.variant);
    }

    if (localStorage.getItem('menuOption')) {
      this.renderer.addClass(
        this.document.body,
        localStorage.getItem('menuOption')
      );
    } else {
      this.renderer.addClass(
        this.document.body,
        'menu_' + this.config.layout.sidebar.backgroundColor
      );
    }

    if (localStorage.getItem('choose_logoheader')) {
      this.renderer.addClass(
        this.document.body,
        localStorage.getItem('choose_logoheader')
      );
    } else {
      this.renderer.addClass(
        this.document.body,
        'logo-' + this.config.layout.logo_bg_color
      );
    }

    if (localStorage.getItem('sidebar_status')) {
      if (localStorage.getItem('sidebar_status') === 'close') {
        this.renderer.addClass(this.document.body, 'side-closed');
        this.renderer.addClass(this.document.body, 'submenu-closed');
      } else {
        this.renderer.removeClass(this.document.body, 'side-closed');
        this.renderer.removeClass(this.document.body, 'submenu-closed');
      }
    } else {
      if (this.config.layout.sidebar.collapsed === true) {
        this.renderer.addClass(this.document.body, 'side-closed');
        this.renderer.addClass(this.document.body, 'submenu-closed');
      }
    }
    this.cargarP()
  }

  ruta(value: ruteo) {
    this.router.navigate([value.path]).then();
  }

  callFullscreen() {
    if (
      !document.fullscreenElement &&
      !document.mozFullScreenElement &&
      !document.webkitFullscreenElement &&
      !document.msFullscreenElement
    ) {
      if (document.documentElement.requestFullscreen) {
        document.documentElement.requestFullscreen();
      } else if (document.documentElement.msRequestFullscreen) {
        document.documentElement.msRequestFullscreen();
      } else if (document.documentElement.mozRequestFullScreen) {
        document.documentElement.mozRequestFullScreen();
      } else if (document.documentElement.webkitRequestFullscreen) {
        document.documentElement.webkitRequestFullscreen();
      }
    } else {
      if (document.exitFullscreen) {
        document.exitFullscreen();
      } else if (document.msExitFullscreen) {
        document.msExitFullscreen();
      } else if (document.mozCancelFullScreen) {
        document.mozCancelFullScreen();
      } else if (document.webkitExitFullscreen) {
        document.webkitExitFullscreen();
      }
    }
  }

  mobileMenuSidebarOpen(event: any, className: string) {
    const hasClass = event.target.classList.contains(className);
    if (hasClass) {
      this.renderer.removeClass(this.document.body, className);
    } else {
      this.renderer.addClass(this.document.body, className);
    }
  }

  callSidemenuCollapse() {
    const hasClass = this.document.body.classList.contains('side-closed');
    if (hasClass) {
      this.renderer.removeClass(this.document.body, 'side-closed');
      this.renderer.removeClass(this.document.body, 'submenu-closed');
    } else {
      this.renderer.addClass(this.document.body, 'side-closed');
      this.renderer.addClass(this.document.body, 'submenu-closed');
    }
  }

  public toggleRightSidebar(): void {
    this.dataService.changeMsg(
      (this.dataService.currentStatus._isScalar = !this.dataService
        .currentStatus._isScalar)
    );
  }

  logout() {
    const headers = this.globalService.getHttpOptions();
    this.globalService.destroyToken();
    this.http.post(this.globalService.BASE_API_URL + 'api/logout', {}, headers).subscribe(() =>
      this.router.navigate(['/Autenticacion/Login']));
  }

  actualizarPortafolio() {
    this.restService.update<string>(this.formulario.get('cvePortafolio').value, this.formulario.value, 'Portafolios').subscribe(() => {
      this.dialogService.snack( 'success', 'Portafolio actualizado');
      this.cargarP()
    }, error => {
      if (error._embedded !== undefined) {
        this.dialogService.snack( 'danger', 'Error al actualizar');
      }});
  }

  myFilter = (d: Date): boolean => {
    const day = d.getDay();
    return day !== 0 && day !== 6;
  }

  cargarP(){
    this.restService.edit<Portafolios>(1, 'Portafolios').subscribe(result => {
      this.formulario.patchValue({
        cvePortafolio: result.cvePortafolio,
        descripcion: result.descripcion,
        fecha: result.fecha+'T00:00:00',
        mercados: result.mercados,
      });
    });
  }
}
