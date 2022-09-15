package mx.saccsa.autopresta

class HistoricoExtensiones {
    String descripcion
    Contrato contrato
    Long parcialidadInicio
    Long parcialidadFin
    BigDecimal montoRequerido
    BigDecimal totalApagar
    Date fechaInicio
    Boolean esDefault
    Boolean aplicada = false


    static constraints = {
        descripcion nullable: true
        contrato nullable: true
        parcialidadInicio nullable: true
        parcialidadFin nullable: true
        montoRequerido nullable: true
        totalApagar nullable: true
        fechaInicio nullable: true
        esDefault nullable: true
        aplicada nullable: true
    }

    static mapping = {
        table('HistoricoExtensiones')
        version(false)
        descripcion name: 'descripcion', column: 'descripcion'
        contrato name: 'contrato', column: 'contrato'
        parcialidadInicio name: 'parcialidadInicio', column: 'parcialidadInicio'
        parcialidadFin name: 'parcialidadFin', column: 'parcialidadFin'
        montoRequerido name: 'montoRequerido', column: 'montoRequerido'
        totalApagar name: 'totalApagar', column: 'totalApagar'
        fechaInicio name: 'fechaInicio', column: 'fechaInicio'
        esDefault name: 'esDefault', column: 'esDefault'
        aplicada name: 'aplicada', column: 'aplicada'
    }
}
