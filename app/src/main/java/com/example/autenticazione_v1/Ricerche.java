package com.example.autenticazione_v1;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Ricerche#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Ricerche extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FirebaseUser user;
    FirebaseAuth auth;

    DatabaseReference mDatabase, acceptReference;
    Utente u;

    String destinazione, partenza, oraInizio, oraFine;
    LinearLayout layout;
    int cont, nRichiesteCompatibili = 0;
    Richieste []r;
    Richieste []valide;

    public Ricerche() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Ricerche.
     */
    // TODO: Rename and change types and number of parameters
    public static Ricerche newInstance(String param1, String param2) {
        Ricerche fragment = new Ricerche();
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
        View view = inflater.inflate(R.layout.fragment_ricerche, container, false);
        layout = view.findViewById(R.id.layout_ricerche);


        Bundle b = this.getArguments();                 //setta automaticamente la destinazione scelta nella mappa e non la rende modificabile
        partenza = b.getString("partenza", partenza);
        System.out.println(partenza);

        destinazione = b.getString("destinazione", destinazione);
        System.out.println(destinazione);

        oraInizio = b.getString("oraInizio", oraInizio);
        System.out.println(oraInizio);

        oraFine = b.getString("oraFine", oraFine);
        System.out.println(oraFine);
        //destinazione.setText(dest);


        acceptReference = FirebaseDatabase.getInstance().getReference("richieste");


        mDatabase = FirebaseDatabase.getInstance().getReference("richieste");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cont = (int) snapshot.getChildrenCount();
                r = new Richieste[cont];
                int i = 0;
                for (DataSnapshot richieste : snapshot.getChildren()) {
                    //System.out.println(utenti.getKey());
                        r[i] = richieste.getValue(Richieste.class);
                        i++;
                    //addRichiesta();
                }

                Ricerche.this.onItemObtained(r, cont);
            }

            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("FAIL DATABASE");
            }
        });



       // for(int i = 0; i < 5; i++){
         //   addRichiesta();
        //}
        return view;
    }

    public void onItemObtained(Richieste []tmp, int n){

        int j;
        for(j = 0; j < n; j++){         //Aggiungo richieste compatibili ai filtri applicati
            if(tmp[j].getPartenza().equals(partenza) && tmp[j].getDestinazione().equals(destinazione) && (tmp[j].getOra_Partenza() >= Integer.parseInt(oraInizio)) && tmp[j].getOra_Partenza() < Integer.parseInt(oraFine) && tmp[j].getPosti_disponibili() > 0){
                nRichiesteCompatibili++;
                addRichiesta(tmp[j]);

            }
        }

        if(nRichiesteCompatibili == 0){     //Se non ci sono richieste compatibili compare un messaggio che avverte utente
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            TextView text = new TextView(this.getContext());
            text.setLayoutParams(p);
            text.setHeight(110);
            text.setTextSize(20);
            text.setPadding(10, 15,0, 0);
            text.setText("Nessuna richiesta compatibile trovata");

            layout.addView(text);
            return;
        }

    }


    public void addRichiesta(Richieste t){
        View v = getLayoutInflater().inflate(R.layout.layout_richieste, null);

        TextView partenza = v.findViewById(R.id.partenzaRicerca);
        partenza.setText(t.getPartenza());

        TextView arrivo = v.findViewById(R.id.arrivoRicerca);
        arrivo.setText(t.getDestinazione());

        TextView autista = v.findViewById(R.id.autistaRicerca);
        autista.setText(t.getNomeAutista());

        TextView orario = v.findViewById(R.id.orarioRicerca);
        orario.setText("  " + t.getOra_Partenza() + " : " + t.getMinutiPartenza());

        Button b = v.findViewById(R.id.accettaPassaggio);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //System.out.println(t.getAutista());
                int temp = t.getPosti_disponibili();

                mDatabase.child(t.getID()).child("posti_disponibili").setValue(temp-1);
                Toast.makeText(getActivity(), "Passaggio accettato correttamente", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                startActivity(intent);



                /*acceptReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mDatabase.child(t.getID()).child("posti_disponibili").setValue(temp-1);

                        Toast.makeText(getActivity(), "Passaggio accettato correttamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });*/


               /* mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                       for (DataSnapshot r : snapshot.getChildren()) {
                            if(r.getValue(Richieste.class).getID().equals(t.getID())){
                                System.out.println(r.getValue(Richieste.class).getID() + " " + t.getID());
                                r.getValue(Richieste.class).setPosti_didponibili(0);
                                return;
                            }
                        }

                        Toast.makeText(getActivity(), "Passaggio accettato correttamente", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });*/
            }
        });

        v.findViewById(R.id.textView17);
        layout.addView(v);
    }
}