package br.com.lucasfrancisco.modulopatrimonio.activities.edits;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import br.com.lucasfrancisco.modulopatrimonio.R;
import br.com.lucasfrancisco.modulopatrimonio.models.Endereco;

public class EditEnderecoActivity extends AppCompatActivity {
    private EditText edtRua, edtNumero, edtCEP, edtBairro, edtCidade, edtEstado, edtPais;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ArrayList<String> listEnderecos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_endereco);

        getListEnderecos();

        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_close);

        Endereco endereco = (Endereco) getIntent().getSerializableExtra("endereco");

        setTitle(endereco.getCEP() + " - " + endereco.getRua() + " - " + endereco.getNumero());

        edtRua = (EditText) findViewById(R.id.edtRua);
        edtNumero = (EditText) findViewById(R.id.edtNumero);
        edtCEP = (EditText) findViewById(R.id.edtCEP);
        edtBairro = (EditText) findViewById(R.id.edtBairro);
        edtCidade = (EditText) findViewById(R.id.edtCidade);
        edtEstado = (EditText) findViewById(R.id.edtEstado);
        edtPais = (EditText) findViewById(R.id.edtPais);

        edtRua.setText(endereco.getRua());
        edtNumero.setText(String.valueOf(endereco.getNumero()));
        edtCEP.setText(endereco.getCEP());
        edtBairro.setText(endereco.getBairro());
        edtCidade.setText(endereco.getCidade());
        edtEstado.setText(endereco.getEstado());
        edtPais.setText(endereco.getPais());

        desativaEdicao();
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
        // edtRua.setEnabled(true);
        // edtNumero.setEnabled(true);
        // edtCEP.setEnabled(true);
        edtBairro.setEnabled(true);
        edtCidade.setEnabled(true);
        edtEstado.setEnabled(true);
        edtPais.setEnabled(true);
    }

    public void desativaEdicao() {
        edtRua.setEnabled(false);
        edtNumero.setEnabled(false);
        edtCEP.setEnabled(false);
        edtBairro.setEnabled(false);
        edtCidade.setEnabled(false);
        edtEstado.setEnabled(false);
        edtPais.setEnabled(false);
    }

    public void atualizar() {
        String rua = edtRua.getText().toString();
        String numero = edtNumero.getText().toString();
        String cep = edtCEP.getText().toString();
        String bairro = edtBairro.getText().toString();
        String cidade = edtCidade.getText().toString();
        String estado = edtEstado.getText().toString();
        String pais = edtPais.getText().toString();
        String documento = cep + " - " + rua + " - " + numero;
        CollectionReference collectionReference = firebaseFirestore.collection("Enderecos");
        Endereco endereco;

        Log.d("Endereços", "" + listEnderecos.size());

        if (rua.trim().isEmpty() || numero.trim().isEmpty() || cep.trim().isEmpty() || bairro.trim().isEmpty() || cidade.trim().isEmpty() || estado.trim().isEmpty() || pais.trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.dados_incompletos), Toast.LENGTH_SHORT).show();
        } else {
            endereco = new Endereco(rua, Integer.parseInt(numero), cep, bairro, cidade, estado, pais);
            collectionReference.document(documento).set(endereco);
            Toast.makeText(getApplicationContext(), getString(R.string.endereco_atualizado), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void excluir() {
        String rua = edtRua.getText().toString();
        String numero = edtNumero.getText().toString();
        String cep = edtCEP.getText().toString();
        String documento = cep + " - " + rua + " - " + numero;
        Boolean isEndereco = false;
        CollectionReference collectionReference = firebaseFirestore.collection("Enderecos");

        if (listEnderecos != null) {
            for (int i = 0; i < listEnderecos.size(); i++) {
                if (documento.equals(listEnderecos.get(i))) {
                    isEndereco = true;
                    collectionReference.document(documento).delete();
                    Toast.makeText(getApplicationContext(), getString(R.string.endereco_excluido), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            if (!isEndereco) {
                Toast.makeText(getApplicationContext(), getString(R.string.endereco_nao_encontrado), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getListEnderecos() {
        final ArrayList<String> list = new ArrayList<>();
        CollectionReference collectionReference = firebaseFirestore.collection("Enderecos");

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    list.add(documentSnapshot.getId());
                }
                listEnderecos = list;
            }
        });
    }
}
