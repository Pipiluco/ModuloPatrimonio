package br.com.lucasfrancisco.modulopatrimonio.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
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
import java.util.List;

import br.com.lucasfrancisco.modulopatrimonio.R;
import br.com.lucasfrancisco.modulopatrimonio.models.Patrimonio;
import br.com.lucasfrancisco.modulopatrimonio.models.Setor;

public class NovoPatrimonioActivity extends AppCompatActivity {
    private Spinner spnEmpresa, spnSetor;
    private EditText edtPlaqueta, edtTipo, edtMarca, edtModelo;
    private FloatingActionButton fabNovaEmpresa, fabNovoSetor;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private ArrayAdapter adapter;
    private ArrayList<String> listEmpresas;
    //private ArrayList<String> listSetores;
    private ArrayList<String> listPatrimonios;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_patrimonio);

        getListPatrimonios();

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle(getString(R.string.novo_patrimonio));

        spnEmpresa = (Spinner) findViewById(R.id.spnEmpresa);
        spnSetor = (Spinner) findViewById(R.id.spnSetor);
        edtPlaqueta = (EditText) findViewById(R.id.edtPlaqueta);
        edtTipo = (EditText) findViewById(R.id.edtTipo);
        edtMarca = (EditText) findViewById(R.id.edtMarca);
        edtModelo = (EditText) findViewById(R.id.edtModelo);
        fabNovaEmpresa = (FloatingActionButton) findViewById(R.id.fabNovaEmpresa);
        fabNovoSetor = (FloatingActionButton) findViewById(R.id.fabNovoSetor);

        getSpinnerEmpresas();
        getFabNovaEmpresa();
        getFabNovoSetor();
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void salvar() { // Estável OK
        if (spnEmpresa.getSelectedItem() == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.necessario_empresa), Toast.LENGTH_SHORT).show();
            return;
        }

        if (spnSetor.getSelectedItem() == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.necessario_setor), Toast.LENGTH_SHORT).show();
            return;
        }

        Setor setor = (Setor) spnSetor.getSelectedItem();
        String nomeEmpresa = spnEmpresa.getSelectedItem().toString();
        String plaqueta = edtPlaqueta.getText().toString();
        String tipo = edtTipo.getText().toString();
        String marca = edtMarca.getText().toString();
        String modelo = edtModelo.getText().toString();
        Boolean isPatrimonio = false;
        CollectionReference collectionReference = firebaseFirestore.collection("Empresas");
        Patrimonio patrimonio;

        for (int i = 0; i < listPatrimonios.size(); i++) {
            // Log.d("PATRIMÔNIO", "" + listPatrimonios.get(i));
            if (plaqueta.equals(listPatrimonios.get(i))) {
                isPatrimonio = true;
                Toast.makeText(getApplicationContext(), getString(R.string.patrimonio_ja_existe) + " (" + plaqueta + ")", Toast.LENGTH_SHORT).show();
            }
        }

        if (!isPatrimonio) {
            // Log.d("NÃO EXISTE", plaqueta);
            if (plaqueta.trim().isEmpty() || tipo.trim().isEmpty() || marca.trim().isEmpty() || modelo.trim().isEmpty()) {
                Toast.makeText(getApplicationContext(), getString(R.string.dados_incompletos), Toast.LENGTH_SHORT).show();
            } else {
                patrimonio = new Patrimonio(tipo, marca, modelo, plaqueta, true, setor);
                collectionReference.document(nomeEmpresa).collection("Patrimonios").document(plaqueta).set(patrimonio);
                Toast.makeText(getApplicationContext(), getString(R.string.patrimonio_salvo), Toast.LENGTH_SHORT).show();
                getListPatrimonios();
                isPatrimonio = true;

                // Limpa campos de texto
                edtPlaqueta.setText("");
                edtTipo.setText("");
                edtMarca.setText("");
                edtModelo.setText("");
            }
        }
    }

    public List<String> getItensSpinner(Spinner spinner) {
        Adapter adapter = spinner.getAdapter();
        int num = adapter.getCount();
        List<String> list = new ArrayList<String>(num);

        for (int i = 0; i < num; i++) {
            String s = (String) adapter.getItem(i);
            list.add(s);
        }

        return list;
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

    public void getSpinnerEmpresas() {
        final ArrayList<String> list = new ArrayList<>();
        CollectionReference collectionReference = firebaseFirestore.collection("Empresas");

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    list.add(documentSnapshot.getId());
                }

                listEmpresas = list;
                adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, listEmpresas);
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

                //listSetores = list;
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

    public void getFabNovaEmpresa() {
        fabNovaEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NovaEmpresaActivity.class);
                startActivity(intent);
            }
        });
    }

    public void getFabNovoSetor() {
        fabNovoSetor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NovoSetorActivity.class);
                startActivity(intent);
            }
        });
    }
}
