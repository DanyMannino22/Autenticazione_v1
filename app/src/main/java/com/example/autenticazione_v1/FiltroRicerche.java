package com.example.autenticazione_v1;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FiltroRicerche#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FiltroRicerche extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Spinner partenza, oraInizio, oraFine;
    TextView annulla, destinazione;
    String dest, inizio, fine, part;
    Button cerca;
    public FiltroRicerche() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FiltroRicerche.
     */
    // TODO: Rename and change types and number of parameters
    public static FiltroRicerche newInstance(String param1, String param2) {
        FiltroRicerche fragment = new FiltroRicerche();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filtro_ricerche, container, false);

        destinazione = view.findViewById(R.id.filtroDestinazione);
        Bundle b = this.getArguments();                 //setta automaticamente la destinazione scelta nella mappa e non la rende modificabile
        dest = b.getString("key", dest);
        //System.out.println(dest);
        destinazione.setText(dest);

        partenza = view.findViewById(R.id.filtroPartenza);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.Partenze, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        partenza.setAdapter(adapter);

        oraInizio = view.findViewById(R.id.filtroOraInizio);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.Ore, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        oraInizio.setAdapter(adapter2);

        oraFine = view.findViewById(R.id.filtroOraFine);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.Ore, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        oraFine.setAdapter(adapter3);

        annulla = view.findViewById(R.id.filtroAnnulla);
        annulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack if needed
                transaction.replace(R.id.frame_layout, new HomeFragment());
// Commit the transaction
                transaction.commit();
            }
        });

        cerca = view.findViewById(R.id.bottoneRicerca);

        cerca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                part = partenza.getSelectedItem().toString();

                if(part.equals(dest)){
                    Toast.makeText(getActivity(), "Partenza e destinazione non possono coincidere", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(Integer.parseInt(oraInizio.getSelectedItem().toString()) >= Integer.parseInt(oraFine.getSelectedItem().toString())){
                    Toast.makeText(getActivity(), "Orario fascia non valido", Toast.LENGTH_SHORT).show();
                    return;
                }

                inizio = oraInizio.getSelectedItem().toString();
                fine = oraFine.getSelectedItem().toString();

                //System.out.println("OK");
                replaceFragment(new Ricerche());
            }
        });

        return view;
    }

    private void replaceFragment(Fragment fragment){            //funzione che effettua il cambio del fragment al click del bottone
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        Bundle b = new Bundle();
        b.putString("destinazione", dest);
        b.putString("partenza", part);
        b.putString("oraInizio", inizio);
        b.putString("oraFine", fine);
        fragment.setArguments(b);
        fragmentTransaction.commit();
    }
}