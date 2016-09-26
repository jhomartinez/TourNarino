package com.codigoj.tournarino;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.codigoj.tournarino.Datos.AdminSQLiteOpenHelper;
import com.codigoj.tournarino.mundo.SitioTuristico;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
public class ActividadSplashScreen extends AppCompatActivity {

    private ProgressBar barraHorizontal, progressBarcircular;
    private static final long SPLASH_SCREEN_DELAY = 3000;
    private RequestQueue mRequestQueue;
    private JsonArrayRequest arrayRequest;
    private ArrayList<SitioTuristico> listaDeSitioTuristicosWS = new ArrayList<SitioTuristico>();;
    private ArrayList<SitioTuristico> listaDeSitioTuristicosLocal = new ArrayList<SitioTuristico>();
    private AdminSQLiteOpenHelper bd;
    private SQLiteDatabase manejador;

    //-----CONSTANTES
    public static final String URL_WEBSERVICE = "http://canchanow.com/API/tour/sitios/lista";
    public static final String BASEIMG = "http://canchanow.com/img/";
    public static final String TAG = "SitioAdapter";
    public static final String tag_json_arry = "json_array_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_activity_splash_screen);

        barraHorizontal = (ProgressBar) findViewById(R.id.progressBarHorizontal);
        barraHorizontal.setProgress(0);
        progressBarcircular = (ProgressBar) findViewById(R.id.progressBarCircular);
        progressBarcircular.setProgress(0);
        if (conectadoAInternet())
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
            AsynTaskCargaInicio cargaInicio = new AsynTaskCargaInicio();
            cargaInicio.execute();
        }
        else
        {
            Toast.makeText(this, "Se requiere conexión a internet para ver contenido actualizado", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Metodo que verifica que el dispositivo este conectado a internet, valida si esta conectado por datos móviles o por wifi
     * Hace falta VALIDAR SI EL USUARIO QUIERE QUE NO SE DESCARGUE CONTENIDO POR RED MÓVIL.
     * @return True si esta conectado False en caso contrario.
     */
    protected boolean conectadoAInternet(){
        boolean conectado= false;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                Toast.makeText(this, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
                conectado= true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                Toast.makeText(this, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
                conectado=true;
            }
        }
        return conectado;
    }

    public class AsynTaskCargaInicio extends AsyncTask<Void, Integer, Void> {
        int progreso;
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "Sitios.db";


        @Override
        protected Void doInBackground(Void... params) {

            cargarDatosWebservice();
            cargarDatosLocal();
            int tamanio = listaDeSitioTuristicosWS.size();
            for (int i = 0; i < tamanio; i++) {
                SitioTuristico sitioWS,sitioLocal;
                sitioWS = listaDeSitioTuristicosWS.get(i);
                if (!listaDeSitioTuristicosLocal.isEmpty() ){
                    //COMPARAR ENTRE LISTAS
                    //Busco si existe el sitioWS en la lista de sitios locales
                    if (buscarSitioLocalPorId(sitioWS.getId_sitio()) != null){
                        sitioLocal = buscarSitioLocalPorId(sitioWS.getId_sitio());
                        //COMPARAR FECHAS DE MODIFICACION, SI ES NECESARIO ACTUALIZAR
                        if (sitioLocal.getFechaModificacion().before(sitioWS.getFechaModificacion())) {
                            sitioLocal = sitioWS;
                            //Metodo que actualiza en la base de datos localmente los datos e imagen como un recurso.
                            actualizarSitioLocalmente(sitioLocal, sitioWS.getId_sitio());
                        }
                    }
                    else{
                        guardarSitioLocalmente(sitioWS);
                    }
                }
                //LA LISTA LOCAL ESTA VACIA
                else
                {
                    //GUARDAR DATOS LOCALMENTE
                    guardarSitioLocalmente(sitioWS);
                }
                //Actualiza la barra de progreso
                progreso = calcularProgreso(i+1,tamanio);
                publishProgress(progreso);
            }
            return null;
        }

        /**
         * Metodo que calcula el porcentaje de avance de la tarea activa
         * @param i es el progreso actual
         * @param tamanio el tamaño maximo del progreso actual
         * @return un número entero como producto del calculo del progreso de la tarea actual.
         */
        private int calcularProgreso(int i, int tamanio) {
            double p = (i * 100)/tamanio;
            return (int) p;
        }

        /**
         * Metodo que agrega el sitio en la lista local, en base de datos localmente el sitioTuristico y la imagen como un recurso
         * @param sitioWS
         */
        private void guardarSitioLocalmente(SitioTuristico sitioWS) {
            descargarImagen(sitioWS);
            //agrego los sitios a la lista local
            listaDeSitioTuristicosLocal.add(sitioWS);
            //Metodo que agrega en la base de datos localmente el sitioTuristico y la imagen como un recurso
            bd.insertarSitio(manejador, sitioWS);
        }

        /**
         * Metodo que actualiza en la base de datos localmente los datos e imagen como un recurso.
         * @param sitioLocal es el sitio turistico a actualizar
         * @param id_sitio es el identificador del sitio
         */
        private void actualizarSitioLocalmente(SitioTuristico sitioLocal, int id_sitio) {
            //Descarga la imagen y actualiza el sitio turistico almacenado la ubicación de la imagen
            descargarImagen(sitioLocal);
            //Actualiza el sitio turistico en la base de datos
            bd.actualizarSitio(sitioLocal, String.valueOf(id_sitio));
        }

        /**
         * Metodo que descarga la imagen del sitioTuristico a la carpeta de la aplicación
         * @param sitio es el sitio turistico que contiene la dirección de imagen.
         */
        private void descargarImagen( SitioTuristico sitio ) {
            // Petición para obtener la imagen
            final String nombre = sitio.getURLImagen();
            final SitioTuristico sitiofinal = sitio;
            Toast.makeText(ActividadSplashScreen.this, "Paso",
                    Toast.LENGTH_SHORT).show();
            ImageRequest request = new ImageRequest(
                BASEIMG + nombre,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        //imagenPost.setImageBitmap(bitmap);

                        Context context =getApplicationContext();
                        String ruta = darRutaImagen(context, nombre, bitmap);
                        sitiofinal.setImagenGuardada(ruta);
                        Toast.makeText(ActividadSplashScreen.this, "cargo la imagen: "+ruta+"/"+ nombre,
                                Toast.LENGTH_SHORT).show();
                    }
                }, 0, 0, null,null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        //imagenPost.setImageResource(R.drawable.error);
                        Log.d(TAG, "Error en respuesta Bitmap: "+ error.getMessage());
                        Toast.makeText(ActividadSplashScreen.this, "No cargo la imagen: !"+ nombre,
                                Toast.LENGTH_SHORT).show();
                        Log.e("No cargo la imagen","Hubo un error en descargar la imagen: "+ nombre + "Descripcion: "+ error.getLocalizedMessage());
                    }
                });
            mRequestQueue.add(request);
        }

        /**
         * Metodo que obtiene la ubicacion de la imagen, sin definir ni el nombre ni el formato
         * @param contexto es el contexto para guardar en el directorio privado de la aplicación
         * @param nombre es el nombre de la imagen a guardar
         * @param imagen es la imagen en formato bitmap
         * @return la ruta de la imagen dentro de la estructura de directorios de la app.
         */
        private String darRutaImagen(Context contexto, String nombre, Bitmap imagen) {
            ContextWrapper context = new ContextWrapper(contexto);
            File rutaImages = context.getDir("Imagenes", Context.MODE_APPEND);
            File path = new File(rutaImages, nombre);

            FileOutputStream fos = null;
            try{
                fos = new FileOutputStream(path);
                imagen.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.flush();
                Toast.makeText(getApplicationContext(), "Guardo",Toast.LENGTH_SHORT).show();
            }catch (FileNotFoundException ex){
                ex.printStackTrace();
                Log.e("Excepcion FileNotFound", "imagen: "+ nombre);
            }catch (IOException ex){
                ex.printStackTrace();
                Log.e("Excepcion IOException", "imagen: "+ nombre);
            }
            return path.getAbsolutePath();
        }

        /**
         * Metodo que permite buscar un sitio turistico por el id del sitio
         * @param id_sitio es el id del sitio a buscar
         * @return el sitio turistico encotrado, de lo contrario null
         */
        private SitioTuristico buscarSitioLocalPorId(int id_sitio) {
            boolean encontrado = false;
            SitioTuristico sitio = null;
            for (int i = 0; i < listaDeSitioTuristicosLocal.size() && !encontrado; i++ )
            {
                SitioTuristico st = listaDeSitioTuristicosLocal.get(i);
                if (st.getId_sitio() == id_sitio){
                    sitio = st;
                    return sitio;
                }
            }
            return sitio;
        }

        private void cargarDatosLocal() {
            Cursor c = bd.buscarTodosSitios(manejador);
            if (c.getCount()>0) {
                while (c.moveToNext()) {
                    SitioTuristico nuevo = new SitioTuristico(
                            c.getInt(c.getColumnIndex(SitioTuristico.ID_SITIO)),
                            c.getString(c.getColumnIndex(SitioTuristico.NOMBRE)),
                            c.getString(c.getColumnIndex(SitioTuristico.DESCRIPCION)),
                            c.getString(c.getColumnIndex(SitioTuristico.LOCALIZACION)),
                            c.getInt(c.getColumnIndex(SitioTuristico.NO_VISITAS)),
                            c.getDouble(c.getColumnIndex(SitioTuristico.LONGITUD)),
                            c.getDouble(c.getColumnIndex(SitioTuristico.LATITUD)),
                            pasarAFecha(c.getString(c.getColumnIndex(SitioTuristico.FECHA_MODIFICACION))),
                            c.getString(c.getColumnIndex(SitioTuristico.URL_IMAGEN))
                    );
                    listaDeSitioTuristicosLocal.add(nuevo);
                }
            }
        }

        /**
         * Metodo que carga los datos del web service. Nota: se ejecuta inmediatamente después del metodo OnCreate
         */
        public void cargarDatosWebservice() {
            arrayRequest = new JsonArrayRequest(Request.Method.GET, URL_WEBSERVICE,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            convertirJson(response);
                            //Coloco los datos dentro del reciclerView
//                            adaptador = new SitioTuristicoAdaptador(listaDeSitioTuristicosWS);
//                            reciclador.setAdapter(adaptador);
                            Log.d(TAG, response.toString());
                            System.out.println(response.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.d(TAG, "Error: " + error.getMessage() + "Error al cargar los datos sel webservice");
                        }
                    }
            );
            mRequestQueue.add(arrayRequest);
        }

        public void convertirJson(JSONArray response) {
            // Variables locales
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject sitio = response.getJSONObject(i);
                    SitioTuristico nuevo = new SitioTuristico(
                            sitio.getInt("id_sitio"),
                            sitio.getString("nombre"),
                            sitio.getString("descripcion"),
                            sitio.getString("localizacion"),
                            sitio.getInt("noVisitas"),
                            sitio.getDouble("longitud"),
                            sitio.getDouble("latitud"),
                            pasarAFecha(sitio.getString("fechaModificacion")),
                            sitio.getString("imagen1"));
                    listaDeSitioTuristicosWS.add(nuevo);
//                    ITEM_MAP.put(nuevo.getId_sitio(),nuevo);
                } catch (JSONException e) {
                    Log.e(TAG, "Error al convertir el Json: " + e.getMessage());
                }
            }
        }

        /**
         * Metodo que permite convertir la fecha del formato String a Date para uso en la clase SitioTuristico.
         * @param fechaModificacion Es la fecha de la última modificación
         * @return La fecha convertida en Date o de lo contrario null
         */
        private Date pasarAFecha(String fechaModificacion) {
            SimpleDateFormat formatoDelTexto = new SimpleDateFormat("yyyy-MM-dd");
            Date fecha = null;
            try {
                fecha = formatoDelTexto.parse(fechaModificacion);
            } catch (ParseException ex) {
                ex.printStackTrace();
                Log.e("Error al converir fecha", "Verifica la conversion y la entrada del valor al metodo");
            } finally {
                return fecha;
            }
        }

        /**
         * Runs on the UI thread before {@link #doInBackground}.
         *
         * @see #onPostExecute
         * @see #doInBackground
         */
        @Override
        protected void onPreExecute() {
            progreso = 0;
            //Obtengo una instancia para volley
            mRequestQueue = VolleyController.getInstance().getRequestQueue();

            progressBarcircular.setVisibility(View.VISIBLE);
            //Creando administrador de bd SQLite
            bd = new AdminSQLiteOpenHelper(getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
            //Creo el manejador para la bd
            manejador = bd.getWritableDatabase();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //Metodo para Iniciar la siguiente Activity
            Intent mainIntent = new Intent().setClass(
                    ActividadSplashScreen.this, Actividad_Principal.class);
            startActivity(mainIntent);
            //Cierra la activity
            finish();
        }

        /**
         * Runs on the UI thread after {@link #publishProgress} is invoked.
         * The specified values are the values passed to {@link #publishProgress}.
         *
         * @param values The values indicating progress.
         * @see #publishProgress
         * @see #doInBackground
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            barraHorizontal.setProgress(values[0]);
            progressBarcircular.setProgress(values[0]);
        }
    }

    //----------------------
    //METODOS DE CLASE
    //----------------------

}
