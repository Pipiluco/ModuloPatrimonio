package br.com.lucasfrancisco.modulopatrimonio.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import br.com.lucasfrancisco.modulopatrimonio.R;
import br.com.lucasfrancisco.modulopatrimonio.models.Endereco;

public class EnderecoActivity extends AppCompatActivity {
    private EditText edtRua, edtNumero, edtCEP, edtBairro, edtCidade, edtEstado, edtPais;
    //
    private static final int MENU_ADD = Menu.FIRST;
    private static final int MENU_LIST = Menu.FIRST + 1;
    private static final int MENU_REFRESH = Menu.FIRST + 2;
    private static final int MENU_LOGIN = Menu.FIRST + 3;
    //

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endereco);

        Endereco endereco = (Endereco) getIntent().getSerializableExtra("endereco");

        setTitle(endereco.getCEP() + " - " + endereco.getRua() + " - " + endereco.getNumero());

        edtRua = (EditText) findViewById(R.id.edtRua);
        edtNumero = (EditText) findViewById(R.id.edtNumero);
        edtCEP = (EditText) findViewById(R.id.edtCEP);
        edtBairro = (EditText) findViewById(R.id.edtBairro);
        edtCidade = (EditText) findViewById(R.id.edtCidade);
        edtEstado = (EditText) findViewById(R.id.edtEstado);
        edtPais = (EditText) findViewById(R.id.edtPais);

        edtBairro.setText(endereco.getBairro());


        edtRua.setEnabled(false);
        edtNumero.setEnabled(false);
        edtCEP.setEnabled(false);
        edtBairro.setEnabled(false);
        edtCidade.setEnabled(false);
        edtEstado.setEnabled(false);
        edtPais.setEnabled(false);


    }

    /*
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
                return true;
            case R.id.itExcluir:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        menu.add(0, MENU_ADD, Menu.NONE, "Add").setIcon(R.drawable.ic_add);
        menu.add(0, MENU_LIST, Menu.NONE, "Endereco").setIcon(R.drawable.ic_address_01);
        menu.add(0, MENU_REFRESH, Menu.NONE, "Fecha").setIcon(R.drawable.ic_close);
        menu.add(0, MENU_LOGIN, Menu.NONE, "Edita").setIcon(R.drawable.ic_edit);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case MENU_ADD:
                break;
            case MENU_LIST:
                break;
            case MENU_REFRESH:
                break;
            case MENU_LOGIN:
                break;
        }
        return false;
    }
}
