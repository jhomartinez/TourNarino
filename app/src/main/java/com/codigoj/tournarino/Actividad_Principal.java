package com.codigoj.tournarino;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.codigoj.tournarino.mundo.SitioTuristico;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Actividad_Principal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView reciclador;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adaptador;
    private RequestQueue mRequestQueue;
    private JsonArrayRequest arrayRequest;
    private ArrayList<SitioTuristico> listaDeSitioTuristicos;
    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map< Integer, SitioTuristico> ITEM_MAP = new HashMap<Integer, SitioTuristico>();
    public static final String URL_WEBSERVICE = "http://canchanow.com/API/tour/sitios/lista";
    public static final String BASEIMG = "http://canchanow.com/img/";
    public static final String TAG = "SitioAdapter";
    public static final String tag_json_arry = "json_array_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_actividad_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_buscar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        listaDeSitioTuristicos = new ArrayList<SitioTuristico>();
        //Escojo el tipo de layoutmanager e inicializo el reciclerView
        reciclador = (RecyclerView) findViewById(R.id.reciclador);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        reciclador.setLayoutManager(layoutManager);
        mRequestQueue = VolleyController.getInstance().getRequestQueue();

        cargarDatosWebservice();
    }

    /**
     * Metodo que carga los datos del web service. Nota: se ejecuta inmediatamente después del metodo OnCreate
     */
    public void cargarDatosWebservice() {
        arrayRequest = new JsonArrayRequest(Request.Method.GET,URL_WEBSERVICE,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        convertirJson(response);
                        //Coloco los datos dentro del reciclerView
                        adaptador = new SitioTuristicoAdaptador(listaDeSitioTuristicos);
                        reciclador.setAdapter(adaptador);
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
                        BASEIMG + sitio.getString("imagen1"));
                listaDeSitioTuristicos.add(nuevo);
                ITEM_MAP.put(nuevo.getId_sitio(),nuevo);
            } catch (JSONException e) {
                Log.e(TAG, "Error al convertir el Json: " + e.getMessage());
            }
        }
    }

    private Date pasarAFecha(String fechaModificacion) {
        SimpleDateFormat formatoDelTexto = new SimpleDateFormat("yyyy-MM-dd");
        Date fecha = null;
        try {
            fecha = formatoDelTexto.parse(fechaModificacion);
        } catch (ParseException ex) {
            ex.printStackTrace();
            Log.e("Error al converir fecha", "Verifica la conversion y la entrada del valor al metodo");
        }
        return fecha;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actividad__pricipal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public ArrayList<SitioTuristico> getListaDeSitioTuristicos() {
        return listaDeSitioTuristicos;
    }

    public void setListaDeSitioTuristicos(ArrayList<SitioTuristico> listaDeSitioTuristicos) {
        this.listaDeSitioTuristicos = listaDeSitioTuristicos;
    }
}
