package br.com.lucasfrancisco.modulopatrimonio.dao.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPreferencesEmpresa {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private ArrayList<String> empresas;
    private Gson gson;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = firebaseFirestore.collection("Empresas");


    public void inserirEmpresas(final Context context) {
        sharedPreferences = context.getSharedPreferences("Empresas", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        empresas = new ArrayList<>();
        gson = new Gson();

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    empresas.add(documentSnapshot.getId());
                }
                String json = gson.toJson(empresas);
                editor.putString("Set", json);
                editor.commit();
                Toast.makeText(context, "Empresas salvas!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public ArrayList<String> buscarEmpresas(Context context) {
        gson = new Gson();
        sharedPreferences = context.getSharedPreferences("Empresas", Context.MODE_PRIVATE);
        empresas = new ArrayList<>();
        String json = sharedPreferences.getString("Set", "");


        if (json.isEmpty()) {
            Toast.makeText(context, "Não há empresas no arquivo Json!", Toast.LENGTH_LONG).show();
        } else {
            Type type = new TypeToken<List<String>>() {
            }.getType();

            empresas = gson.fromJson(json, type);
            for (String empresa : empresas) {
                Log.d("EMPRESA", empresa);
            }
        }
        return empresas;
    }
}