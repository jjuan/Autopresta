alter table Direccion
    add tipo varchar(255)
    go


INSERT INTO Autopresta.dbo.COMBO (combo, clave, orden, habilitado, tipo, descripcion) VALUES
(N'TipoDireccion', N'1', 1, 1, N'Direccion', N'Domicilio casa cliente'),
(N'TipoDireccion', N'2', 2, 1, N'Direccion', N'Domicilio donde se sitúa el auto'),
(N'TipoDireccion', N'3', 3, 1, N'Direccion', N'Domicilio laboral'),
(N'TipoDireccion', N'4', 4, 1, N'Direccion', N'2do Domicilio donde radica');

select * from Direccion where contrato in (SELECT contrato FROM Direccion
                                                           group by contrato having count(contrato)>1) order by contrato

Delete from Direccion where id in
(1983,1984,1985,1986,1987,1988,2010,2011,2012,2013,2014,2015,2016,2018,2017,2019,2020,2021,2022,2023,2024,2025,2029,2032,
 2030,2031,2037,2038,2039,2040,2041,2042,2043,2044,2045,2046,2047,2048,2049,2050,2051,2052,2053,2054,2055,2056,2057,2058,
 2059,2060,2061,2062,2063,2064,2065,2066,2067,2068,2069,2070,2071,2072,2073,2074,2075,2076,2077,2078,2079,2080,2082,2083,
 2081,2084,2085,2086,2087,2088,2089,2090,2091,2092,2093,2094,2095,2096,2098,2097,2099,2100,2101,2102,2103,2104,2105,2106)

update Direccion set tipo = 'Domicilio casa cliente' where id in
(1025,1026,1027,1028,1029,1030,1052,1053,1054,1055,1056,1057,1058,1060,1059,1061,1062,1063,1064,1065,1066,1067,1071,1074,
 1072,1073,1079,1080,1081,1082,1083,1084,1085,1086,1087,1088,1089,1090,1091,1092,1093,1094,1095,1096,1097,1098,1099,1100,
 1101,1102,1103,1104,1105,1106,1107,1108,1109,1110,1111,1112,1113,1114,1115,1116,1117,1118,1119,1120,1121,1122,1124,1125,
 1123,1126,1127,1128,1129,1130,1131,1132,1133,1134,1135,1136,1137,1138,1140,1139,1141,1142,1143,1144,1145,1146,1147,1148,
 42968,43460)

update Direccion set tipo = 'Domicilio donde se sitúa el auto' where contrato in
(1,3,4,5,7,10,14,15,16,17,18,19,21,22,23,24,25,26,27,28,29,30,31,32,33,34,36,37,38,40,42,43,44,45,46,47,48,49,50,51,52,
 53,54,55,56,57,58,59,61,64,65,67,68,69,70,71,72,73,74,75,76,77,78,80,81,82,83,89,90,91,93,94,95,96,97,98,99,101,102,103,
 105,106,107,108,109,110,112,113,114,115,116,117,118,119,120,121) and tipo is null

UPDATE Autopresta.dbo.Direccion SET tipo = N'Domicilio donde se sitúa el auto' WHERE id in (42969,43461)
UPDATE Autopresta.dbo.Direccion SET tipo = N'Domicilio laboral' WHERE id = 42970
