export class Agencias {
  id: number;
  nombre: string;
  sucursal: Sucursales;
  region: Regiones;
  portafolio: Portafolios;
  clave: string;
}

export class Automoviles {
  id: number;
  anio: string;
  marca: Marcas;
  modelo: Modelos;
  versionAuto: string;
}

export class Clientes {
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

export class Combo {
  id: string;
  descripcion: string;
}

export class Divisas {
  id: number;
  clave: string;
  descripcion: string;
}

export class Gps {
  id: number;
  nombre: string;
}

export class Marcas {
  id: number;
  slug: string;
  nombre: string;
}

export class Mercados {
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

export class Portafolios {
  cvePortafolio: number;
  descripcion: string;
  fecha: Date;
  mercados: Mercados;
}

export class Proveedores {
  id: number;
  nombre: string;
  rfc: string;
  moneda: Divisas;
  monedaFactura: Divisas;
  nombreDeContacto: string;
  correoElectronico: string;
  direccion: string;
  telefono: string;
  estatus: string;
  tipo: string;
}

export class Regiones {
  id: number;
  clave: string;
  descripcion: string;
  variacion: number;
}

export class Servicios {
  id: number;
  gps1: Gps;
  proveedor1: Proveedores;
  gps2: Gps;
  proveedor2: Proveedores;
  gps3: Gps;
  proveedor3: Proveedores;
}

export class Sucursales {
  id: number;
  descripcion: string;
  region: Regiones;
  ciudad: string;
  direccion: string;
  colonia: string;
  codigoPostal: string;
  telefono: string;
}

export class _comboCp {
  id: any;
  descripcion: any;
  estado: any;
  municipio: any;
  ciudad: any;
  asentamiento: any;
  cp: string
}

export class Contrato {
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

export class direccion {
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
