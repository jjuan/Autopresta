import {RouteInfo} from './sidebar.metadata';

export const ROUTES: RouteInfo[] = [
  {
    path: '',
    title: 'Contrataciones',
    moduleName: 'Contrataciones',
    icon: 'fas fa-folder-open',
    class: 'menu-toggle',
    groupTitle: false,
    submenu: [
      {
        path: 'Contrataciones/Contrato-Persona-Fisica',
        title: 'Contrato de Persona Fisica',
        moduleName: 'Contrataciones',
        icon: '',
        class: 'ml-menu',
        groupTitle: false,
        submenu: []
      },
      {
        path: 'Contrataciones/Contrato-Persona-Fisica-Coacreditado',
        title: 'Contrato de Persona Física(Co-Acreditado)',
        moduleName: 'Contrataciones',
        icon: '',
        class: 'ml-menu',
        groupTitle: false,
        submenu: []
      },
      {
        path: 'Contrataciones/Contrato-Persona-Moral',
        title: 'Contrato de Persona Moral',
        moduleName: 'Contrataciones',
        icon: '',
        class: 'ml-menu',
        groupTitle: false,
        submenu: []
      },
    ]
  },
  {
    path: '',
    title: 'Catálogos',
    moduleName: 'Catalogos',
    icon: 'fas fa-folder-open',
    class: 'menu-toggle',
    groupTitle: false,
    submenu: [
      // {
      //   path: 'Catalogos/Agencias',
      //   title: 'Agencias',
      //   moduleName: 'Catalogos',
      //   icon: '',
      //   class: 'ml-menu',
      //   groupTitle: false,
      //   submenu: []
      // },
      {
        path: 'Catalogos/Marcas',
        title: 'Marcas',
        moduleName: 'Catalogos',
        icon: '',
        class: 'ml-menu',
        groupTitle: false,
        submenu: []
      },
      {
        path: 'Catalogos/Modelos',
        title: 'Modelos',
        moduleName: 'Catalogos',
        icon: '',
        class: 'ml-menu',
        groupTitle: false,
        submenu: []
      },
      {
        path: 'Catalogos/Automoviles',
        title: 'Automoviles',
        moduleName: 'Catalogos',
        icon: '',
        class: 'ml-menu',
        groupTitle: false,
        submenu: []
      },
      {
        path: 'Catalogos/GPS',
        title: 'GPS',
        moduleName: 'Catalogos',
        icon: '',
        class: 'ml-menu',
        groupTitle: false,
        submenu: []
      },
      {
        path: 'Catalogos/Calificacion-Clientes',
        title: 'Calificación Clientes',
        moduleName: 'Catalogos',
        icon: '',
        class: 'ml-menu',
        groupTitle: false,
        submenu: []
      },
      {
        path: 'Catalogos/Sucursales',
        title: 'Sucursales',
        moduleName: 'Catalogos',
        icon: '',
        class: 'ml-menu',
        groupTitle: false,
        submenu: []
      },
      {
        path: 'Catalogos/Regiones',
        title: 'Regiones',
        moduleName: 'Catalogos',
        icon: '',
        class: 'ml-menu',
        groupTitle: false,
        submenu: []
      },
      {
        path: 'Catalogos/Cp',
        title: 'Codigo Postal',
        moduleName: 'Catalogos',
        icon: '',
        class: 'ml-menu',
        groupTitle: false,
        submenu: []
      },
      {
        path: 'Catalogos/Portafolios',
        title: 'Portafolios',
        moduleName: 'Catalogos',
        icon: '',
        class: 'ml-menu',
        groupTitle: false,
        submenu: []
      },
      {
        path: 'Catalogos/Fecha-Inhabil',
        title: 'Fechas Inhabiles',
        moduleName: 'Catalogos',
        icon: '',
        class: 'ml-menu',
        groupTitle: false,
        submenu: []
      },
      {
        path: 'Catalogos/Cuentas-Bancarias',
        title: 'Cuentas-Bancarias',
        moduleName: 'Catalogos',
        icon: '',
        class: 'ml-menu',
        groupTitle: false,
        submenu: []
      },
      {
        path: 'Catalogos/Usuarios',
        title: 'Usuarios',
        moduleName: 'Catalogos',
        icon: '',
        class: 'ml-menu',
        groupTitle: false,
        submenu: []
      },
      {
        path: 'Catalogos/Bancos',
        title: 'Bancos',
        moduleName: 'Catalogos',
        icon: '',
        class: 'ml-menu',
        groupTitle: false,
        submenu: []
      },
      {
        path: 'Catalogos/Folios',
        title: 'Folios',
        moduleName: 'Catalogos',
        icon: '',
        class: 'ml-menu',
        groupTitle: false,
        submenu: []
      },
      {
        path: 'Catalogos/Folios-Recuperados',
        title: 'Folios Recuperados',
        moduleName: 'Catalogos',
        icon: '',
        class: 'ml-menu',
        groupTitle: false,
        submenu: []
      },
      {
        path: 'Catalogos/Mercados',
        title: 'Mercados',
        moduleName: 'Catalogos',
        icon: '',
        class: 'ml-menu',
        groupTitle: false,
        submenu: []
      },
      {
        path: 'Catalogos/Divisas',
        title: 'Divisas',
        moduleName: 'Catalogos',
        icon: '',
        class: 'ml-menu',
        groupTitle: false,
        submenu: []
      },
      {
        path: 'Catalogos/Identificaciones-Oficiales',
        title: 'Identificaciones Oficiales',
        moduleName: 'Catalogos',
        icon: '',
        class: 'ml-menu',
        groupTitle: false,
        submenu: []
      },
    ]
  },
  {
    path: '',
    title: 'Procesos',
    moduleName: 'Procesos',
    icon: 'fas fa-desktop',
    class: 'menu-toggle',
    groupTitle: false,
    submenu: [
      // {
      //   path: 'Procesos/Genera-Liquidacion',
      //   title: 'Genera-Liquidacion',
      //   moduleName: 'Procesos',
      //   icon: '',
      //   class: 'ml-menu',
      //   groupTitle: false,
      //   submenu: []
      // },
      {
        path: 'Procesos/Importaciones',
        title: 'Importaciones',
        moduleName: 'Procesos',
        icon: '',
        class: 'menu-toggle',
        groupTitle: true,
        submenu: []
      },
      // {
      //   path: 'Procesos/Conciliacion-Egresos',
      //   title: 'Conciliacion-Egresos',
      //   moduleName: 'Procesos',
      //   icon: '',
      //   class: 'ml-menu',
      //   groupTitle: false,
      //   submenu: []
      // },
      {
        path: '',
        title: 'Conciliacion-Ingresos',
        moduleName: 'Procesos',
        icon: '',
        class: 'ml-menu',
        groupTitle: true,
        submenu: [{
          path: 'Procesos/Conciliacion-Ingresos/Mensualidades',
          title: 'Contratos',
          moduleName: 'Procesos',
          icon: '',
          class: 'ml-menu',
          groupTitle: false,
          submenu: []
        }, {
          path: 'Procesos/Conciliacion-Ingresos/Movimientos',
          title: 'Movimientos',
          moduleName: 'Procesos',
          icon: '',
          class: 'ml-menu',
          groupTitle: false,
          submenu: []
        },]
      },
    ]
  },
  {
    path: '',
    title: 'Consultas',
    moduleName: 'Consultas',
    icon: 'fas fa-desktop',
    class: 'menu-toggle',
    groupTitle: false,
    submenu: [
      {
        path: 'Consultas/Contrataciones',
        title: 'Contrataciones',
        moduleName: 'Consultas',
        icon: '',
        class: 'ml-menu',
        groupTitle: false,
        submenu: []
      },
      {
        path: 'Consultas/Contratos-Firmados',
        title: 'Contratos Firmados',
        moduleName: 'Consultas',
        icon: '',
        class: 'ml-menu',
        groupTitle: false,
        submenu: []
      },
      // {
      //   path: 'Consultas/Firma',
      //   title: 'Firma',
      //   moduleName: 'Consultas',
      //   icon: '',
      //   class: 'ml-menu',
      //   groupTitle: false,
      //   submenu: []
      // },
    ]
  },
  {
    path: '',
    title: 'Reportes',
    moduleName: 'Reportes',
    icon: 'fas fa-book',
    class: 'menu-toggle',
    groupTitle: false,
    submenu: [
      {
        path: 'Reportes/Contratos-Firmados',
        title: 'Contratos Firmados',
        moduleName: 'Reportes',
        icon: '',
        class: 'ml-menu',
        groupTitle: false,
        submenu: []
      },
      {
        path: 'Reportes/Pagos-Realizados',
        title: 'Pagos Realizados',
        moduleName: 'Reportes',
        icon: '',
        class: 'ml-menu',
        groupTitle: false,
        submenu: []
      },{
        path: 'Reportes/Reporte-Conciliaciones',
        title: 'Reporte de Conciliaciones',
        moduleName: 'Reportes',
        icon: '',
        class: 'ml-menu',
        groupTitle: false,
        submenu: []
      },
      {
        path: 'Reportes/Reporte-Cobranza',
        title: 'Reporte de Cobranza',
        moduleName: 'Reportes',
        icon: '',
        class: 'ml-menu',
        groupTitle: false,
        submenu: []
      },
      {
        path: 'Reportes/Reporte-Morosidad',
        title: 'Reporte de Morosidad',
        moduleName: 'Reportes',
        icon: '',
        class: 'ml-menu',
        groupTitle: false,
        submenu: []
      },
    ]
  },
];
