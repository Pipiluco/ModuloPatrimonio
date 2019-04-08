package br.com.lucasfrancisco.modulopatrimonio.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import br.com.lucasfrancisco.modulopatrimonio.R;
import br.com.lucasfrancisco.modulopatrimonio.models.Empresa;

public class NovoSetorActivity extends AppCompatActivity {
    private Spinner spnEmpresa;
    private EditText edtNome, edtBloco, edtSala;
    private FloatingActionButton fabNovaEmpresa;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private ArrayAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle(getString(R.string.novo_setor));

        spnEmpresa = (Spinner) findViewById(R.id.spnEmpresa);
        edtNome = (EditText) findViewById(R.id.edtNome);
        edtBloco = (EditText) findViewById(R.id.edtBloco);
        edtSala = (EditText) findViewById(R.id.edtSala);
        fabNovaEmpresa = (FloatingActionButton) findViewById(R.id.fabNovaEmpresa);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_salvar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itSalvar:
                // salvar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getSpinnerEmpresas() {
        final ArrayList<Empresa> list = new ArrayList<>();
        final CollectionReference collectionReference = firebaseFirestore.collection("Empresas");

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    collectionReference.document(documentSnapshot.getId()).collection("Sobre").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                                Empresa empresa = snapshot.toObject(Empresa.class);
                                list.add(empresa);
                            }

                            adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, list);
                            spnEmpresa.setAdapter(adapter);
                        }
                    });
                }



                spnEmpresa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getSpinnerEmpresas();
    }
}
