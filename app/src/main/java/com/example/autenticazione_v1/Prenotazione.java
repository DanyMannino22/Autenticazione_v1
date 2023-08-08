package com.example.autenticazione_v1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Prenotazione#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Prenotazione extends Fragment {

    public String dest, partenza, data;
    private TextView destinazione, visualizzaData;
    private Spinner spinner, spinnerOre, spinnerMinuti, spinnerPosti;

    int gg, mm, aaaa;

    Button caricaRichiesta, selezionaData;
    String nomeCognome;
    FirebaseUser user;
    FirebaseAuth auth;
    DatabaseReference mDatabase, userReference, idReference;
    TextView annulla;
    Utente u;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Prenotazione() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Prenotazione.
     */
    // TODO: Rename and change types and number of parameters
    public static Prenotazione newInstance(String param1, String param2) {
        Prenotazione fragment = new Prenotazione();
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
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();  //prende il current user
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_prenotazione, container, false);

        //mDatabase = FirebaseDatabase.getInstance().getReference("richieste");

        mDatabase = FirebaseDatabase.getInstance().getReference();


        userReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

        userReference.addValueEventListener(new ValueEventListener() {              //Ricavo nome e cognome autista
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                u = snapshot.getValue(Utente.class);
                Prenotazione.this.onItemsObtained(u);           //uso questa funzione per evitare di ottenere oggetto null
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("FAIL DATABASE");
            }
        });

        final Calendar prova = Calendar.getInstance();

        destinazione = view.findViewById(R.id.textDestinazione);
        Bundle b = this.getArguments();                 //setta automaticamente la destinazione scelta nella mappa e non la rende modificabile
        dest = b.getString("key", dest);
        //System.out.println(dest);
        destinazione.setText(dest);

        spinner = view.findViewById(R.id.spinnerPartenza);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.Partenze, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);


        spinnerOre = view.findViewById(R.id.spinnerOre);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.Ore, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerOre.setAdapter(adapter1);

        spinnerMinuti = view.findViewById(R.id.spinnerMinuti);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.Minuti, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerMinuti.setAdapter(adapter2);

        spinnerPosti = view.findViewById(R.id.spinnerPosti);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.Posti, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerPosti.setAdapter(adapter3);

        selezionaData = view.findViewById(R.id.selezionaData);
        visualizzaData = view.findViewById(R.id.visualizzaData);

        // on below line we are adding click listener for our pick date button
        selezionaData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are getting
                // the instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our text view.
                                visualizzaData.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                gg = dayOfMonth;
                                mm = monthOfYear+1;
                                aaaa = year;

                                data = gg + "/" + mm + "/" + aaaa;
                                //Toast.makeText(getContext(), dayOfMonth + "-" + (monthOfYear + 1) + "-" + year, Toast.LENGTH_SHORT).show();

                            }
                        },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.show();

            }
        });


        annulla = view.findViewById(R.id.annulla);
        annulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });


        caricaRichiesta = view.findViewById(R.id.ConfermaDettagli);

        caricaRichiesta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String part;

                part = spinner.getSelectedItem().toString();

                if(part.equals(dest)){
                    Toast.makeText(getContext(), "Partenza e destinazione non possono coincidere", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(aaaa < prova.get(Calendar.YEAR)){
                    Toast.makeText(getContext(), "Seleziona una data valida", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(aaaa == prova.get(Calendar.YEAR)){
                    if(mm < prova.get(Calendar.MONTH)+1) {
                        Toast.makeText(getContext(), "Seleziona una data valida", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if (mm == prova.get(Calendar.MONTH)+1 && gg < prova.get(Calendar.DAY_OF_MONTH)) {
                        Toast.makeText(getContext(), "Seleziona una data valida", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }



                    //Richieste r = new Richieste(user.getUid(), part, dest, Integer.parseInt(spinnerPosti.getSelectedItem().toString()), Integer.parseInt(spinnerOre.getSelectedItem().toString()), Integer.parseInt(spinnerMinuti.getSelectedItem().toString()), Integer.parseInt(spinnerPosti.getSelectedItem().toString()), nomeCognome, "");

                idReference = mDatabase.child("richieste");
                String key = idReference.push().getKey(); //This is the value of your key

                //HashMap<String, String> passeggeri = new HashMap<String, String>();
                /*for(int i = 0; i < Integer.parseInt(spinnerPosti.getSelectedItem().toString()); i++){
                    passeggeri.put("passeggero"+(i+1), "");
                }*/

                ArrayList<String> lista = new ArrayList<String>();
                for(int i = 0; i < Integer.parseInt(spinnerPosti.getSelectedItem().toString()); i++){
                    lista.add("");
                }
                Passeggeri passeggeri = new Passeggeri(lista);

                Richieste r = new Richieste(user.getUid(), part, dest, Integer.parseInt(spinnerPosti.getSelectedItem().toString()), Integer.parseInt(spinnerOre.getSelectedItem().toString()), Integer.parseInt(spinnerMinuti.getSelectedItem().toString()), nomeCognome, key, gg, mm, aaaa, passeggeri);
                idReference.child(key).setValue(r).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity().getApplicationContext(), "Richiesta creata correttamente", Toast.LENGTH_SHORT).show();

                        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack if needed
                        transaction.replace(R.id.frame_layout, new HomeFragment());
// Commit the transaction
                        transaction.commit();
/*
                        Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                        startActivity(intent);

 */
                    }
                });

                /*mDatabase.push().setValue(r).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity(), "Richiesta creata correttamente", Toast.LENGTH_SHORT).show();




                        Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                        startActivity(intent);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Errore durante caricamento richiesta", Toast.LENGTH_SHORT).show();
                    }
                });*/
            }
        });

        return view;
    }

    public void onItemsObtained(Utente temp) {
        nomeCognome = temp.getNome() + " " + temp.getCognome();
    }
}