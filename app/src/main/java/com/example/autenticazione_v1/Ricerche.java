package com.example.autenticazione_v1;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    int giorno, mese, anno;
    ArrayList<String> dest;
    LinearLayout layout;
    int cont, nRichiesteCompatibili = 0;
    Richieste []r;
    Notifiche []n;
    TextView noResult;
    FirebaseStorage storage;
    StorageReference storageReference;
    final long DIM = 2048*2048;
    Bitmap bmp;


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

        //recupero tutte le informazioni inviate dalla pagina del filtro
        Bundle b = this.getArguments();                 //setta automaticamente la destinazione scelta nella mappa e non la rende modificabile
        partenza = b.getString("partenza", partenza);
        System.out.println(partenza);

        destinazione = b.getString("destinazione", destinazione);
        System.out.println(destinazione);

        oraInizio = b.getString("oraInizio", oraInizio);
        System.out.println(oraInizio);

        oraFine = b.getString("oraFine", oraFine);
        System.out.println(oraFine);

        giorno = b.getInt("giorno", giorno);
        mese = b.getInt("mese", mese);
        anno = b.getInt("anno", anno);


        acceptReference = FirebaseDatabase.getInstance().getReference("richieste");
        mDatabase = FirebaseDatabase.getInstance().getReference("richieste");

        mDatabase.addValueEventListener(new ValueEventListener() {      //Recupero tutte le richieste sul database
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cont = (int) snapshot.getChildrenCount();
                r = new Richieste[cont];
                int i = 0;
                for (DataSnapshot richieste : snapshot.getChildren()) {
                        r[i] = richieste.getValue(Richieste.class);
                        i++;
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

            if(tmp[j].getGiorno() == giorno && tmp[j].getMese() == mese && tmp[j].getAnno() == anno && tmp[j].getPartenza().equals(partenza) && tmp[j].getDestinazione().equals(destinazione) && (tmp[j].getOra_Partenza() >= Integer.parseInt(oraInizio)) && tmp[j].getOra_Partenza() < Integer.parseInt(oraFine) && tmp[j].getPosti_disponibili() > 0 && !user.getUid().equals(tmp[j].getAutista())) {

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
            }
        }

        if(nRichiesteCompatibili == 0){     //Se non ci sono richieste compatibili compare un messaggio che avverte utente
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            noResult.setLayoutParams(p);
            noResult.setHeight(110);
            noResult.setTextSize(20);
            noResult.setPadding(10, 15,0, 0);

            noResult.setText("Nessuna richiesta compatibile trovata");
            return;
        }

    }
    public void addRichiesta(Richieste t, LayoutInflater inflater){
        View v = inflater.inflate(R.layout.layout_richieste, null);

        ImageView imagAutista = v.findViewById(R.id.imgAutista);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("images/"+ t.getAutista());

       storageReference.getBytes(DIM).addOnSuccessListener(new OnSuccessListener<byte[]>() {    //Scarico foto profilo utente
            @Override
            public void onSuccess(byte[] bytes) {
                bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);     //adatto la foto per essere inserita nell'image view
                imagAutista.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("FAIL");
            }
        });

       //setto i vari campi della richiesta da visualizzare
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
        b.setOnClickListener(new View.OnClickListener() {       //se clicco su accetta passaggio
            @Override
            public void onClick(View view) {
                int temp = t.getPosti_disponibili();

                mDatabase.child(t.getID()).child("posti_disponibili").setValue(temp-1);    //abbasso numero passeggeri della richiesta
                ArrayList<String> nuovo = t.getP().getPasseggeri();         //aggiorno la lista dei passeggeri aggiungendo il nuovo utente
                for(int i = 0; i < nuovo.size(); i++){
                    if(nuovo.get(i).equals("")){
                        nuovo.set(i, user.getUid());
                        break;
                    }
                }

                Passeggeri p = new Passeggeri();                //setto la lista sul database
                p.setPasseggeri(nuovo);
                mDatabase.child(t.getID()).child("p").setValue(p);

                DatabaseReference notifica = FirebaseDatabase.getInstance().getReference().child("notifiche");    //creo le notifiche per autista e passeggero
                notifica.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int cont_n = (int) snapshot.getChildrenCount();
                        n = new Notifiche[cont_n];
                        int i = 0;
                        for(DataSnapshot notifiche : snapshot.getChildren()){
                            n[i] = notifiche.getValue(Notifiche.class);
                            i++;
                        }

                        aggiornaDestinatari(n, cont_n, t.getID());
                        notifica.removeEventListener(this);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });


                //inserisco notifiche sul database
                DatabaseReference inserisciNotifica = notifica;
                String chiave = inserisciNotifica.push().getKey();

                ArrayList<String>accetta = new ArrayList<>();
                accetta.add(t.getAutista());

                Notifiche accettazione = new Notifiche("Accetta_passaggio", user.getUid(), accetta, t.getID(), chiave);
                inserisciNotifica.child(chiave).setValue(accettazione);

                Toast.makeText(getContext(), "Passaggio accettato correttamente", Toast.LENGTH_SHORT).show();

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, new HomeFragment());
                transaction.commit();
            }
        });
        layout.addView(v);
    }

    public void aggiornaDestinatari(Notifiche []n, int dim, String id_richiesta){   //aggiorno i destinatari della notifica di inserimento dell'autista per un eventuale cancellazione dello stesso
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