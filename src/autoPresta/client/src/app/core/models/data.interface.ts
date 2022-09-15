export interface Agencias {
  id: number;
  nombre: string;
  sucursal: Sucursales;
  region: Regiones;
  portafolio: Portafolios;
  clave: string;
}

export interface ConciliacionAutomatica {
  concilio: boolean;
  parcialidad: any;
  movimiento: any;
}

export interface configConcilicacionComponent {
  fechaInicio: Date;
  fechaFin: Date;
  cargoAbono: Boolean;
  titulo: string;
  subtitulo: string;
}

export interface conciliacionStatus {
  conciliadas?: number;
  pendientes: number;
  total: number;
}

export interface conciliacionMovimientosTable {
  folio: number
  cuenta: string;
  fecha: Date;
  titular?: string;
  referencia: string;
  monto: number;
  estatus: string;
  clase?: string;
}

export interface conciliacionContratosTable {
  folio: number;
  contrato: string;
  parcialidad: string;
  fecha: Date;
  titular?: string;
  monto: number;
  estatus: string;
  clase?: string;
}

export interface Automoviles {
  id: number;
  anio: string;
  marca: Marcas;
  modelo: Modelos;
}

export class _statusContratos {
  registrado: number;
  impreso: number;
  firmado: number;
  cancelado: number;
  total: number;
}

export class _subconceptos {
  id: number;
  subconcepto: string;
  descripcion: string;
  concepto: string;
  razonSocial: _razonSocial;
  divisa: Divisas;
  formaLiquidacion: _formaLiquidacion;
  categoria: _categorias;
}

export class _operacion {
  // campos generales
  operacion: string;
  folio: string;
  razonSocial: string;
  monto: string;
  fecha: string;
  estatus: string;
  concepto: string;

  // intercompanias
  tipoDeOperacion?: string;
  lineaDeCredito?: string;
  montoLinea?: string;
  saldoInsoluto?: string;
  montoDisponible?: string;

  // encomun traspasos y pago a proveedores
  cuentaCargo?: string;
  beneficiario?: string;
  cuentaAbono?: string;
  tipoTransaccion?: string;

  // proveedores
  bancoCliente?: string;
  monedaCliente?: string;
  alias?: string;
  bancoProveedor?: string;
  convenio?: string;
  referencia?: string;

  // traspasos
  bancoEmisor?: string;
  monedaEmisor?: string;
  aliasEmisor?: string;
  bancoReceptor?: string;
  aliasReceptor?: string;
}

export class _genLiq_ext_head {
  banco: _bancos;
  chequerasCasa: _cuentaBancaria;
  configuracion: string;
  habilitado: Boolean;
  nombre: string;
  orden: number;
  subconcepto: _subconceptos;
  tipoArchivo: string;
  cargoAbono: string;
}

export class _genLiq_ext_det {
  campoLiq: string;
  genLiqExtHead: _genLiq_ext_head;
  operador: string;
  orden: number;
  valor: string;
}

export class _categorias {
  id: number;
  descripcion: string;
}

export class _formaLiquidacion {
  id: number;
  bancoCasa: _bancos;
  chequeraCasa: _cuentaBancaria;
  descripcion: string;
  divisa: Divisas;
  estatusAplicacion: string;
  formaLiquidacion: string;
  montoMax: number;
  montoMin: number;
  multibanco: Boolean;
  numeroCheque: Boolean;
  pBanco: Boolean;
  pBuscaConcentradora: Boolean;
  pChequera: Boolean;
  pDireccion: Boolean;
  preguntaImpresion: string;
  tipoAplicacion: string;
  tipoImpresion: string;
  tipoMovimiento: string;
}

export class _puestos {
  id: number;
  puesto?: string;
  razonSocial?: _razonSocial;
}

export class _usuario {
  id: number;
  username: string;
  password: string;
  password5: string;
  nombre: string;
  apellidoPaterno: string;
  apellidoMaterno: string;
  mail: string;
  cargo: _puestos;
  avatar: string;
  razonSocial: _razonSocial;
  desde: Date;
  nuevo: boolean;
  enabled: boolean;
  accountExpired: boolean;
  accountLocked: boolean;
  passwordExpired: boolean;
  role: string;
}

export interface Clientes {
  id: number;
  nombres: string;
  primerApellido: string;
  segundoApellido: string;
  genero: string;
  rfc: string;
  fechaDeNacimiento: string;
  curp: string;
  claveDeElector: string;
  telefonoFijo: string;
  telefonoCelular: string;
  telefonoOficina: string;
  correoElectronico: string;
  dirTrabajo: boolean;
  dirAdicional: boolean;
  direccionPrincipal: string;
  exterior: number;
  interior: number;
  cp: number;
  colonia: string;
  alcaldiaMunicipio: string;
  entidad: string;
}

export interface Combo {
  id: string;
  descripcion: string;
}

export class _importaciones {
  id: number;
  cuenta: string;
  fecha: Date;
  montoAbono: number;
  montoCargo: number;
  tipoMovimiento: string;
  referencia: string;
}

export class _bancos {
  id: number;
  descripcionCorta: string;
  descripcionLarga: string;
  direccion_clc: string;
  direccion_con: string;
  direccion_dis: string;
  pais: string;
}

export class _instruccionesDePago {
  id: number;
  // proveedor: _proveedores;
  banco: _bancos;
  cuenta: string;
  clabe: string;
  moneda: Divisas;
  convenio: string;
  referencia: string;
  concepto: string;
}

export class _cuentaBancaria {
  id: number;
  razonSocial: _razonSocial;
  banco: _bancos;
  alias: string;
  cuenta: string;
  clabe: string;
  moneda: Divisas;
  fechaDeApertura: Date;
  estatus: string;
  fechaDeCancelacion: Date;
}

export interface Folio {
  cveTipo: string;
  folio: number;
}

export interface FoliosRecuerados {
  id: number;
  cveTipo: string;
  folio: number;
}

export interface objetoConciliacion {
  color?: string,
  label?: string
  icon?: string
}

export interface Divisas {
  id: number;
  clave: string;
  descripcion: string;
}

export interface Gps {
  id: number;
  nombre: string;
}

export interface Marcas {
  id: number;
  slug: string;
  nombre: string;
}

export interface CalificacionCliente {
  id: number;
  nombre: string;
  descripcion: string;
}

export interface Mercados {
  cveMercado: string;
  descripcion: string;
  fecha: Date;
}

export interface Modelos {
  id: number;
  marca: Marcas;
  slug: string;
  nombre: string;
}

export interface Portafolios {
  cvePortafolio: number;
  descripcion: string;
  fecha: Date;
  mercados: Mercados;
}

export interface Proveedores {
  id: number;
  nombre: string;
  rfc: string;
  moneda?: Divisas;
  monedaFactura?: Divisas;
  nombreDeContacto: string;
  correoElectronico: string;
  direccion: string;
  telefono: string;
  estatus: string;
  tipo: string;
}

export interface Regiones {
  id: number;
  clave: string;
  descripcion: string;
  variacion: number;
}

export interface Servicios {
  id: number;
  gps1: Gps;
  proveedor1: Proveedores;
  gps2: Gps;
  proveedor2: Proveedores;
  gps3: Gps;
  proveedor3: Proveedores;
}

export interface Sucursales {
  id: number;
  descripcion: string;
  region?: Regiones;
  ciudad: string;
  direccion: string;
  colonia: string;
  codigoPostal: string;
  telefono: string;
}

export interface _comboCp {
  id: any;
  descripcion: any;
  estado: any;
  municipio: any;
  ciudad: any;
  asentamiento: any;
  cp: string
}

export interface ContratoDetalle {
  contrato: Contrato;
  parcialidad: string;
  fecha: Date;
  interes: number;
  monitoreo: number;
  gps: number;
  capital: number;
  subtotal: number;
  iva: number;
  saldoFinal: number;
  fechaPago: Date;
  estatus: string;
}

export interface Contrato {
  id: number
  //datos del cliente
  regimenFiscal: string;
  numeroContrato: string;
  nombres: string;
  primerApellido: string;
  segundoApellido: string;
  genero: string;
  rfc: string;
  fechaDeNacimiento: string;
  curp: string;
  claveDeElector: string;
  telefonoFijo: string;
  telefonoCelular: string;
  telefonoOficina: string;
  correoElectronico: string;
  direccion: any
  edad: string;
  fechaNacimiento: Date;
  claveElector: string;

  nombresCoacreditado: string;
  primerApellidoCoacreditado: string;
  segundoApellidoCoacreditado: string;
  generoCoacreditado: string;
  rfcCoacreditado: string;
  fechaDeNacimientoCoacreditado: string;
  curpCoacreditado: string;
  claveDeElectorCoacreditado: string;
  telefonoFijoCoacreditado: string;
  telefonoCelularCoacreditado: string;
  telefonoOficinaCoacreditado: string;
  correoElectronicoCoacreditado: string;
  Coacreditadodireccion: any
  edadCoacreditado: string;
  fechaNacimientoCoacreditado: Date;
  claveElectorCoacreditado: string;
  //datos del automovil
  anio: string;
  marca: Marcas;
  modelo: Modelos;
  versionAuto: string;
  color: string;
  placas: string;
  numeroDeMotor: string;
  numeroDeFactura: string;
  fechaDeFactura: Date;
  emisoraDeFactura: string;
  valorDeVenta: number;
  valorDeCompra: number;
  montoMaximoAutorizado: number;
  numeroVin: string;

  //servicios
  gps1: Gps;
  proveedor1: Proveedores;
  gps2: Gps;
  proveedor2: Proveedores;
  gps3: Gps;
  proveedor3: Proveedores;

  //datosdel prestamo
  montoRequerido: number;
  costoMensualInteres: number;
  costoMensualMonitoreo: number;
  costoMensualGPS: number;
  totalAutoPresta: number;
  iva: number;
  costoMensualTotal: number;
  tipoContrato: string;
  referencia: string;
  clabe: string;
}

export class _razonSocial {
  id: number;
  estatus: boolean;
  nombre: string;
  rfc: string;

  calle?: string;
  noExterior?: string;
  noInterior?: string;
  colonia?: string;
  codigoPostal?: string;
  localidad?: string;
  municipio?: string;
  estado?: string;
  pais?: string;
  telefono?: string;
  email?: string;
}

export interface direccion {
  id?: number
  contrato?: Contrato;
  dirTrabajo: boolean;
  dirAdicional: boolean;
  direccionPrincipal: string;
  exterior: string;
  interior: string;
  cp: number;
  colonia: string;
  municipio: string;
  entidad: string;
  principal?: boolean;
}

export interface Folios {
  folio: string;
  folioMty: string;
  folioPrueba: string;
}

export interface IdentificacionesOficiales {
  id: number;
  nombre: string;
  longitud: number;
}

export interface monto {
  monto: number
}

export interface Contrataciones {
  id: number;
  folio: string;
  regimenFiscal: string;
  fechaEmision: Date;
  estatusLabel?: string;
  titular?: string;
  estatusContrato?: string;
  numeroContrato?: string;
  representante?: string;
  total: monto[];
  montoPrestamo: number;
  estatus: string;
  estatusCliente?: string;
}

export interface parcialidad {
  folio: any,
  contrato: string,
  parcialidad: string,
  fecha: Date,
  monto: number,
  estatus: string,
  clase: string

}

export interface movimiento {
  folio: any,
  cuenta: string,
  referencia: string,
  fecha: Date,
  monto: number,
  estatus: string,
  clase: string

}

export interface detalleConciliacion {
  concilio: Boolean,
  parcialidad: parcialidad,
  movimiento: movimiento,
  formaConciliacion: string
}
