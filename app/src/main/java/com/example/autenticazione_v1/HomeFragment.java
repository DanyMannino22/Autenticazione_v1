package com.example.autenticazione_v1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    public String destinazione;

    FirebaseUser user;
    FirebaseAuth auth;

    DatabaseReference mDatabase;
    Utente u;
    private boolean MACCHINA_DISPONIBILE = false;

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

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        //inizializzo osm
        Context ctx = getContext().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //Inserisco mappa all'interno del fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        MapView map = (MapView) view.findViewById(R.id.mapView);

        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());    //Verifico se utente ha macchina disponibile

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                /*for(DataSnapshot utenti : snapshot.getChildren()){
                    //System.out.println(utenti.getKey());
                    if(utenti.getKey().equals(user.getUid())){
                        u = utenti.getValue(Utente.class);          //prendo riferimento utente loggato
                        //System.out.println(u.getNome());
                        //tmp = snapshot.getValue(Utente.class);
                        break;
                    }
                }*/


                //u = snapshot.getChildren(user_id);
                u = snapshot.getValue(Utente.class);

                HomeFragment.this.onItemsObtained(u);           //uso questa funzione per evitare di ottenere oggetto null
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("FAIL DATABASE");
            }
        });


        //setto i paramteri della mappa con centro puntato su Catania
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
        map.setMultiTouchControls(true);
        mapController = map.getController();
        mapController.setZoom(15);
        GeoPoint startPoint = new GeoPoint(37507877, 15083030);
        mapController.setCenter(startPoint);


        Drawable markerDrawable = ContextCompat.getDrawable(getContext().getApplicationContext(), R.drawable.pinpoint); //assegno il marker a tutti i POI

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

        GeoPoint PalazzoScienze = new GeoPoint(37515555, 15095440);
        OverlayItem overlayPalazzo = new OverlayItem("Palazzo delle Scienze", "Catania", PalazzoScienze);
        overlayPalazzo.setMarker(markerDrawable);

        GeoPoint PiazzaUniversita = new GeoPoint(37503580, 15086820);
        OverlayItem overlayPiazza = new OverlayItem("Piazza università", "Catania", PiazzaUniversita);
        overlayPiazza.setMarker(markerDrawable);


        //Creo una lista di tutti i punti di interesse, e li aggiungo man mano
        ArrayList<OverlayItem> overlayItemArrayList = new ArrayList<>();
        overlayItemArrayList.add(overlayCittadella);
        overlayItemArrayList.add(overlayMonastero);
        overlayItemArrayList.add(overlayTorre);
        overlayItemArrayList.add(overlayPalazzo);
        overlayItemArrayList.add(overlayPiazza);

        //Inserisco funzioni che regolano il click dei marker

        ItemizedOverlay<OverlayItem> locationOverlay = new ItemizedIconOverlay<>(overlayItemArrayList, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemSingleTapUp(int i, OverlayItem overlayItem) {
               //Toast.makeText(getActivity(), "Località : "+overlayItem.getTitle() /*+"\nItem's Desc : "+overlayItem.getSnippet()*/, Toast.LENGTH_SHORT).show();

                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle("Scegli attività")
                        .setMessage("Destinazione:  " + overlayItem.getTitle())
                        .setPositiveButton("Macchina disponibile", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if(!MACCHINA_DISPONIBILE){
                                    Toast.makeText(getActivity(), "Macchina non disponibile, cambia impostazioni", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                dialogInterface.dismiss();
                                destinazione = overlayItem.getTitle();
                                replaceFragment(new Prenotazione());
                            }
                        })
                        .setNegativeButton("Ricerca Passaggio", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                Toast.makeText(getActivity(), "Ricerca Passaggio", Toast.LENGTH_SHORT).show();
                                destinazione = overlayItem.getTitle();
                                replaceFragment(new FiltroRicerche());
                            }
                        })

                        .create();

               dialog.show();
                return true; // Handled this event.
            }

            @Override
            public boolean onItemLongPress(int i, OverlayItem overlayItem) {
                return false;
            }
        }, getContext().getApplicationContext());

        //map.getOverlays().add(locationOverlay);
        map.getOverlays().add(locationOverlay);

        return view;
    }

    private void replaceFragment(Fragment fragment){            //funzione che effettua il cambio del fragment al click di macchina disponibile
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        Bundle b = new Bundle();
        b.putString("key", destinazione);
        fragment.setArguments(b);
        fragmentTransaction.commit();
    }

    public void onItemsObtained(Utente temp) {
        if(temp.getDisponibilitaVeicolo()){
            MACCHINA_DISPONIBILE = true;
        }
    }

    public void onDestroy() {
        super.onDestroy();
    }

}