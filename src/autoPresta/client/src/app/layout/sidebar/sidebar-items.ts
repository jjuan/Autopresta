import { RouteInfo } from './sidebar.metadata';

export const ROUTES: RouteInfo[] = [
  {
    path: '',
    title: 'Catálogos',
    moduleName: 'Catalogos',
    icon: 'fas fa-folder-open',
    class: 'menu-toggle',
    groupTitle: false,
    submenu: [
      {
        path: 'Catalogos/Agencias',
        title: 'Agencias',
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
        path: 'Catalogos/Marcas',
        title: 'Marcas',
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
        path: 'Catalogos/Portafolios',
        title: 'Portafolios',
        moduleName: 'Catalogos',
        icon: '',
        class: 'ml-menu',
        groupTitle: false,
        submenu: []
      },
      {
        path: 'Catalogos/Proveedores',
        title: 'Proveedores',
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
      {
        path: 'Procesos/Contrato',
        title: 'Contrato',
        moduleName: 'Procesos',
        icon: '',
        class: 'ml-menu',
        groupTitle: false,
        submenu: []
      },
    ]
  },
  {
    path: '',
    title: 'Reportes',
    moduleName: 'Reportes',
    icon: 'fas fa-book',
    class: 'menu-toggle',
    groupTitle: false,
    submenu: []
  },
];
