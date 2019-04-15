package br.com.lucasfrancisco.modulopatrimonio.dao.sqlite;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import br.com.lucasfrancisco.modulopatrimonio.models.Empresa;

public class InsertSQLite {
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = firebaseFirestore.collection("Empresas");
    private BancoController controller;

    public void inserirEmpresas(final Context context) {
        controller = new BancoController(context);

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    collectionReference.document(documentSnapshot.getId()).collection("Sobre").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                                Empresa empresa = snapshot.toObject(Empresa.class);
                                controller.inserirDados(empresa.getCodigo(), empresa.getFantasia(), empresa.getEndereco().getCidade());
                            }
                            Toast.makeText(context, "Impresas inseridas!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
