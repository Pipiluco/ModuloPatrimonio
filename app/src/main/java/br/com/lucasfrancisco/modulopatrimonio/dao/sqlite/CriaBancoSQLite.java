package br.com.lucasfrancisco.modulopatrimonio.dao.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CriaBancoSQLite extends SQLiteOpenHelper {
    private static final String NOME_BANCO = "modulo_patrimonio.db";
    public static final String TABELA = "empresas";
    public static final String ID = "id";
    public static final String CODIGO = "codigo";
    public static final String FANTASIA = "fantasia";
    public static final String CIDADE = "cidade";
    public static final int VERSAO = 1;

    public CriaBancoSQLite(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABELA + "("
                + ID + " integer primary key autoincrement,"
                + CODIGO + " text,"
                + FANTASIA + " text,"
                + CIDADE + " text"
                + ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA);
        onCreate(db);
    }
}
