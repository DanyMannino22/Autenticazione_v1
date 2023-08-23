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
    int nNotifiche = 0, cont = 0, totale = 0;
    TextView noNotification;
    LinearLayout layout;
    Richieste []r;
    String nome_passeggero;
    Button cancellaPrenotazione;


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
        mDatabase.addValueEventListener(new ValueEventListener() {              //estraggo tutte le notifiche presenti sul database
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int cont = (int) snapshot.getChildrenCount();
                n = new Notifiche[cont];
                int i = 0;
                for (DataSnapshot notifiche : snapshot.getChildren()) {         //le conservo in un arrray e invoco la funzione per filtrarle e mostrarle all'utente
                    n[i] = notifiche.getValue(Notifiche.class);
                    i++;
                }
                NotificationFragment.this.onItemObtained(n, cont, inflater);
                mDatabase.removeEventListener(this);
            }
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("FAIL DATABASE");
            }
        });
        return view;
    }

    public void onItemObtained(Notifiche []tmp, int n, LayoutInflater inflater){
        int j;
        for(j = 0; j < n; j++){
            filtraNotifica(tmp[j], inflater, tmp);           //invoco la funzione che filtrra notifiche
        }
    }

    public void filtraNotifica(Notifiche t, LayoutInflater inflater, Notifiche []tmp){
        DatabaseReference richieste = FirebaseDatabase.getInstance().getReference().child("richieste");         //ricavo le richieste per aggiornare i dati
        richieste.addValueEventListener(new ValueEventListener() {                                                       //e passo l'array alla funzione di inserimento
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
                addNotifica(t,inflater, r, i, tmp);
                richieste.removeEventListener(this);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    void addNotifica(Notifiche t, LayoutInflater inflater, Richieste []r, int dim, Notifiche []notifications){
        View v = inflater.inflate(R.layout.layout_notifiche, null);
        int index = 0;

        TextView testo = v.findViewById(R.id.testo);           //casella di testo dove verranno scritti i dettagli della notifica

        //Ogni notifica viene filtrata e, se rigurda l'utente loggato, viene mostrata a schermo, altrimenti viene scartata

        for(int i = 0; i < dim; i++){
                if(t.getTipoNotifica().equals("inserimento") && t.getIDRichiestaRiferimento().equals(r[i].getID()) && t.getAutore().equals(user.getUid())) {
                    index = i;
                    testo.setText("Hai messo a disposizione l'auto per " + r[index].getPosti_auto() + " passeggeri giorno " + r[index].getGiorno() + "/" + r[index].getMese() + "/" + r[index].getAnno() +
                            " alle ore " + r[index].getOra_Partenza() + ":" + r[index].getMinutiPartenza() + " per la tratta " + r[index].getPartenza() + "-" + r[index].getDestinazione());
                    nNotifiche++;
                    layout.addView(v);
                    break;
                }
                else if(t.getTipoNotifica().equals("Accetta_passaggio") && t.getIDRichiestaRiferimento().equals(r[i].getID()) && t.getAutore().equals(user.getUid())){
                    index = i;
                    testo.setText("Hai accettato un passaggio da " + r[index].getNomeAutista() + " giorno " + r[index].getGiorno() + "/" + r[index].getMese() + "/" + r[index].getAnno() +
                            " alle ore " + r[index].getOra_Partenza() + ":" + r[index].getMinutiPartenza() + " per la tratta " + r[index].getPartenza() + "-" + r[index].getDestinazione());
                    nNotifiche++;
                    layout.addView(v);
                    break;
                }
                else if(t.getTipoNotifica().equals("Accetta_passaggio") && t.getIDRichiestaRiferimento().equals(r[i].getID())){
                    if(t.getDestinatari().get(0).equals(user.getUid())){
                        DatabaseReference utenti = FirebaseDatabase.getInstance().getReference().child("users");
                        utenti.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot users : snapshot.getChildren()){
                                    if(users.getKey().equals(t.getAutore())){
                                        String nome = users.getValue(Utente.class).getNome() + " " + users.getValue(Utente.class).getCognome();
                                        testo.append(nome);
                                        return;
                                    }
                                }
                                utenti.removeEventListener(this);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                        testo.setText("È stato accettato il passaggio da te offerto per giorno " + r[i].getGiorno() + "/" + r[i].getMese() + "/" + r[i].getAnno() +
                                " alle ore " + r[i].getOra_Partenza() + ":" + r[i].getMinutiPartenza() + " per la tratta " + r[i].getPartenza() + "-" + r[i].getDestinazione() + " da parte dell'utente ");
                        nNotifiche++;
                        layout.addView(v);
                        break;
                    }
                }
                else if(t.getTipoNotifica().equals("cancellazione") && t.getDestinatari().get(0).equals(user.getUid())){        //CORREGGERE
                    System.out.println(t.getDestinatari().get(0) + "cancellazione");
                    index = i;
                    testo.setText("Il passaggio che avevi accettato da " + t.getR().getNomeAutista() + " per  giorno " + t.getR().getGiorno() + "/" + t.getR().getMese() + "/" +t.getR().getAnno() +
                            " alle ore " + t.getR().getOra_Partenza() + ":" + t.getR().getMinutiPartenza() + " per la tratta " + t.getR().getPartenza() + "-" + t.getR().getDestinazione() + " è stato cancellato");
                    nNotifiche++;
                    layout.addView(v);
                    break;
                }
                else if(t.getTipoNotifica().equals("cancellazione_passaggio") && t.getDestinatari().get(0).equals(user.getUid())){        //CORREGGERE
                    index = i;
                    DatabaseReference utenti = FirebaseDatabase.getInstance().getReference().child("users");
                    utenti.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot users : snapshot.getChildren()){
                                if(users.getKey().equals(t.getAutore())){
                                    String nome = users.getValue(Utente.class).getNome() + " " + users.getValue(Utente.class).getCognome();
                                    testo.append(nome);
                                    return;
                                }
                            }
                            utenti.removeEventListener(this);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    testo.setText("Il passaggio accettato per  giorno " + t.getR().getGiorno() + "/" + t.getR().getMese() + "/" +t.getR().getAnno() +
                            " alle ore " + t.getR().getOra_Partenza() + ":" + t.getR().getMinutiPartenza() + " per la tratta " + t.getR().getPartenza() + "-" + t.getR().getDestinazione() + " è stato rifiutato da ");
                    nNotifiche++;
                    layout.addView(v);
                    break;
                }
                else{
                    continue;
                }
            }

        cancellaPrenotazione = v.findViewById(R.id.cancellaPrenotazione);
        if(t.getTipoNotifica().equals("inserimento") || t.getTipoNotifica().equals("Accetta_passaggio") && t.getAutore().equals(user.getUid())){
            cancellaPrenotazione.setVisibility(View.VISIBLE);
        }
        cancellaPrenotazione.setOnClickListener(new View.OnClickListener() {
            DatabaseReference notifiche = FirebaseDatabase.getInstance().getReference().child("notifiche");
            DatabaseReference richieste = FirebaseDatabase.getInstance().getReference().child("richieste");
            @Override
            public void onClick(View view) {
                if(t.getTipoNotifica().equals("inserimento")){          //setto azioni da effettuare se la notifica è di inserimento generata dal current user
                    System.out.println("Inserimento");
                    Richieste tmp = new Richieste();
                    ArrayList<String> destinatari = t.getDestinatari();
                    for(int i = 0; i < destinatari.size(); i++){          //ad ogni utente che aveva accettato il mio passaggio invio la notifica che quest'ultimo è stato cancellato
                        if(destinatari.get(i).equals("")){
                            break;
                        }
                        else {
                            String chiave = notifiche.push().getKey();
                            ArrayList<String> dest = new ArrayList<>();
                            dest.add(destinatari.get(i));
                            for(int j = 0; j < r.length; j++){
                                if(r[j].getID().equals(t.getIDRichiestaRiferimento())){
                                    tmp = r[j];
                                    break;
                                }
                            }
                            Notifiche n = new Notifiche("cancellazione", user.getUid(), dest, t.getIDRichiestaRiferimento(), chiave, tmp);       //Creo una notifica utile ad avvertire coloro che avevano accettato
                            notifiche.child(chiave).setValue(n);                                                                                           //il passaggio che quest'ultimo è stato cancellato
                        }
                    }

                    notifiche.child(t.getId()).removeValue();
                    richieste.child(t.getIDRichiestaRiferimento()).removeValue();       //rimuovo la richiesta cosi che non sia piu visibile ad altri utenti

                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_layout, new NotificationFragment());
                    transaction.commit();
                }

                //Gestisco caso in cui passeggero vuole cancellare la propria prenotazione
                else if(t.getTipoNotifica().equals("Accetta_passaggio")){

                    System.out.println("Richiesta = " + t.getId());
                    Richieste tmp = new Richieste();
                    int numero_attuale;
                    for(int j = 0; j < r.length; j++){
                        if(r[j].getID().equals(t.getIDRichiestaRiferimento())){
                            tmp = r[j];
                            break;
                        }
                    }

                    //incremento il numero di posti disponibili dopo la mia cancellazione
                    numero_attuale = tmp.getPosti_disponibili();
                    richieste.child(t.getIDRichiestaRiferimento()).child("posti_disponibili").setValue(numero_attuale+1);


                    //aggiorno l'array dei passeggeri della richiesta
                    ArrayList<String> array_aggiornato = new ArrayList<>();
                    array_aggiornato = tmp.getP().getPasseggeri();
                    for(int k =0; k < array_aggiornato.size(); k++){
                        if(array_aggiornato.get(k).equals(user.getUid())){
                            array_aggiornato.set(k, "");
                            break;
                        }
                    }
                    richieste.child(t.getIDRichiestaRiferimento()).child("p").child("passeggeri").setValue(array_aggiornato);

                    //Ricavo la lista dei destinatari della ntotifica di cancellazione passaggio da parte dell'autista
                    String id_notifica_inserimento = "";
                    ArrayList<String> destinatari_notifica_inserimento = new ArrayList<>();
                    for(int k = 0; k < notifications.length; k++){
                        if(notifications[k].getIDRichiestaRiferimento().equals(t.getIDRichiestaRiferimento()) && t.getDestinatari().get(0).equals(notifications[k].getAutore())){
                            id_notifica_inserimento = notifications[k].getId();
                            destinatari_notifica_inserimento = notifications[k].getDestinatari();
                            break;
                        }
                    }

                    //aggiorno l'array dei destinatari di eventuale cancellazione passaggio da parte di autista
                    for(int k =0; k < destinatari_notifica_inserimento.size(); k++){
                        if(destinatari_notifica_inserimento.get(k).equals(user.getUid())){
                            destinatari_notifica_inserimento.set(k, "");
                            break;
                        }
                    }

                    notifiche.child(id_notifica_inserimento).child("destinatari").setValue(destinatari_notifica_inserimento);


                    //Creo la notificache avverte l'autista che il passeggero ha cancellato la propria prenotazione per il passaggio
                    String chiave = notifiche.push().getKey();
                    ArrayList<String> dest = new ArrayList<>();
                    dest.add(t.getDestinatari().get(0));
                    Notifiche n = new Notifiche("cancellazione_passaggio", user.getUid(), dest, t.getIDRichiestaRiferimento(), chiave, tmp);
                    notifiche.child(chiave).setValue(n);
                    notifiche.child(t.getId()).removeValue();

                    //Aggiorno pagina notifiche
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_layout, new NotificationFragment());
                    transaction.commit();
                }
            }
        });

        /*if(nNotifiche == 0) {     //Se non ci sono notifiche compare un messaggio che avverte utente
            System.out.println("DADAFWAFAFAWF");
            //RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            //TextView text = new TextView(this.getContext());
            noNotification.setLayoutParams(p);
            noNotification.setHeight(110);
            noNotification.setTextSize(20);
            noNotification.setPadding(10, 15,0, 0);
            noNotification.setText("Nessuna notifica trovata");
        }*/

        System.out.println(nNotifiche);
    }
}