package com.example.autenticazione_v1;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    FirebaseUser user;
    FirebaseAuth auth;
    Notifiche []n;
    int nNotifiche = 0;
    TextView noNotification;
    LinearLayout layout;
    Richieste []r;


    public NotificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
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

        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        noNotification = view.findViewById(R.id.nessunaNotifica);
        layout = view.findViewById(R.id.layoutNotifiche);


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("notifiche");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int cont = (int) snapshot.getChildrenCount();
                n = new Notifiche[cont];
                int i = 0;
                for (DataSnapshot notifiche : snapshot.getChildren()) {
                    //System.out.println(utenti.getKey());
                    n[i] = notifiche.getValue(Notifiche.class);
                    i++;
                    //addRichiesta();
                }

                NotificationFragment.this.onItemObtained(n, cont, inflater);
            }

            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("FAIL DATABASE");
            }
        });

        return view;
    }

    public void onItemObtained(Notifiche []tmp, int n, LayoutInflater inflater){

        int j;
        for(j = 0; j < n; j++){         //Aggiungo richieste compatibili ai filtri applicati
            if(tmp[j].getAutore().equals(user.getUid())) {   //Se utente è autore della notifica, faccio comparire l'avviso
                nNotifiche++;
                filtraNotifica(tmp[j], inflater);
            }
        }


        if(nNotifiche == 0){     //Se non ci sono richieste compatibili compare un messaggio che avverte utente
            //RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            //TextView text = new TextView(this.getContext());
            noNotification.setLayoutParams(p);
            noNotification.setHeight(110);
            noNotification.setTextSize(20);
            noNotification.setPadding(10, 15,0, 0);


            noNotification.setText("Nessuna notifica trovata");

            //layout.addView(noResult);
            return;
        }
    }

    public void filtraNotifica(Notifiche t, LayoutInflater inflater){

        DatabaseReference richieste = FirebaseDatabase.getInstance().getReference().child("richieste");

        richieste.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int n_richieste = (int)snapshot.getChildrenCount();
                r = new Richieste[n_richieste];
                int i = 0;
                for(DataSnapshot tmp : snapshot.getChildren()){
                    {
                       // if(tmp.getValue(Richieste.class).getID().equals(t.getIDRichiestaRiferimento())) {
                            r[i] = tmp.getValue(Richieste.class);
                            i++;
                         //   break;
                        //}
                    }
                }
                addNotifica(t,inflater, r, i);
                richieste.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    void addNotifica(Notifiche t, LayoutInflater inflater, Richieste []r, int dim){
        View v = inflater.inflate(R.layout.layout_notifiche, null);
        int index = 0;
        for(int i = 0; i < dim; i++){
                System.out.println("Ciaoo");
                if(t.getTipoNotifica().equals("inserimento") && t.getIDRichiestaRiferimento().equals(r[i].getID())) {
                    index = i;
                    break;
                }
                else{
                    TextView testo = v.findViewById(R.id.testo);
                    testo.setText("Accettazione");
                }
            }

        TextView testo = v.findViewById(R.id.testo);
        testo.setText("Hai messo a disposizione l'auto per " + r[index].getPosti_auto() + " passeggeri giorno " + r[index].getGiorno() + "/" + r[index].getMese() + "/" + r[index].getAnno() +
                " alle ore " + r[index].getOra_Partenza() + ":" + r[index].getMinutiPartenza() + " per la tratta " + r[index].getPartenza() + "-" + r[index].getDestinazione());

        layout.addView(v);
    }
}