package br.com.lucasfrancisco.modulopatrimonio.dao.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class BancoController {

    private SQLiteDatabase db;
    private CriaBancoSQLite banco;

    public BancoController(Context context) {
        banco = new CriaBancoSQLite(context);
    }

    public String inserirDados(String codigo, String fantasia, String cidade) {
        ContentValues valores;
        long resultado;

        db = banco.getWritableDatabase();
        valores = new ContentValues();
        valores.put(CriaBancoSQLite.CODIGO, codigo);
        valores.put(CriaBancoSQLite.FANTASIA, fantasia);
        valores.put(CriaBancoSQLite.CIDADE, cidade);

        resultado = db.insert(CriaBancoSQLite.TABELA, null, valores);
        db.close();

        if (resultado == -1)
            return "Erro ao inserir registro";
        else
            return "Registro Inserido com sucesso";
    }

    public List<String> consultarDados() {
        List<String> empresas = new ArrayList<>();
        Cursor cursor;
        String[] colunas = {banco.ID, banco.CODIGO};
        db = banco.getReadableDatabase();
        cursor = db.query(banco.TABELA, colunas, null, null, null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                String empresa;
                empresa = cursor.getString(1);
                empresas.add(empresa);
            }while (cursor.moveToNext());
        }

        db.close();
        return empresas;
    }
}


/*
public class BancoController {

    private SQLiteDatabase db;
    private CriaBancoSQLite banco;

    public BancoController(Context context) {
        banco = new CriaBancoSQLite(context);
    }

    public String inserirDados(String codigo, String fantasia, String cidade) {
        ContentValues valores;
        long resultado;

        db = banco.getWritableDatabase();
        valores = new ContentValues();
        valores.put(CriaBancoSQLite.CODIGO, codigo);
        valores.put(CriaBancoSQLite.FANTASIA, fantasia);
        valores.put(CriaBancoSQLite.CIDADE, cidade);

        resultado = db.insert(CriaBancoSQLite.TABELA, null, valores);
        db.close();

        if (resultado == -1)
            return "Erro ao inserir registro";
        else
            return "Registro Inserido com sucesso";
    }

    public Cursor consultarDados() {
        Cursor cursor;
        String[] campos = {banco.ID, banco.CODIGO};
        db = banco.getReadableDatabase();
        cursor = db.query(banco.TABELA, campos, null, null, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        db.close();
        return cursor;
    }
}
 */