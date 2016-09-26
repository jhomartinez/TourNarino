package com.codigoj.tournarino;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.codigoj.tournarino.mundo.SitioTuristico;
import java.util.ArrayList;


/**
 * Created by Jhon Martinez on 14/06/2016.
 */
public class SitioTuristicoAdaptador extends RecyclerView.Adapter<SitioTuristicoAdaptador.SitioTuristicoViewHolder> {

    public ArrayList<SitioTuristico> items;
    public static final String TAG = "SitioAdapter";

    /**
     * Atributo para manejar la ejecución de la actividad en modo de dos paneles, es decir, podria estar funcionando en un dispositivo tablet.
     */
    private boolean mTwoPane;

    public SitioTuristicoAdaptador(ArrayList<SitioTuristico> items) {
        this.items = items;
    }

    @Override
    public SitioTuristicoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.a_a_a_row_sitio_turistico, parent,false);
        SitioTuristicoViewHolder sitio = new SitioTuristicoViewHolder(v);
        if (v.findViewById(R.id.sitio_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        return sitio;
    }

    @Override
    public void onBindViewHolder(SitioTuristicoViewHolder holder, int position) {

        holder.nombre.setText(items.get(position).getNombre());
        holder.descripcion.setText(items.get(position).getDescripcion());

        //Cargar la imagen proveniente del webservice
        final SitioTuristicoViewHolder holderEstatico = holder;
        ImageLoader img = VolleyController.getInstance().getImageLoader();
        // Si utilizamos una ImageView, cargo la imagen con ImageLoader
        img.get(items.get(position).getURLImagen(), new ImageLoader.ImageListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error al cargar la imagen: " + error.getMessage());
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                if (response.getBitmap() != null) {
                    // Cargamos la imagen en una ImageView
                    holderEstatico.imagen.setImageBitmap(response.getBitmap());

                    //OBTENER CONTENIDO AL CARGAR CADA UNA DE LA IMAGENES MOSTRADAS EN EL RECICLER VIEW
                    //int image = holderEstatico.imagen.get
                }
            }
        });

        //Cargo el objeto - item del webservice al holder
        holder.mItem = items.get(position);
        //Al hacer clic en alguno de los cargados anteriormente
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTwoPane)
                {
                    // Se ejecuta cuando es hace clic en un sitio turistico, pero éste esta funcionando en una tablet, para ver en conjunto
                    // el contenido de la lista y el detelle.
                    /*Bundle arguments = new Bundle();
                    //Hay que probar el siguiente codigo
                    arguments.putString(FragmentSitioTuristicoDetalle.ARG_ITEM_ID, String.valueOf(holderEstatico.mItem.getId_sitio()));
                    FragmentSitioTuristicoDetalle fragment = new FragmentSitioTuristicoDetalle();
                    fragment.setArguments(arguments);
                    Context context = v.getContext();
                    //Hay que probar el siguiente codigo
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.sitio_detail_container, fragment)
                            .commit();*/
                } else {
                    // Se ejecuta cuando es hace clic en un sitio turistico, pero éste esta funcionando en un smartphone
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ActividadSitioTuristicoDetalle.class);
                    //Se envia a la actividad como parametro el id del sitio_turistico. (nombre, valor)
                    intent.putExtra(FragmentSitioTuristicoDetalle.ARG_ITEM_ID, holderEstatico.mItem.getId_sitio());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class SitioTuristicoViewHolder extends RecyclerView.ViewHolder{
        public TextView nombre, descripcion;
        public ImageView imagen;
        public final View mView;
        public SitioTuristico mItem;

        public SitioTuristicoViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            nombre = (TextView)itemView.findViewById(R.id.cardNombre);
            descripcion = (TextView)itemView.findViewById(R.id.cardDescripcion);
            imagen = (ImageView)itemView.findViewById(R.id.cardImagen);
        }

        @Override
        public String toString() {
            return super.toString()+ " '" + nombre.getText() + "'";
        }
    }
}
