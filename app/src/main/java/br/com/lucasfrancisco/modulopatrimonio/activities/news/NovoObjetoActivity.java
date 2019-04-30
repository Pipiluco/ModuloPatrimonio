package br.com.lucasfrancisco.modulopatrimonio.activities.news;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import java.util.List;
import java.util.Objects;

import br.com.lucasfrancisco.modulopatrimonio.R;
import br.com.lucasfrancisco.modulopatrimonio.models.Objeto;

public class NovoObjetoActivity extends AppCompatActivity {
    private EditText edtTipo, edtMarca, edtModelo;

    private List<Objeto> objetos;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_objeto);

        getListObjetos();

        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle(getString(R.string.novo_objeto));

        edtTipo = (EditText) findViewById(R.id.edtTipo);
        edtMarca = (EditText) findViewById(R.id.edtMarca);
        edtModelo = (EditText) findViewById(R.id.edtModelo);
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

    private void salvar() {
        String tipo = edtTipo.getText().toString();
        String marca = edtMarca.getText().toString();
        String modelo = edtModelo.getText().toString();
        String documento = tipo + " - " + marca + " " + modelo;
        boolean isObjeto = false;
        CollectionReference collectionReference = firebaseFirestore.collection("Objetos");
        Objeto objeto;

        if (objetos != null) {
            for (int i = 0; i < objetos.size(); i++) {
                String document = objetos.get(i).getTipo() + " - " + objetos.get(i).getMarca() + " " + objetos.get(i).getModelo();
                if (documento.equals(document)) {
                    isObjeto = true;
                    Toast.makeText(getApplicationContext(), getString(R.string.objeto_ja_existe) + " (" + documento + ")", Toast.LENGTH_SHORT).show();
                }
            }

            if (!isObjeto) {
                if (tipo.trim().isEmpty() || marca.trim().isEmpty() || modelo.trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.dados_incompletos), Toast.LENGTH_SHORT).show();
                } else {
                    objeto = new Objeto(tipo, marca, modelo);
                    collectionReference.document(documento).set(objeto);
                    Toast.makeText(getApplicationContext(), getString(R.string.objeto_salvo), Toast.LENGTH_SHORT).show();
                    getListObjetos();
                    isObjeto = true;
                    clearForm();
                }
            }
        } else {
            if (tipo.trim().isEmpty() || marca.trim().isEmpty() || modelo.trim().isEmpty()) {
                Toast.makeText(getApplicationContext(), getString(R.string.dados_incompletos), Toast.LENGTH_SHORT).show();
            } else {
                objeto = new Objeto(tipo, marca, modelo);
                collectionReference.document(documento).set(objeto);
                Toast.makeText(getApplicationContext(), getString(R.string.objeto_salvo), Toast.LENGTH_SHORT).show();
                getListObjetos();
                isObjeto = true;
                clearForm();
            }
        }
    }

    public void getListObjetos() {
        final List<Objeto> list = new ArrayList<>();
        CollectionReference collectionReference = firebaseFirestore.collection("Objetos");

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Objeto objeto = documentSnapshot.toObject(Objeto.class);
                    list.add(objeto);
                }
                objetos = list;
            }
        });
    }

    private void clearForm() {
        edtTipo.setText("");
        edtMarca.setText("");
        edtModelo.setText("");
    }
}
