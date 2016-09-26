package com.codigoj.tournarino;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codigoj.tournarino.mundo.SitioTuristico;


/**
 * Este fragment representa un Sitio Turistico de la lista seleccionado por el usuario.

 */
public class FragmentSitioTuristicoDetalle extends Fragment {
    /**
     * Atributo para el argumento de fragment que representa el ID del item que este fragment representa
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * El Sitio Turistico que este fragment muestra.
     */
    private SitioTuristico mItem;

    public FragmentSitioTuristicoDetalle() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     */
    // TODO: Rename and change types and number of parameters
    /*public static FragmentSitioTuristicoDetalle newInstance(String param1) {
        FragmentSitioTuristicoDetalle fragment = new FragmentSitioTuristicoDetalle();
        Bundle args = new Bundle();
        args.putString(ARG_ITEM_ID, param1);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM_ID)) {

            mItem = Actividad_Principal.ITEM_MAP.get(getArguments().getInt(ARG_ITEM_ID));
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout_detalle);
            if (appBarLayout != null)
            {
                appBarLayout.setTitle(mItem.getNombre());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.b_sitio_turistico_detail, container, false);
        if (mItem != null){
            ((TextView) rootView.findViewById(R.id.detail_area)).setText(mItem.getDescripcion());
        }
        return rootView;
    }


}
