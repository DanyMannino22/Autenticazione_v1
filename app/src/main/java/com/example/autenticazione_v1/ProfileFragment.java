package com.example.autenticazione_v1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ImageView profilePic;
    FirebaseUser user;
    FirebaseAuth auth;
    TextView textNome, textCellulare, textMail, textAuto;
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference mDatabase;
    ImageButton back;
    Utente u, tmp;
    Bitmap bmp;

    public ProfileFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();  //prende il current user
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        final long DIM = 2048*2048;
        storage = FirebaseStorage.getInstance();     //sistema i riferimenti per accedere alla profile pic nello storage
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());      //sistema i riferimenti per accedere ai dati del database
        storageReference = storage.getReference("images/"+user.getUid());

        profilePic = view.findViewById(R.id.profilePict);

        storageReference.getBytes(DIM).addOnSuccessListener(new OnSuccessListener<byte[]>() {    //Scarico foto profilo utente
            @Override
            public void onSuccess(byte[] bytes) {
                bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);     //adatto la foto per essere inserita nell'image view
                profilePic.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("FAIL");
            }
        });

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                u = snapshot.getValue(Utente.class);                //ricavo l'utente interessato
                ProfileFragment.this.onItemsObtained(u);           //uso questa funzione per evitare di ottenere oggetto null
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("FAIL DATABASE");
            }
        });

        textNome = view.findViewById(R.id.casellaNome);
        textCellulare = view.findViewById(R.id.casellaCellulare);
        textMail = view.findViewById(R.id.casellaMail);
        textAuto = view.findViewById(R.id.casellaAuto);
        back = view.findViewById(R.id.tastoBack);

        back.setOnClickListener(new View.OnClickListener() {        //setto tasto Back
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    public void onItemsObtained(Utente temp){
        String email;
        tmp = temp;

        //Visualizzo i dati dell'utente
        textNome.setText(tmp.getNome() + " " + tmp.getCognome());
        textCellulare.setText(tmp.getCellulare());

        email = tmp.getEmail();
        textMail.setText(email.substring(0, email.indexOf("@")));

        boolean b = tmp.getDisponibilitaVeicolo();
        if(b)
            textAuto.setText("Disponibile");
        else
            textAuto.setText("NON Disponibile");
    }

}