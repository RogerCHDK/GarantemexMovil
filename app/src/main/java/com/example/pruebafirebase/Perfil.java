package com.example.pruebafirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pruebafirebase.model.Garantia;
import com.example.pruebafirebase.model.Persona;
import com.example.pruebafirebase.model.Usuario;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Perfil extends AppCompatActivity {

    Button logout;
    TextView nombre,ap_p,ap_m,correo;
    ListView lv_garantia;

    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Usuario personaSelect;
    private List<Garantia> listPersona = new ArrayList<>();
    ArrayAdapter<Garantia> arrayAdapterPersona;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        logout = findViewById(R.id.btn_logout);
        nombre = findViewById(R.id.lbl_nombre);
        ap_p = findViewById(R.id.lbl_ap_p);
        ap_m = findViewById(R.id.lbl_ap_m);
        correo = findViewById(R.id.lbl_correo);
        lv_garantia = findViewById(R.id.lv_datosGarantia);

        inicializarFirebase();



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(Perfil.this,Login.class));
                finish();
            }
        });

        getUserInfo();
        listarDatos();

    }

    private void listarDatos() {


        databaseReference.child("Garantia").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPersona.clear();
                String id= auth.getCurrentUser().getUid();

                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    Garantia g = objSnapshot.getValue(Garantia.class);
                    if (g.getId_usuario().equals(id)){
                    listPersona.add(g);
                    arrayAdapterPersona = new ArrayAdapter<Garantia>(Perfil.this,android.R.layout.simple_list_item_1,listPersona);
                    lv_garantia.setAdapter(arrayAdapterPersona);}
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void getUserInfo() {
        String id= auth.getCurrentUser().getUid();

        databaseReference.child("Users").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String nombreP = dataSnapshot.child("nombre").getValue().toString();
                    String correoP = dataSnapshot.child("correo").getValue().toString();
                    String ap_paternoP = dataSnapshot.child("apellidoPaterno").getValue().toString();
                    String ap_maternoP = dataSnapshot.child("apellidoMaterno").getValue().toString();

                    //nombre.setText(nombreP);
                    ap_p.setText(ap_paternoP);
                    ap_m.setText(ap_maternoP);
                    correo.setText(correoP);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        auth=FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();

    }
}
