package com.example.autenticazione_v1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

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
    TextView t;
    Bitmap bmp;
    FirebaseStorage storage;
    StorageReference storageReference;
    Button logout, changeSettings;
    ImageButton back;
    DatabaseReference mDatabase;


    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
        //System.out.println(user.getEmail());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        // Inflate the layout for this fragment
        final long DIM = 2048*2048;
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("disponibilitaVeicolo");

        back = view.findViewById(R.id.tastoBack2);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        changeSettings = view.findViewById(R.id.CambiaImpostazioni);

        changeSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle("Impostazioni veicolo")
                        .setMessage("Seleziona disponibilità auto")
                        .setPositiveButton("Disponibile", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                mDatabase.setValue(true);
                                Toast.makeText(getActivity(), "Modifica effettuata con successo", Toast.LENGTH_SHORT).show();
                                //;
                            }
                        })
                        .setNegativeButton("NON Disponibile", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                mDatabase.setValue(false);
                                Toast.makeText(getActivity(), "Modifica effettuata con successo", Toast.LENGTH_SHORT).show();
                                //;
                            }
                        })

                        .create();

                dialog.show();
            }
        });

        logout = view.findViewById(R.id.LogoutButton);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    //logut utente con ritorno alla schermata login
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getActivity().getApplicationContext(), Login.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        String id = user.getEmail();
        profilePic = getActivity().findViewById(R.id.ProPict);
        t = view.findViewById(R.id.textView3);
        t.setText(id.substring(0, id.indexOf("@")));

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("images/"+user.getUid());

        profilePic = view.findViewById(R.id.ProPict);

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




        //profilePic.setImageBitmap();

        //return inflater.inflate(R.layout.fragment_settings, container, false);
        return view;
    }
}