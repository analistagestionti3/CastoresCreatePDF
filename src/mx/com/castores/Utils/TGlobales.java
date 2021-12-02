package mx.com.castores.Utils;

import com.castores.datautilsapi.log.LoggerUtils;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.logging.Level;

public class TGlobales {

    public static final int OK = 0;
    public static final int BD_NOCONEXION = -1;
    public static final int BD_NOINSERCION = -2;
    public static final int BD_NOMODIFICACION = -3;
    public static final int BD_CAMPONOENCONTRADO = -4;
    public static final int BD_NOENCONTRADO = -5;
    public static final int BD_CAMPOENTRADO = -1000;
    public static final String INSE_FECHA = "yyyy-mm-dd";
    public static final String INSE_HORA = "HH:mm:ss";
    public static final String INSE_HORA_MS = "HH:mm:ss:zz";
    public static final String FUN_FECHA = "current_date()";
    public static final String FUN_HORA = "current_time()";
    /**
     * Formas de visualizacin de datos *
     */
    public static final String VER_FECHA = "mmm/dd/yyyy";
    public static final String VER_HORA = INSE_HORA;
    public static final long difHora = 21600000;
    public static final Locale LOCALE_MX = new Locale("es", "MX");

    /**
     * Creates a new instance of Utilerias
     */
    public TGlobales() {
    }

    public static String moneda(double cantidad, int decimales) {
        String cad = "";
        for (int i = 0; i < decimales; i++) {
            cad += "0";
        }
        DecimalFormat FMT = new DecimalFormat("#,##0." + cad);

        return "$ " + FMT.format(cantidad);
    }

    public static String moneda(double cantidad) {
        return moneda(cantidad, 2);
    }

    public static String numeros(double cantidad, int decimales) {
        String cad = "";
        for (int i = 0; i < decimales; i++) {
            cad += "0";
        }
        DecimalFormat FMT = new DecimalFormat("#,##0." + cad);

        return FMT.format(cantidad);
    }

    public static String numeros(double cantidad) {
        return numeros(cantidad, 2);
    }

    public static String insertaDecimal(double cantidad) {
        return insertaDecimal(cantidad, 2);
    }

    public static String insertaDecimal(double cantidad, int decimales) {
        /*
         * String cad = ""; for (int i = 0; i < decimales; i++) { cad += "0"; }
         *
         * DecimalFormat FMT = new DecimalFormat("0." + cad);
         *
         * return FMT.format(cantidad);
         */

        //NumberFormat.getCurrencyInstance(Locale.getDefault());
        /*
         * Locale locale = new Locale("es","MX"); // elegimos Argentina
         *
         * DecimalFormat nf = (DecimalFormat)
         * DecimalFormat.getCurrencyInstance(locale); String strcantidad =
         * nf.format(cantidad);
         *
         * return strcantidad.replace("$", ""); /*DecimalFormat FMT = new
         * DecimalFormat("0." + cad);
         *
         * return FMT.format(cantidad);
         */
        return String.valueOf(cantidad);
    }

    public static String insertaFecha(Date fecha) {
        SimpleDateFormat FMT = new SimpleDateFormat(INSE_FECHA);

        return FMT.format(fecha);
    }

    public static String verFecha(Date fecha) {
        SimpleDateFormat FMT = new SimpleDateFormat(VER_FECHA);

        return FMT.format(fecha);
    }

    public static String inseHora(Time hora) {
        SimpleDateFormat FMT = new SimpleDateFormat(INSE_HORA);

        return FMT.format(hora);
    }

    public static String verHora(Date hora) {
        SimpleDateFormat FMT = new SimpleDateFormat(INSE_HORA);

        return FMT.format(hora);
    }

    public static String formatea(int valor, int num, char c) {
        String cad = "";
        String numero = Integer.toString(valor);
        for (int i = numero.length(); i < num; i++) {
            cad += "0";
        }

        return cad + numero;
    }

    public static String formatea(int valor, int num) {
        return formatea(valor, num, '0');
    }

    public static String quitaCar(String c, char car) {
        String r = "";
        for (int i = 0; i < c.length(); i++) {
            if (c.charAt(i) != car) {
                r += c.charAt(i);
            }
        }
        return r;
    }

    /**
     * Obtiene un Date a partir de la expresion porporsionada y un formato
     * establecido, el formato puede ser dd/MM/yyyy, dd-MM-yyyy, dd-MMM-yyyy,
     * yyyy/MM/dd, etc
     *
     * @param fecha es la expresion con el formato establecido.
     * @param formato es el formato de fecha establecido.
     * @return Date que representa la fecha solicitada o null si ocurre un
     * error.
     */
    public static Date toDate(String fecha, String formato) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(formato, new Locale("es", "MX"));
            return sdf.parse(fecha);
        } catch (Exception e) {
            return null;
        }
    }

    public static Date longToDate(long valor) {
        Date resultado = null;
        Calendar objCalendar = GregorianCalendar.getInstance();
        objCalendar.setTimeInMillis(valor);
        resultado = objCalendar.getTime();
        return resultado;
    }

    public static int Comparafechas(long valor, long valor2) {

        if (valor < valor2) {
            return 1;
        } else if (valor == valor2) {
            return 2;
        } else {
            return -1;
        }

    }

    public static long convierteFecha(Date valor) {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String cadenaFecha = formato.format(valor);
        try {
            Date f = formato.parse(cadenaFecha);
            long longDate = f.getTime();
            Calendar objCalendar = GregorianCalendar.getInstance();
            objCalendar.setTimeInMillis(longDate);
            Date f1 = objCalendar.getTime();
            long fechatmp = f1.getTime();
            return fechatmp;
        } catch (Exception e) {
            LoggerUtils.printLog(TGlobales.class, Level.SEVERE, e, null, Thread.currentThread().getStackTrace());
            return (0);
        }

    }

    public static long convierteHora(Date valor) {
        SimpleDateFormat formato = new SimpleDateFormat("HH:mm:ss");
        String cadenaFecha = formato.format(valor);
        try {
            Date f = formato.parse(cadenaFecha);
            long longDate = f.getTime();
            Calendar objCalendar = GregorianCalendar.getInstance();
            objCalendar.setTimeInMillis(longDate);
            Date f1 = objCalendar.getTime();
            long fechatmp = f1.getTime();
            return fechatmp;
        } catch (Exception e) {
            LoggerUtils.printLog(TGlobales.class, Level.SEVERE, e, null, Thread.currentThread().getStackTrace());
            return (0);
        }

    }

    public static String FormateaFecha(Date valor) {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        String cadenaFecha = formato.format(valor);

        return cadenaFecha;

    }

    /**
     * Obtiene un String que representa al objeto Date con el formato indicado.
     *
     * @param fecha es el objeto Date que se desea obtener su representacion.
     * @param formato es el formato en el que se desea representar al objeto
     * Date.
     * @return la representacion del objeto Date.
     * @see #verFecha(java.util.Date)
     * @see #verHora(java.util.Date)
     */
    public static String verFecha(Date fecha, String formato) {
        SimpleDateFormat FMT = new SimpleDateFormat(formato);
        return FMT.format(fecha);
    }

    public static int getDia(Date dfecha) {
        String fecha = verFecha(dfecha, "dd/MM/yyyy");
        return Integer.parseInt(fecha.substring(0, 2));
    }

    public static int getMes(Date dfecha) {

        String fecha = verFecha(dfecha, "dd/MM/yyyy");
        return Integer.parseInt(fecha.substring(3, 5));
    }

    public static int getAnio(Date dfecha) {
        String fecha = verFecha(dfecha, "dd/MM/yyyy");
        return Integer.parseInt(fecha.substring(6));
    }

    /**
     * Date d la fecha del dia que se quiere saber cual es return el numero del
     * dia de la semana (Domingo = 1, Lunes = 2 ...)
     */
    public static int getDiaSemana(Date d) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(d);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public static Date sumarRestarDiasFecha(Date fecha, int dias) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.add(Calendar.DAY_OF_YEAR, dias);

        return calendar.getTime();
    }

    public static Date getFechaHoy() {
        Calendar fecha = new GregorianCalendar();

        int anio = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH);
        int dia = fecha.get(Calendar.DAY_OF_MONTH);

        return TGlobales.toDate(dia + "/" + (mes + 1) + "/" + anio, "dd/MM/yyyy");
    }

    public static int restarFechas(Date fechaIn, Date fechaFinal) {
        GregorianCalendar fechaInicio = new GregorianCalendar();
        fechaInicio.setTime(fechaIn);
        GregorianCalendar fechaFin = new GregorianCalendar();
        fechaFin.setTime(fechaFinal);
        int dias = 0;
        if (fechaFin.get(Calendar.YEAR) == fechaInicio.get(Calendar.YEAR)) {
            dias = (fechaFin.get(Calendar.DAY_OF_YEAR) - fechaInicio.get(Calendar.DAY_OF_YEAR)) + 1;
        } else {
            int rangoAnyos = fechaFin.get(Calendar.YEAR) - fechaInicio.get(Calendar.YEAR);
            for (int i = 0; i <= rangoAnyos; i++) {
                int diasAnio = fechaInicio.isLeapYear(fechaInicio.get(Calendar.YEAR)) ? 366 : 365;
                if (i == 0) {
                    dias = 1 + dias + (diasAnio - fechaInicio.get(Calendar.DAY_OF_YEAR));
                } else if (i == rangoAnyos) {
                    dias = dias + fechaFin.get(Calendar.DAY_OF_YEAR);
                } else {
                    dias = dias + diasAnio;
                }
            }
        }
        return dias;
    }

    public static int getNoDiasMes(int p_intMes, int p_intAnio) {

        switch (p_intMes) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if (p_intAnio % 4 == 0 && p_intAnio % 100 != 0) {
                    return 29;
                } else {
                    if (p_intAnio % 100 == 0 && p_intAnio % 400 == 0) {
                        return 29;
                    } else {
                        return 28;
                    }
                }
        }

        return 0;
    }

    public static String dateFormatter(String inputFormat, String outputFormat, String inputDate) {
        //Define formato default de entrada.   
        String input = inputFormat.isEmpty() ? "yyyy-MM-dd hh:mm:ss" : inputFormat;
        //Define formato default de salida.
        String output = outputFormat.isEmpty() ? "d 'de' MMMM 'del' yyyy" : outputFormat;
        String outputDate = inputDate;
        try {
            outputDate = new SimpleDateFormat(output, LOCALE_MX).format(new SimpleDateFormat(input, LOCALE_MX).parse(inputDate));
        } catch (Exception e) {
            System.out.println("dateFormatter(): " + e.getMessage());
        }
        return outputDate;
    }
    
    public static boolean isNum(String num){
        try {
            Double.parseDouble(num);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
