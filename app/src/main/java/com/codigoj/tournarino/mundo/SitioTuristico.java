package com.codigoj.tournarino.mundo;

import android.content.ContentValues;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jhon Martinez on 14/06/2016.
 */
public class SitioTuristico {

    private int Id_sitio;
    private String Nombre;
    private String Descripcion;
    private String Localizacion;
    private int NoVisitas;
    private double Longitud;
    private double Latitud;
    private Date FechaModificacion;
    private String URLImagen;
    private String ImagenGuardada;

    //---------------------
    //CONSTANTES
    //---------------------
    public static final String NOMBRE_TABLA = "sitioTuristico";
    public static final String ID_SITIO = "id_sitio";
    public static final String NOMBRE = "nombre";
    public static final String DESCRIPCION = "descripcion";
    public static final String LOCALIZACION = "localizacion";
    public static final String NO_VISITAS = "noVisitas";
    public static final String LONGITUD = "longitud";
    public static final String LATITUD = "latitud";
    public static final String FECHA_MODIFICACION = "fechaModificacion";
    public static final String URL_IMAGEN = "URLImagen";
    public static final String IMAGEN_GUARDADA = "imagenGuardada";

    /**
     * Método contructor que crea un nuevo sitio turistico.
     * @param pId_sitio
     * @param pNombre
     * @param pDescripcion
     * @param pLocalizacion
     * @param pNoVisitas
     * @param pLongitud
     * @param pLatitud
     * @param pFechaModificacion
     * @param pURLImagen
     * <pre>Si no hay un recurso-imagen guardada tomará el valor de -1</pre>
     */
    public SitioTuristico( int pId_sitio, String pNombre, String pDescripcion, String pLocalizacion, int pNoVisitas, double pLongitud, double pLatitud,  Date pFechaModificacion, String pURLImagen) {
        this.Id_sitio = pId_sitio;
        this.Nombre = pNombre;
        this.Descripcion = pDescripcion;
        this.Localizacion = pLocalizacion;
        this.NoVisitas = pNoVisitas;
        this.Longitud = pLongitud;
        this.Latitud = pLatitud;
        this.FechaModificacion = pFechaModificacion;
        this.URLImagen = pURLImagen;
        this.ImagenGuardada = null;
    }

    public int getId_sitio() {
        return Id_sitio;
    }

    public void setId_sitio(int id_sitio) {
        Id_sitio = id_sitio;
    }

    public String getURLImagen() {
        return URLImagen;
    }

    public void setURLImagen(String URLImagen) {
        this.URLImagen = URLImagen;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getLocalizacion() {
        return Localizacion;
    }

    public void setLocalizacion(String localizacion) {
        Localizacion = localizacion;
    }

    public int getNoVisitas() {
        return NoVisitas;
    }

    public void setNoVisitas(int noVisitas) {
        NoVisitas = noVisitas;
    }

    public double getLongitud() {
        return Longitud;
    }

    public void setLongitud(double longitud) {
        Longitud = longitud;
    }

    public double getLatitud() { return Latitud;  }

    public void setLatitud(double latitud) {
        Latitud = latitud;
    }

    public Date getFechaModificacion() {
        return FechaModificacion;
    }

    public void setFechaModificacion(Date fechaModificacion) { FechaModificacion = fechaModificacion; }

    public String getImagenGuardada() { return ImagenGuardada;  }

    public void setImagenGuardada(String imagenGuardada) { ImagenGuardada = imagenGuardada; }

    //Metodo que devuelve el valor del objeto en un objeto ContentValues como pares clave-valor
    public ContentValues toContentValues() {
        //Formateo las fechas para el ingreso a la bd
        DateFormat formato =  new SimpleDateFormat("yyyy-MM-dd");
        String fecha = formato.format(FechaModificacion);
        ContentValues values = new ContentValues();
        values.put(SitioTuristico.ID_SITIO, Id_sitio);
        values.put(SitioTuristico.NOMBRE, Nombre);
        values.put(SitioTuristico.DESCRIPCION, Descripcion);
        values.put(SitioTuristico.LOCALIZACION, Localizacion);
        values.put(SitioTuristico.NO_VISITAS, NoVisitas);
        values.put(SitioTuristico.LONGITUD, Longitud);
        values.put(SitioTuristico.LATITUD, Latitud);
        values.put(SitioTuristico.FECHA_MODIFICACION, fecha);
        values.put(SitioTuristico.URL_IMAGEN, URLImagen);
        values.put(SitioTuristico.IMAGEN_GUARDADA, ImagenGuardada);
        return values;
    }
}
