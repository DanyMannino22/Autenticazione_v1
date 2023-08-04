package com.example.autenticazione_v1;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Prenotazione#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Prenotazione extends Fragment {

    public String dest, partenza;
    private TextView destinazione;
    private Spinner spinner, spinnerOre, spinnerMinuti, spinnerPosti;

    Button caricaRichiesta;
    String nomeCognome;
    FirebaseUser user;
    FirebaseAuth auth;
    DatabaseReference mDatabase, userReference;
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

        mDatabase = FirebaseDatabase.getInstance().getReference("richieste");

        userReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

        userReference.addValueEventListener(new ValueEventListener() {              //Ricavo nomee cognome autista
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
                    Toast.makeText(getActivity(), "Partenza e destinazione non possono coincidere", Toast.LENGTH_SHORT).show();
                    return;
                }

                Richieste r = new Richieste(user.getUid(), part, dest, Integer.parseInt(spinnerPosti.getSelectedItem().toString()), Integer.parseInt(spinnerOre.getSelectedItem().toString()), Integer.parseInt(spinnerMinuti.getSelectedItem().toString()), Integer.parseInt(spinnerPosti.getSelectedItem().toString()), nomeCognome);
                mDatabase.push().setValue(r).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                });
            }
        });

        return view;
    }

    public void onItemsObtained(Utente temp) {
        nomeCognome = temp.getNome() + " " + temp.getCognome();
    }
}