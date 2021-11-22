export interface Agencias {
  id: number;
  nombre: string;
  sucursal: Sucursales;
  region: Regiones;
  portafolio: Portafolios;
  clave: string;
}

export interface Automoviles {
  id: number;
  anio: string;
  marca: Marcas;
  modelo: Modelos;
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

export interface Contrato {
  id: number
  //datos del cliente
  regimenFiscal: string;

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

export interface direccion {
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

export interface Folios{
  folio: string;
  folioPrueba: string;
}
