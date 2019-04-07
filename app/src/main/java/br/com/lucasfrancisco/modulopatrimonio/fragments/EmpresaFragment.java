package br.com.lucasfrancisco.modulopatrimonio.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import br.com.lucasfrancisco.modulopatrimonio.R;
import br.com.lucasfrancisco.modulopatrimonio.activities.NovaEmpresaActivity;
import br.com.lucasfrancisco.modulopatrimonio.adapters.EmpresaAdapter;
import br.com.lucasfrancisco.modulopatrimonio.models.Empresa;

public class EmpresaFragment extends Fragment {
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = firebaseFirestore.collection("Empresas");

    private EmpresaAdapter empresaAdapter;

    private FloatingActionButton fabNova, fabXls;
    private RecyclerView rcyEmpresas;

    private ArrayList<String> listEmpresas;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_empresa, container, false);
        Query query = collectionReference.orderBy("codigo", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Empresa> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Empresa>().setQuery(query, Empresa.class).build();
        empresaAdapter = new EmpresaAdapter(firestoreRecyclerOptions);

        rcyEmpresas = (RecyclerView) view.findViewById(R.id.rcyEmpresas);
        rcyEmpresas.setHasFixedSize(true);
        rcyEmpresas.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcyEmpresas.setAdapter(empresaAdapter);

        fabNova = (FloatingActionButton) view.findViewById(R.id.fabNova);
        fabXls = (FloatingActionButton) view.findViewById(R.id.fabXls);

        // Firebase
        getListEmpresas();

        // Métodos para eventos
        getFabXls();
        getFabNova();
        // getAdapterItemTouch();
        getAdapterItemClick();

        return view;
    }


    public void pesquisar(String pesquisa) {
        if (listEmpresas.contains(pesquisa)) {
            Query query = collectionReference.whereEqualTo("codigo", pesquisa);
            FirestoreRecyclerOptions<Empresa> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Empresa>().setQuery(query, Empresa.class).build();
            empresaAdapter = new EmpresaAdapter(firestoreRecyclerOptions);

            rcyEmpresas.setHasFixedSize(true);
            rcyEmpresas.setLayoutManager(new LinearLayoutManager(getActivity()));
            rcyEmpresas.setAdapter(empresaAdapter);

            empresaAdapter.startListening();

            // getAdapterItemTouch();
            getAdapterItemClick();
        } else {
            Toast.makeText(getActivity(), getString(R.string.empresa_nao_encontrada), Toast.LENGTH_SHORT).show();
        }
    }

    public void getListEmpresas() {
        final ArrayList<String> list = new ArrayList<>();
        CollectionReference collectionReference = firebaseFirestore.collection("Empresas");

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    list.add(documentSnapshot.getId());
                }
                listEmpresas = list;
            }
        });
    }

    public void getFabXls() {
        fabXls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Funcionalidade em desenvolvimento", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getFabNova() {
        fabNova.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NovaEmpresaActivity.class);
                startActivity(intent);
            }
        });
    }

    public void getAdapterItemClick() {
        empresaAdapter.setOnItemClickListener(new EmpresaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int posicao) {
                Empresa empresa = documentSnapshot.toObject(Empresa.class);
                Toast.makeText(getActivity(), "Código: " + empresa.getCodigo(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void getAdapterItemTouch() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                empresaAdapter.excluir(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(rcyEmpresas);
    }

    @Override
    public void onStart() {
        super.onStart();
        empresaAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        empresaAdapter.stopListening();
    }

    // Dados entre Fragments
    public void getTextPesquisa(String texto) {
        if (texto != null) {
            pesquisar(texto);
        }
    }
}
