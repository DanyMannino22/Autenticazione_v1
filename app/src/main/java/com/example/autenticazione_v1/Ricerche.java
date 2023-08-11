package com.example.autenticazione_v1;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
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

import java.util.ArrayList;
import java.util.Objects;

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
    ArrayList<String> dest;
    LinearLayout layout;
    int cont, nRichiesteCompatibili = 0;
    Richieste []r;
    Notifiche []n;
    TextView noResult;

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
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();  //prende il current user
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ricerche, container, false);

        layout = view.findViewById(R.id.layout_ricerche);
        noResult = view.findViewById(R.id.nessunRisultato);

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

                Ricerche.this.onItemObtained(r, cont, inflater);
            }

            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("FAIL DATABASE");
            }
        });


        return view;
    }

    public void onItemObtained(Richieste []tmp, int n, LayoutInflater inflater){

        int j;
        for(j = 0; j < n; j++){         //Aggiungo richieste compatibili ai filtri applicati
            boolean prenotato = false;
            /*
            if(tmp[j].getPartenza().equals(partenza) && tmp[j].getDestinazione().equals(destinazione) && (tmp[j].getOra_Partenza() >= Integer.parseInt(oraInizio)) && tmp[j].getOra_Partenza() < Integer.parseInt(oraFine) && tmp[j].getPosti_disponibili() > 0 && !user.getUid().equals(tmp[j].getAutista())){
                nRichiesteCompatibili++;
                addRichiesta(tmp[j], inflater);

            }
             */

            if(tmp[j].getPartenza().equals(partenza) && tmp[j].getDestinazione().equals(destinazione) && (tmp[j].getOra_Partenza() >= Integer.parseInt(oraInizio)) && tmp[j].getOra_Partenza() < Integer.parseInt(oraFine) && tmp[j].getPosti_disponibili() > 0 && !user.getUid().equals(tmp[j].getAutista())) {
                //int dim = tmp[j].getP().getPasseggeri().isEmpty();
                //System.out.println(dim);
                /*for (int i = 0; i < tmp[j].getP().getPasseggeri().size(); i++) {
                    if (tmp[j].getP().getPasseggeri().get(i).equals(user.getUid())) {
                        prenotato = true;
                    }
                }*/
                //if(prenotato == false){

                for (int i = 0; i < tmp[j].getP().getPasseggeri().size(); i++) {            //verifico se già prenotato per quella richista
                    if (tmp[j].getP().getPasseggeri().get(i).equals(user.getUid())) {
                        prenotato = true;
                        break;
                    }
                }

                if(prenotato == false) {            //se non risulta prenotato, la mostro all'utente, altrimenti la scarto
                    nRichiesteCompatibili++;
                    addRichiesta(tmp[j], inflater);
                }
                //}
            }
        }

        if(nRichiesteCompatibili == 0){     //Se non ci sono richieste compatibili compare un messaggio che avverte utente
            //RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            //TextView text = new TextView(this.getContext());
            noResult.setLayoutParams(p);
            noResult.setHeight(110);
            noResult.setTextSize(20);
            noResult.setPadding(10, 15,0, 0);


            noResult.setText("Nessuna richiesta compatibile trovata");

            //layout.addView(noResult);
            return;
        }

    }


    public void addRichiesta(Richieste t, LayoutInflater inflater){
        View v = inflater.inflate(R.layout.layout_richieste, null);

        TextView partenza = v.findViewById(R.id.partenzaRicerca);
        partenza.setText(t.getPartenza());

        TextView arrivo = v.findViewById(R.id.arrivoRicerca);
        arrivo.setText(t.getDestinazione());

        TextView autista = v.findViewById(R.id.autistaRicerca);
        autista.setText(t.getNomeAutista());

        TextView orario = v.findViewById(R.id.orarioRicerca);
        orario.setText("  " + t.getOra_Partenza() + " : " + t.getMinutiPartenza());

        TextView data = v.findViewById(R.id.dataRicerca);
        data.setText(" " + t.getGiorno() + "/" + t.getMese() + "/" + t.getAnno());

        Button b = v.findViewById(R.id.accettaPassaggio);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //System.out.println(t.getAutista());
                int temp = t.getPosti_disponibili();

                mDatabase.child(t.getID()).child("posti_disponibili").setValue(temp-1);


                ArrayList<String> nuovo = t.getP().getPasseggeri();
                for(int i = 0; i < nuovo.size(); i++){
                    if(nuovo.get(i).equals("")){
                        nuovo.set(i, user.getUid());
                        break;
                    }
                }

                Passeggeri p = new Passeggeri();
                p.setPasseggeri(nuovo);
                mDatabase.child(t.getID()).child("p").setValue(p);

                //System.out.println(t.getID());
                //System.out.println(id_richiesta);

                DatabaseReference notifica = FirebaseDatabase.getInstance().getReference().child("notifiche");
                notifica.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int cont_n = (int) snapshot.getChildrenCount();
                        n = new Notifiche[cont_n];
                        int i = 0;
                        for(DataSnapshot notifiche : snapshot.getChildren()){
                            n[i] = notifiche.getValue(Notifiche.class);
                            i++;
                            System.out.println("Ciaoo");
                        }

                        aggiornaDestinatari(n, cont_n, t.getID());
                        notifica.removeEventListener(this);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });


                DatabaseReference inserisciNotifica = notifica;
                String chiave = inserisciNotifica.push().getKey();

                ArrayList<String>accetta = new ArrayList<>();
                accetta.add(t.getAutista());

                Notifiche accettazione = new Notifiche("Accetta_passaggio", user.getUid(), accetta, t.getID(), chiave);
                inserisciNotifica.child(chiave).setValue(accettazione);

                /*notifica.addValueEventListener(new ValueEventListener() {

                    //ArrayList<String> dest;
                   // String id_notifica;
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {              //aggiungo utente nella lista interessati in caso di cancellazione da parte dell'autista
                        for(DataSnapshot notifiche : snapshot.getChildren()){
                            Notifiche n = notifiche.getValue(Notifiche.class);
                            //System.out.println(n.getIDRichiestaRiferimento());

                            if(n.getIDRichiestaRiferimento().equals(id_richiesta)){
                                id_notifica = notifiche.getKey();
                                dest = n.getDestinatari();
                                System.out.println(id_notifica);
                                System.out.println(dest.size());

                                for(int k = 0; k < dest.size(); k++) {
                                    if (dest.get(k).equals("")) {
                                        dest.set(k, user.getUid());
                                        break;
                                    }
                                }
                                break;
                            }
                        }

                        DatabaseReference modificaRichiesta = FirebaseDatabase.getInstance().getReference().child("notifiche").child(id_notifica).child("destinatari");
                        modificaRichiesta.setValue(dest);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                 */

                /*for(int k = 0; k < dest.size(); k++){
                    if(dest.get(k).equals("")){
                        dest.set(k, user.getUid());
                        break;
                    }
                }

                 */

//                notifica.child(id_notifica).child("destinatari").setValue(dest);


                //mDatabase.child(t.getID()).child("p").child("passeggeri")
            /*
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Passeggeri updated = new Passeggeri();
                        for (DataSnapshot tmp : snapshot.getChildren()) {
                            Richieste temp = tmp.getValue(Richieste.class);

                            if (temp.getID().equals(t.getID())) {
                                System.out.println("TRUE");
                                int dim = temp.getP().getPasseggeri().size();
                                ArrayList<String> nuovo = temp.getP().getPasseggeri();
                                for (int k = 0; k < dim; k++) {

                                    //System.out.println("BREAK");

                                    //   if (temp.getP().getPasseggeri().get(i).equals("")) {
                                    //      System.out.println("Ciaoooo");
                                    //     temp.getP().setPasseggero(i, user.getUid());
                                    //    System.out.println(temp.getP().getPasseggeri());
                                    //   return;
                                    //}


                                    if (nuovo.get(k).equals("")) {
                                        nuovo.set(k, user.getUid());
                                      //  System.out.println("Inserisco");
                                        break;
                                    }
                                }
                                updated.setPasseggeri(nuovo);
                                //System.out.println(updated);
                                temp.setP(updated);
                                DatabaseReference prova = FirebaseDatabase.getInstance().getReference().child("richieste");
                                prova.child(t.getID()).child("p").child("passeggeri").setValue(nuovo);
                                return;
                            }
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

              */
                /*mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot passegg  : snapshot.getChildren()){
                            System.out.println(passegg.child("p").child("passeggeri").child("0").getValue());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                 */

               /* mDatabase.child(t.getID()).child("passeggeri");
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot richieste : snapshot.getChildren()) {
                            //System.out.println(utenti.getKey());
                            if(richieste.getValue().equals("")){
                                System.out.println("Vuoto");
                            }
                            //addRichiesta();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            */

                Toast.makeText(getContext(), "Passaggio accettato correttamente", Toast.LENGTH_SHORT).show();

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

    public void aggiornaDestinatari(Notifiche []n, int dim, String id_richiesta){
        String id_notifica = "";
        ArrayList<String> dest = new ArrayList<>();

        for(int c = 0; c <dim; c++){
            if(n[c].getIDRichiestaRiferimento().equals(id_richiesta)){
                dest = n[c].getDestinatari();
                id_notifica = n[c].getId();
                break;
            }
        }

        for(int i = 0; i < dest.size(); i++){
            if (dest.get(i).equals("")) {
                dest.set(i, user.getUid());
                break;
            }
        }
        DatabaseReference aggiornaNotifica = FirebaseDatabase.getInstance().getReference().child("notifiche").child(id_notifica).child("destinatari");
        aggiornaNotifica.setValue(dest);
    }

}