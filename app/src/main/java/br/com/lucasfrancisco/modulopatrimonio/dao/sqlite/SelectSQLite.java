package br.com.lucasfrancisco.modulopatrimonio.dao.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class SelectSQLite {
    private BancoController controller;
    private Cursor cursor;

    public void selectEmpresas(Context context) {
        controller = new BancoController(context);
       // cursor = controller.consultarDados();

       // String[] nomeCampos = new String[]{CriaBancoSQLite.ID, CriaBancoSQLite.CODIGO};

        //cursor = controller.qu
    }
}
