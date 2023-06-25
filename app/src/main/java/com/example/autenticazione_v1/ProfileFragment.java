package com.example.autenticazione_v1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    TextView textNome, textCognome;
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference mDatabase;
    Utente u, tmp;



    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
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

        //System.out.println(user.getEmail());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        String email = user.getEmail();     //ricavo inforamzioni utente per accedere ai dati sul db
        int iend = email.indexOf("@");
        String user_id = email.substring(0 , iend);   //ID utente
        String us_id = user_id.substring(0, 1).toUpperCase() + user_id.substring(1);


        //System.out.println(path);
        final long ONE_MEGABYTE = 1024*1024;
        storage = FirebaseStorage.getInstance();     //sistema i riferimenti per accedere ai dati del db da riportare nelle info
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        storageReference = storage.getReference("images/"+us_id);

        profilePic = view.findViewById(R.id.profilePict);

        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
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
                for(DataSnapshot utenti : snapshot.getChildren()){
                    //System.out.println(utenti.getKey());
                    if(utenti.getKey().equals(us_id)){
                        u = utenti.getValue(Utente.class);
                        //System.out.println(u.getNome());
                        //tmp = snapshot.getValue(Utente.class);
                        break;
                    }
                }

                //u = snapshot.getChildren(user_id);

                ProfileFragment.this.onItemsObtained(u);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


       // System.out.println(u.getNome());





       // System.out.println(u.getNome());

        textNome = view.findViewById(R.id.casellaNome);
        textCognome = view.findViewById(R.id.casellaCognome);

            //System.out.println(u.getNome());


        //t.setText("Nome : " + u.getNome());


        //profilePic.setImageBitmap();

        //return inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }

    public void onItemsObtained(Utente temp){
        tmp = temp;
        //System.out.println(tmp.getNome());
        textNome.setText("Nome : " + tmp.getNome());
        textCognome.setText("Cognome : " + tmp.getCognome());
    }

}