package com.example.autenticazione_v1;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class Registration extends AppCompatActivity {

    private int STORAGE_MEDIA_CODE = 1;  //serve per chiedere permessi utente per accedere alla galleria
    TextInputEditText editTextEmail, editTextPassword, editTextNome, editTextCognome, editTextCellulare;
    Button buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;
    DatabaseReference mDatabase;
    ImageButton insertPhoto;
    com.google.android.material.switchmaterial.SwitchMaterial veicolo;
    Uri foto;
    ActivityResultLauncher<Intent> takePhoto;       //serve per usare la nuova versione di startActivityForResult (deprecata)
    FirebaseStorage storage;
    StorageReference storageReference;

    private static final int PICK_IMAGE = 1;

    @Override
    public void onStart() {
        super.onStart();
        //verifico se utente è loggato in modo da reindirizzarlo alla MainActivity
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Recupero i riferimenti ai campi editText dove l'utente inserisce i propri dati
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        editTextNome = findViewById(R.id.nome);
        editTextCognome = findViewById(R.id.cognome);
        editTextCellulare = findViewById(R.id.telefono);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonReg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        veicolo = findViewById(R.id.switchMacchina);
        insertPhoto = findViewById(R.id.photoButton);
        textView = findViewById(R.id.loginNow);

        takePhoto = registerForActivityResult(              //sostituzione di startActivityForResult
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        Intent data = result.getData();
                        foto = data.getData();

                        //inserisco foto sul db nel momento in cui viene cliccato tasto registrati
                        //poiche se utente decide di cambiare avrò più foto conservata nel db che non servirà a nulla
                    }
                }

        );

        textView.setOnClickListener(new View.OnClickListener() {     //se utente clicca su "Effettua login" lo reindirizzo alla pagina di login
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        insertPhoto.setOnClickListener(new View.OnClickListener() {    //al click dell'image button apre galleria per scelta foto
            @Override
            public void onClick(View view) {

                if(ContextCompat.checkSelfPermission(Registration.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {              //verifico se permessi già accettati o meno

                        ActivityCompat.requestPermissions(Registration.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_MEDIA_CODE);
                }
                else {
                    Intent intent = new Intent();           //setto l'intent per recuperare la foto dalla galleria
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    takePhoto.launch(intent);
                }
            }
        });

        buttonReg.setOnClickListener(new View.OnClickListener() {           //al click di "Registrati controllo se i dati rispettano tutti i requisiti
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                String verifyEmail = email;

                //se l'email non è stata inserita o se non ha il giusto dominio avviso l'utente
                if(TextUtils.isEmpty(email) || !verifyEmail.substring(verifyEmail.indexOf("@")+1).equals("studium.unict.it")){
                    Toast.makeText(Registration.this, "Inserisci email universitaria", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                //se password non valida avviso l'utente
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(Registration.this, "Inserisci password valida (min. 6 caratteri)", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                //se foto non è stata scelta avviso l'utente
                if(foto == null){
                    Toast.makeText(Registration.this, "Inserisci foto", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                //se tutto rispetta i vincoli imposti procedo alla creazione dell'utente tramite metodo messo a disposizione da Firebase Authenticate
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    String uid = mAuth.getCurrentUser().getUid();  //recupero id utente

                                    Utente u = new Utente(editTextNome.getText().toString(), editTextCognome.getText().toString(), editTextCellulare.getText().toString(), editTextEmail.getText().toString(), veicolo.isChecked());

                                    mDatabase.child("users").child(uid).setValue(u);  //inserisco dati utente nel db realtime

                                    StorageReference ref    //processo per inserimento foto sul db
                                            = storageReference
                                            .child(
                                                    "images/" + uid);

                                    ref.putFile(foto)
                                            .addOnSuccessListener(
                                                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(
                                                                UploadTask.TaskSnapshot taskSnapshot) {
                                                        }
                                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                    // Error, Image not uploaded
                                                    Toast.makeText(Registration.this,"Failed " + e.getMessage(),                                                                Toast.LENGTH_SHORT)
                                                            .show();
                                                }
                                            });

                                    //se tutto va a buon fine avviso utente tramite Toast
                                    Toast.makeText(Registration.this, "Account creato. Effettua login.",
                                            Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(Registration.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}