package br.com.lucasfrancisco.modulopatrimonio.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

public class NovaEmpresaActivity extends AppCompatActivity {
    private EditText edtNome, edtFantasia, edtCodigo, edtCNPJ;
    private Spinner spnEndereco;
    private FloatingActionButton fabNovoEndereco;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private ArrayList<String> listEmpresas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_empresa);

        getListEmpresas();

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle(getString(R.string.nova_empresa));

        edtNome = (EditText) findViewById(R.id.edtNome);
        edtFantasia = (EditText) findViewById(R.id.edtFantasia);
        edtCodigo = (EditText) findViewById(R.id.edtCodigo);
        edtCNPJ = (EditText) findViewById(R.id.edtCNPJ);
        spnEndereco = (Spinner) findViewById(R.id.spnEndereco);
        fabNovoEndereco = (FloatingActionButton) findViewById(R.id.fabNovoEndereco);

        getFabNovoEndereco();
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

    public void salvar() {
        String nome = edtNome.getText().toString();
        String fantasia = edtFantasia.getText().toString();
        String codigo = edtCodigo.getText().toString();
        String cnpj = edtCNPJ.getText().toString();
        Boolean isEmpresa = false;
        CollectionReference collectionReference = firebaseFirestore.collection("Empresas");
        Empresa empresa;

        for (int i = 0; i < listEmpresas.size(); i++) {
            if (codigo.equals(listEmpresas.get(i))) {
                isEmpresa = true;
                Toast.makeText(getApplicationContext(), getString(R.string.empresa_ja_existe) + " (" + codigo + ")", Toast.LENGTH_SHORT).show();
            }
        }

        if (!isEmpresa) {
            if (nome.trim().isEmpty() || fantasia.trim().isEmpty() || codigo.trim().isEmpty() || cnpj.trim().isEmpty()) {
                Toast.makeText(getApplicationContext(), getString(R.string.dados_incompletos), Toast.LENGTH_SHORT).show();
            } else {
                empresa = new Empresa(nome, fantasia, codigo, cnpj);
                collectionReference.document(codigo).set(empresa);
                Toast.makeText(getApplicationContext(), getString(R.string.empresa_salva), Toast.LENGTH_SHORT).show();
                getListEmpresas();
                isEmpresa = true;
            }
        }
    }

    public void getFabNovoEndereco() {
        fabNovoEndereco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NovoEnderecoActivity.class);
                startActivity(intent);
            }
        });
    }

    public void getListEmpresas() {
        final ArrayList<String> list = new ArrayList<>();
        CollectionReference collectionReference = firebaseFirestore.collection("Empresas");

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    list.add(documentSnapshot.getId());
                }
                listEmpresas = list;
            }
        });
    }
}
