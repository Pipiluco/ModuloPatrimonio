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
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import br.com.lucasfrancisco.modulopatrimonio.R;
import br.com.lucasfrancisco.modulopatrimonio.models.Empresa;
import br.com.lucasfrancisco.modulopatrimonio.models.Patrimonio;
import br.com.lucasfrancisco.modulopatrimonio.models.Setor;

public class EditPatrimonioActivity extends AppCompatActivity {
    private Spinner spnEmpresa, spnSetor;
    private EditText edtPlaqueta, edtTipo, edtMarca, edtModelo;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private ArrayAdapter adapter;
    private ArrayList<String> listPatrimonios;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_patrimonio);

        getListPatrimonios();

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        Patrimonio patrimonio = (Patrimonio) getIntent().getSerializableExtra("patrimonio");

        setTitle(patrimonio.getPlaqueta());

        spnEmpresa = (Spinner) findViewById(R.id.spnEmpresa);
        spnSetor = (Spinner) findViewById(R.id.spnSetor);
        edtPlaqueta = (EditText) findViewById(R.id.edtPlaqueta);
        edtTipo = (EditText) findViewById(R.id.edtTipo);
        edtMarca = (EditText) findViewById(R.id.edtMarca);
        edtModelo = (EditText) findViewById(R.id.edtModelo);

        desativaEdicao();
        getSpinnerEmpresas();
        getSpinnerSetores();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_opcoes_objeto, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itEditar:
                ativaEdicao();
                return true;
            case R.id.itSalvar:
                atualizar();
                return true;
            case R.id.itExcluir:
                excluir();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void ativaEdicao() {
        spnEmpresa.setEnabled(true);
        spnSetor.setEnabled(true);
        edtPlaqueta.setEnabled(false); // A plaqueta nunca deve ser alterada
        edtTipo.setEnabled(true);
        edtMarca.setEnabled(true);
        edtModelo.setEnabled(true);
    }

    public void desativaEdicao() {
        spnEmpresa.setEnabled(false);
        spnSetor.setEnabled(false);
        edtPlaqueta.setEnabled(false);
        edtTipo.setEnabled(false);
        edtMarca.setEnabled(false);
        edtModelo.setEnabled(false);
    }

    public void atualizar() {
        String empresa = spnEmpresa.getSelectedItem().toString();
        Setor setor = (Setor) spnSetor.getSelectedItem();
        String plaqueta = edtPlaqueta.getText().toString();
        String tipo = edtTipo.getText().toString();
        String marca = edtMarca.getText().toString();
        String modelo = edtModelo.getText().toString();
        boolean isAtivo = true;
        CollectionReference collectionReference = firebaseFirestore.collection("Empresas");
        Patrimonio patrimonio;

        if (tipo.trim().isEmpty() || marca.trim().isEmpty() || modelo.trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.dados_incompletos), Toast.LENGTH_SHORT).show();
        } else {
            patrimonio = new Patrimonio(tipo, marca, modelo, plaqueta, isAtivo, setor);
            collectionReference.document(empresa).collection("Patrimonios").document(plaqueta).set(patrimonio);
            Toast.makeText(getApplicationContext(), getString(R.string.patrimonio_atualizado), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void excluir() {
        String empresa = spnEmpresa.getSelectedItem().toString();
        String documento = edtPlaqueta.getText().toString();
        boolean isPatrimonio = false;
        CollectionReference collectionReference = firebaseFirestore.collection("Empresas").document(empresa).collection("Patrimonios");

        if (listPatrimonios != null) {
            for (int i = 0; i < listPatrimonios.size(); i++) {
                if (documento.equals(listPatrimonios.get(i))) {
                    isPatrimonio = true;
                    collectionReference.document(documento).delete();
                    Toast.makeText(getApplicationContext(), getString(R.string.patrimonio_excluido), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            if (!isPatrimonio) {
                Toast.makeText(getApplicationContext(), getString(R.string.patrimonio_nao_encontrado), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getSpinnerEmpresas() {
        final ArrayList<Empresa> list = new ArrayList<>();
        CollectionReference collectionReference = firebaseFirestore.collection("Empresas");

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Empresa empresa = documentSnapshot.toObject(Empresa.class);
                    list.add(empresa);
                }

                adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, list);
                spnEmpresa.setAdapter(adapter);

                spnEmpresa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        getSpinnerSetores();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });
    }

    public void getSpinnerSetores() {
        final ArrayList<Setor> list = new ArrayList<>();
        CollectionReference collectionReference = firebaseFirestore.collection("Empresas").document(spnEmpresa.getSelectedItem().toString()).collection("Setores");

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Setor setor = documentSnapshot.toObject(Setor.class);
                    list.add(setor);
                }

                adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, list);
                spnSetor.setAdapter(adapter);

                spnSetor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    public void getListPatrimonios() {
        final ArrayList<String> list = new ArrayList<>();
        final CollectionReference collectionReference = firebaseFirestore.collection("Empresas");

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    collectionReference.document(documentSnapshot.getId()).collection("Patrimonios").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                list.add(snapshot.getId());
                            }
                            listPatrimonios = list;
                        }
                    });
                }
            }
        });
    }
}
