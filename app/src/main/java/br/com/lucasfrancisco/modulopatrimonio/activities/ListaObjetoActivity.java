package br.com.lucasfrancisco.modulopatrimonio.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import br.com.lucasfrancisco.modulopatrimonio.R;
import br.com.lucasfrancisco.modulopatrimonio.adapters.ObjetoAdapter;
import br.com.lucasfrancisco.modulopatrimonio.models.Objeto;

public class ListaObjetoActivity extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference objetoCollectionReference = firebaseFirestore.collection("Objetos");

    private ObjetoAdapter objetoAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objeto_lista);

        //
        FloatingActionButton fabAdicionarObjeto = findViewById(R.id.fabAdicionarObjeto);
        fabAdicionarObjeto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListaObjetoActivity.this, ObjetoActivity.class));
            }
        });
        //

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = objetoCollectionReference.orderBy("tipo", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Objeto> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Objeto>().setQuery(query, Objeto.class).build();
        objetoAdapter = new ObjetoAdapter(firestoreRecyclerOptions);

        RecyclerView rcyObjetos = findViewById(R.id.rcyObjetos);
        rcyObjetos.setHasFixedSize(true);
        rcyObjetos.setLayoutManager(new LinearLayoutManager(this));
        rcyObjetos.setAdapter(objetoAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                objetoAdapter.excluir(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(rcyObjetos);

        objetoAdapter.setOnItemClickListener(new ObjetoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int posicao) {
                Objeto objeto = documentSnapshot.toObject(Objeto.class);
                String id = documentSnapshot.getId();
                Toast.makeText(getApplicationContext(), "Posição: " + posicao + " ID: " + id, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        objetoAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        objetoAdapter.stopListening();
    }
}
