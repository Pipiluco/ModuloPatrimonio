package br.com.lucasfrancisco.modulopatrimonio.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import br.com.lucasfrancisco.modulopatrimonio.R;
import br.com.lucasfrancisco.modulopatrimonio.models.Objeto;
import yuku.ambilwarna.AmbilWarnaDialog;

public class ObjetoActivity extends AppCompatActivity {
    private EditText edtTipo, edtMarca, edtModelo, edtCor;
    private Button btnSalvar, btnCor;

    private int corDefault;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference objetosCollectionReference = firebaseFirestore.collection("Objetos");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objeto);

        edtTipo = (EditText) findViewById(R.id.edtTipo);
        edtMarca = (EditText) findViewById(R.id.edtMarca);
        edtModelo = (EditText) findViewById(R.id.edtModelo);
        edtCor = (EditText) findViewById(R.id.edtCor);
        btnSalvar = (Button) findViewById(R.id.btnSalvar);
        btnCor = (Button) findViewById(R.id.btnCor);
    }

    public void salvarObjeto(View view) {
        String tipo = edtTipo.getText().toString();
        String marca = edtMarca.getText().toString();
        String modelo = edtModelo.getText().toString();
        String cor = edtCor.getText().toString();
        Objeto objeto = new Objeto(tipo, marca, modelo);

        objetosCollectionReference.add(objeto);
    }


    public void abrirColorPicker(View view) {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, corDefault, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                corDefault = color;
                String cor = String.valueOf(color);
                edtCor.setText(cor);
                edtCor.setBackgroundColor(corDefault);
            }
        });
        colorPicker.show();
    }
}
