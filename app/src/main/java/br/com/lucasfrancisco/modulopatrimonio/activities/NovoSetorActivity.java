package br.com.lucasfrancisco.modulopatrimonio.activities;

import android.content.Intent;
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
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import br.com.lucasfrancisco.modulopatrimonio.R;
import br.com.lucasfrancisco.modulopatrimonio.models.Empresa;
import br.com.lucasfrancisco.modulopatrimonio.models.Setor;

public class NovoSetorActivity extends AppCompatActivity {
    private Spinner spnEmpresa;
    private EditText edtTipo, edtBloco, edtSala;
    private FloatingActionButton fabNovaEmpresa;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private ArrayAdapter adapter;
    private ArrayList<String> listSetores;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_setor);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle(getString(R.string.novo_setor));

        spnEmpresa = (Spinner) findViewById(R.id.spnEmpresa);
        edtTipo = (EditText) findViewById(R.id.edtTipo);
        edtBloco = (EditText) findViewById(R.id.edtBloco);
        edtSala = (EditText) findViewById(R.id.edtSala);
        fabNovaEmpresa = (FloatingActionButton) findViewById(R.id.fabNovaEmpresa);

        getFabNovaEmpresa();
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
                salvar();
                // Log.i("Empresa", "" + listEmpresas.size());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void salvar() {
        if (spnEmpresa.getSelectedItem() == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.necessario_empresa), Toast.LENGTH_SHORT).show();
            return;
        }

        Empresa empresa = (Empresa) spnEmpresa.getSelectedItem();
        String nomeEmpresa = spnEmpresa.getSelectedItem().toString();
        String tipo = edtTipo.getText().toString();
        String bloco = edtBloco.getText().toString();
        String sala = edtSala.getText().toString();
        Boolean isSetor = false;
        CollectionReference collectionReference = firebaseFirestore.collection("Empresas");
        Setor setor;

        for (int i = 0; i < listSetores.size(); i++) {
            if (listSetores.get(i).equals(bloco + " - " + sala)) {
                isSetor = true;
                Toast.makeText(getApplicationContext(), getString(R.string.setor_ja_existe), Toast.LENGTH_SHORT).show();
            }
        }

        if (!isSetor) {
            if (tipo.trim().isEmpty() || bloco.trim().isEmpty() || sala.trim().isEmpty()) {
                Toast.makeText(getApplicationContext(), getString(R.string.dados_incompletos), Toast.LENGTH_SHORT).show();
            } else {
                setor = new Setor(bloco, tipo, sala, empresa);
                collectionReference.document(nomeEmpresa).collection("Setores").document(bloco + " - " + sala).set(setor);
                Toast.makeText(getApplicationContext(), getString(R.string.setor_salvo), Toast.LENGTH_SHORT).show();
                getListSetores();
                isSetor = true;
                finish();
            }
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
                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
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
                        getListSetores();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });
    }

    public void getListSetores() {
        final ArrayList<String> list = new ArrayList<>();
        CollectionReference collectionReference = firebaseFirestore.collection("Empresas").document(spnEmpresa.getSelectedItem().toString()).collection("Setores");

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    list.add(documentSnapshot.getId());
                }
                listSetores = list;
            }
        });
    }

    public void getFabNovaEmpresa() {
        fabNovaEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NovaEmpresaActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getSpinnerEmpresas();
    }
}
