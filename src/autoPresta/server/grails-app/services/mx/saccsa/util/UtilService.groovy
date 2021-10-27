package mx.saccsa.util

import grails.gorm.transactions.Transactional
import mx.saccsa.autopresta.Divisas

import java.math.RoundingMode
import java.text.SimpleDateFormat

@Transactional
class UtilService {

    private decimalPart(BigDecimal bd){
        bd.setScale(2, RoundingMode.DOWN)
        BigDecimal fractionalPart = bd.remainder( BigDecimal.ONE ).setScale(2,RoundingMode.DOWN);
        fractionalPart.abs().unscaledValue()?.toString()?.padRight(2,"0")
    }

    public montoLetra(BigDecimal importe){
        BigDecimal totalBigDecimal
        if(importe < 0.0)
            totalBigDecimal = importe.abs()
        else
            totalBigDecimal = importe
        Closure triTexto = { int n ->
            StringBuilder result = new StringBuilder();
            int centenas = n / 100;
            int decenas  = (n % 100) / 10;
            int unidades = (n % 10);

            switch (centenas) {
                case 0: break;
                case 1:
                    if (decenas == 0 && unidades == 0) {
                        result.append("Cien ");
                        return result;
                    }
                    else result.append("Ciento ");
                    break;
                case 2: result.append("Doscientos "); break;
                case 3: result.append("Trescientos "); break;
                case 4: result.append("Cuatrocientos "); break;
                case 5: result.append("Quinientos "); break;
                case 6: result.append("Seiscientos "); break;
                case 7: result.append("Setecientos "); break;
                case 8: result.append("Ochocientos "); break;
                case 9: result.append("Novecientos "); break;
            }

            switch (decenas) {
                case 0: break;
                case 1:
                    if (unidades == 0) { result.append("Diez "); return result; }
                    else if (unidades == 1) { result.append("Once "); return result; }
                    else if (unidades == 2) { result.append("Doce "); return result; }
                    else if (unidades == 3) { result.append("Trece "); return result; }
                    else if (unidades == 4) { result.append("Catorce "); return result; }
                    else if (unidades == 5) { result.append("Quince "); return result; }
                    else result.append("Dieci");
                    break;
                case 2:
                    if (unidades == 0) { result.append("Veinte "); return result; }
                    else result.append("Veinti");
                    break;
                case 3: result.append("Treinta "); break;
                case 4: result.append("Cuarenta "); break;
                case 5: result.append("Cincuenta "); break;
                case 6: result.append("Sesenta "); break;
                case 7: result.append("Setenta "); break;
                case 8: result.append("Ochenta "); break;
                case 9: result.append("Noventa "); break;
            }

            if (decenas > 2 && unidades > 0)
                result.append("y ");

            switch (unidades) {
                case 0: break;
                case 1: result.append("Un "); break;
                case 2: result.append("Dos "); break;
                case 3: result.append("Tres "); break;
                case 4: result.append("Cuatro "); break;
                case 5: result.append("Cinco "); break;
                case 6: result.append("Seis "); break;
                case 7: result.append("Siete "); break;
                case 8: result.append("Ocho "); break;
                case 9: result.append("Nueve "); break;
            }

            return result;
        }


        StringBuilder restulBuilder = new StringBuilder();
        totalBigDecimal.setScale(2, BigDecimal.ROUND_DOWN);
        long parteEntera = totalBigDecimal.toBigInteger().longValue();
        int triUnidades      = (int)((parteEntera % 1000));
        int triMiles         = (int)((parteEntera / 1000d) % 1000);
        int triMillones      = (int)((parteEntera / 1000000d) % 1000);
        int triMilMillones   = (int)((parteEntera / 1000000000d) % 1000);

        if (parteEntera == 0) {
            restulBuilder.append("Cero ");
            return restulBuilder.toString();
        }

        if (triMilMillones > 0) restulBuilder.append(triTexto(triMilMillones).toString() + "Mil ");
        if (triMillones > 0)    restulBuilder.append(triTexto(triMillones).toString());

        if (triMilMillones == 0 && triMillones == 1) restulBuilder.append("Millón ");
        else if (triMilMillones > 0 || triMillones > 0) restulBuilder.append("Millones ");

        if (triMiles > 0)       restulBuilder.append(triTexto(triMiles).toString() + "Mil ");
        if (triUnidades > 0)    restulBuilder.append(triTexto(triUnidades).toString());

        return restulBuilder.toString()?.toUpperCase();
    }

    String cantidadLetra(BigDecimal importe, Divisas divisa){
        "(" + (importe <0.0? 'MENOS ':'') + montoLetra(importe) + ' PESOS '+ decimalPart(importe) + "/100"+ divisa?.clave+")"
    }
}
