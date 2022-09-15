USE AUTOPRESTA
ALTER TABLE[dbo].[ContratoDetalle]
ALTER COLUMN [parcialidad] INT

DELETE FROM AUTOPRESTA.dbo.Agencias
DELETE FROM AUTOPRESTA.dbo.SUCURSAL WHERE  AUTOPRESTA.dbo.SUCURSAL.id > 3
DELETE FROM AUTOPRESTA.dbo.Regiones WHERE  AUTOPRESTA.dbo.Regiones.id > 3

UPDATE AUTOPRESTA.dbo.Regiones SET version = 4, clave = 'CDMX', variacion = 0.00, descripcion = 'CDMX' WHERE id = 1;
UPDATE AUTOPRESTA.dbo.Regiones SET version = 2, clave = 'MTY', variacion = 0.00, descripcion = 'Nuevo León' WHERE id = 2;
UPDATE AUTOPRESTA.dbo.Regiones SET version = 2, clave = 'GDL', variacion = 0.00, descripcion = 'Jalisco' WHERE id = 3;

UPDATE AUTOPRESTA.dbo.SUCURSAL SET version = 1, region_id = 1, telefono = '55 7619 8801', colonia = 'Col. Jardínes del Pedregal', descripcion = 'CORPORATIVO', codigoPostal = '01900', direccion = 'Calle Lava No. 232', ciudad = 'Alcaldía Álvaro Obregón' WHERE id = 1;
UPDATE AUTOPRESTA.dbo.SUCURSAL SET version = 2, region_id = 2, telefono = '81 3038 1027', colonia = 'Col. Valle Oriente', descripcion = 'MONTERREY', codigoPostal = '66260', direccion = 'Av. Lázaro Cardenas 2225', ciudad = 'San Pedro Garza García' WHERE id = 2;
UPDATE AUTOPRESTA.dbo.SUCURSAL SET version = 1, region_id = 3, telefono = '33 2531 5322', colonia = 'Col. Italia Providencia', descripcion = 'GUADALAJARA', codigoPostal = '44648', direccion = 'Av. Adolfo López Mateos Norte 95', ciudad = 'Guadalajara' WHERE id = 3;


UPDATE AUTOPRESTA.dbo.ContratoDetalle SET
                                          AUTOPRESTA.dbo.ContratoDetalle.ivaReal = AUTOPRESTA.dbo.ContratoDetalle.iva,
                                          AUTOPRESTA.dbo.ContratoDetalle.saldoFinalReal = AUTOPRESTA.dbo.ContratoDetalle.saldoFinal,
                                          AUTOPRESTA.dbo.ContratoDetalle.subtotalReal = AUTOPRESTA.dbo.ContratoDetalle.subtotal,
                                          AUTOPRESTA.dbo.ContratoDetalle.capitalReal = AUTOPRESTA.dbo.ContratoDetalle.capital,
                                          AUTOPRESTA.dbo.ContratoDetalle.monitoreoReal = AUTOPRESTA.dbo.ContratoDetalle.monitoreo,
                                          AUTOPRESTA.dbo.ContratoDetalle.gpsReal = AUTOPRESTA.dbo.ContratoDetalle.gps,
                                          AUTOPRESTA.dbo.ContratoDetalle.interesReal = AUTOPRESTA.dbo.ContratoDetalle.interesReal

ALTER TABLE Contrato ADD estatusCliente VARCHAR(200)
-- update HistoricoExtensiones set fechaInicio = (select fecha from ContratoDetalle where ContratoDetalle.contrato = HistoricoExtensiones.contrato and parcialidad = HistoricoExtensiones.parcialidadInicio + 1) where descripcion = '2G'
up
