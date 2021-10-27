export interface InConfiguration {
    layout: {
        variant: string
        theme_color: string,
        logo_bg_color: string,
        sidebar: {
            collapsed: boolean,
            backgroundColor: string,
        }
    };
}

export interface ruteo {
  descripcion: string;
  path: string;
}
