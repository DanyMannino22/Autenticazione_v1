package com.example.autenticazione_v1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.autenticazione_v1.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    ActivityMainBinding binding;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());  //Strumento che permette di associale i layout all'oggetto
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());            //di default apre il fragmento della home

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {   //associa ai bottoni della navigation bar il loro fragment

            switch (item.getItemId()){

                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.profile:
                    replaceFragment(new ProfileFragment());
                    break;
                case R.id.settings:
                    replaceFragment(new SettingsFragment());
                    break;
                case R.id.notifications:
                    replaceFragment(new NotificationFragment());
            }

            return true;
        });

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();  //prende il current user

        if(user == null){     //se user non loggato apre login
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
    }

    private void replaceFragment(Fragment fragment){            //funzione che effettua il cambio del fragment al click
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}