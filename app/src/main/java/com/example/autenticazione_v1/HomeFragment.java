package com.example.autenticazione_v1;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    //parametri mappa
    //private MapView map;
    private IMapController mapController;
    private static final String TAG = "OsmActivity";
    private static final int PERMISSION_REQUEST_CODE = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        */

        //inizializzo osm
        Context ctx = getContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        //Inserisco mappa all'interno del fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        MapView map = (MapView) view.findViewById(R.id.mapView);

        //setto i paramteri della mappa con centro puntato su Catania
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
        map.setMultiTouchControls(true);
        mapController = map.getController();
        mapController.setZoom(15);
        GeoPoint startPoint = new GeoPoint(37507877, 15083030);
        mapController.setCenter(startPoint);


        Drawable markerDrawable = ContextCompat.getDrawable(getContext(), R.drawable.pinpoint); //assegno il marker a tutti i POI

        //definisco coordinate di tutti i POI e gli assegno il marker
        GeoPoint Cittadella = new GeoPoint(37525745, 15073806);
        OverlayItem overlayCittadella = new OverlayItem("Cittadella universitaria", "Catania", Cittadella);
        overlayCittadella.setMarker(markerDrawable);

        GeoPoint Monastero = new GeoPoint(37503591, 15080049);
        OverlayItem overlayMonastero = new OverlayItem("Monastero Benedettini", "Catania", Monastero);
        overlayMonastero.setMarker(markerDrawable);

        GeoPoint TorreBiologica = new GeoPoint(37530477, 15069159);
        OverlayItem overlayTorre = new OverlayItem("Torre biologica", "Catania", TorreBiologica);
        overlayTorre.setMarker(markerDrawable);


        //Creo una lista di tutti i punti di interesse, e li aggiungo man mano
        ArrayList<OverlayItem> overlayItemArrayList = new ArrayList<>();
        overlayItemArrayList.add(overlayCittadella);
        overlayItemArrayList.add(overlayMonastero);
        overlayItemArrayList.add(overlayTorre);

        //Inserisco funzioni che regolano il click dei marker

        ItemizedOverlay<OverlayItem> locationOverlay = new ItemizedIconOverlay<>(overlayItemArrayList, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemSingleTapUp(int i, OverlayItem overlayItem) {

               Toast.makeText(getActivity(), "Item's Title : "+overlayItem.getTitle() +"\nItem's Desc : "+overlayItem.getSnippet(), Toast.LENGTH_SHORT).show();
                return true; // Handled this event.
            }

            @Override
            public boolean onItemLongPress(int i, OverlayItem overlayItem) {
                return false;
            }
        }, getContext());

        //map.getOverlays().add(locationOverlay);
        map.getOverlays().add(locationOverlay);

        return view;
    }

}