package com.example.autenticazione_v1;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

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
    TextView annulla, destinazione, visualizzaData;
    String dest, inizio, fine, part;
    Button cerca, selezData;
    int gg, mm ,aaaa;
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

        final Calendar prova = Calendar.getInstance();

        destinazione = view.findViewById(R.id.filtroDestinazione);
        Bundle b = this.getArguments();                 //setta automaticamente la destinazione scelta nella mappa e non la rende modificabile
        dest = b.getString("key", dest);
        //System.out.println(dest);
        destinazione.setText(dest);

        partenza = view.findViewById(R.id.filtroPartenza);          //assegno le varie opzioni agli elementi "Spinner"
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

        annulla = view.findViewById(R.id.filtroAnnulla);            //click per tornare all'HomeFragment
        annulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, new HomeFragment());
                transaction.commit();
            }
        });

        visualizzaData = view.findViewById(R.id.filtroData);
        selezData = view.findViewById(R.id.selezDataFiltro);
        selezData.setOnClickListener(new View.OnClickListener() {           //aggiunta del menu per selezionare data al click di "Seleziona Data"
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        visualizzaData.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        gg = dayOfMonth;
                        mm = monthOfYear+1;
                        aaaa = year;

                    }
                },
                        year, month, day);
                datePickerDialog.show();

            }
        });

        cerca = view.findViewById(R.id.bottoneRicerca);
        cerca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {                //prima di reindirizaare utente alla pagina successiva verifico i vincoli imposti

                part = partenza.getSelectedItem().toString();

                if(part.equals(dest)){          //se partenza e destinazione coincidono avviso l'utente
                    Toast.makeText(getActivity(), "Partenza e destinazione non possono coincidere", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(Integer.parseInt(oraInizio.getSelectedItem().toString()) >= Integer.parseInt(oraFine.getSelectedItem().toString())){         //Se fascia oraria non valida avviso l'utente
                    Toast.makeText(getActivity().getApplicationContext(), "Orario fascia non valido", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(aaaa < prova.get(Calendar.YEAR)){            //se data passata avviso l'utente
                    Toast.makeText(getActivity().getApplicationContext(), "Seleziona una data valida", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(aaaa == prova.get(Calendar.YEAR)){
                    if(mm < prova.get(Calendar.MONTH)+1) {
                        Toast.makeText(getActivity().getApplicationContext(), "Seleziona una data valida", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if (mm == prova.get(Calendar.MONTH)+1 && gg < prova.get(Calendar.DAY_OF_MONTH)) {
                        Toast.makeText(getActivity().getApplicationContext(), "Seleziona una data valida", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                inizio = oraInizio.getSelectedItem().toString();
                fine = oraFine.getSelectedItem().toString();

                replaceFragment(new Ricerche());
            }
        });

        return view;
    }

    private void replaceFragment(Fragment fragment){            //funzione che effettua il cambio del fragment al click del bottone
        FragmentManager fragmentManager = getParentFragmentManager();               //INVIO I DATI DEL FILTRO ALLA PAGINA "RICERCHE" TRAMITE BUNDLE
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        Bundle b = new Bundle();
        b.putString("destinazione", dest);
        b.putString("partenza", part);
        b.putString("oraInizio", inizio);
        b.putString("oraFine", fine);
        b.putInt("giorno", gg);
        b.putInt("mese", mm);
        b.putInt("anno", aaaa);
        fragment.setArguments(b);
        fragmentTransaction.commit();
    }
}