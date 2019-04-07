package br.com.lucasfrancisco.modulopatrimonio.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
import br.com.lucasfrancisco.modulopatrimonio.activities.NovoPatrimonioActivity;
import br.com.lucasfrancisco.modulopatrimonio.adapters.PatrimonioAdapter;
import br.com.lucasfrancisco.modulopatrimonio.models.Patrimonio;

public class PatrimonioFragment extends Fragment {
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = firebaseFirestore.collection("Empresas");

    private PatrimonioAdapter patrimonioAdapter;
    private FirestoreRecyclerOptions<Patrimonio> firestoreRecyclerOptions;

    private RecyclerView rcyPatrimonios;
    private FloatingActionButton fabNovo, fabXls, fabFiltro;

    private ArrayAdapter adapter;
    private String empresaEscolhida = "";
    private String setorEscolhido = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_patrimonio, container, false);

        Query query = collectionReference.document("4082").collection("Patrimonios").orderBy("plaqueta", Query.Direction.ASCENDING);
        firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Patrimonio>().setQuery(query, Patrimonio.class).build();
        patrimonioAdapter = new PatrimonioAdapter(firestoreRecyclerOptions);

        rcyPatrimonios = (RecyclerView) view.findViewById(R.id.rcyPatrimonios);
        rcyPatrimonios.setHasFixedSize(true);
        rcyPatrimonios.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcyPatrimonios.setAdapter(patrimonioAdapter);

        fabNovo = (FloatingActionButton) view.findViewById(R.id.fabNovo);
        fabXls = (FloatingActionButton) view.findViewById(R.id.fabXls);
        fabFiltro = (FloatingActionButton) view.findViewById(R.id.fabFiltro);


        fabNovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NovoPatrimonioActivity.class);
                startActivity(intent);
            }
        });

        fabXls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Exportar para XLS", Toast.LENGTH_SHORT).show();
            }
        });

        getFabFiltro();
        getAdapterItemTouch();
        getAdapterItemClick();

        return view;
    }

    public void getFabFiltro() {
        fabFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View viewDialog = getLayoutInflater().inflate(R.layout.dialog_filtro_patrimonio, null);
                Spinner spnEmpresa = (Spinner) viewDialog.findViewById(R.id.spnEmpresa);
                Spinner spnSetor = (Spinner) viewDialog.findViewById(R.id.spnSetor);
                Button btnFiltrar = (Button) viewDialog.findViewById(R.id.btnFiltrar);

                spinnerEmpresas(spnEmpresa, spnSetor);
                btnFiltrar(btnFiltrar);

                builder.setView(viewDialog);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    public void spinnerEmpresas(final Spinner spnEmpresa, final Spinner spnSetor) {
        final ArrayList<String> list = new ArrayList<>();
        CollectionReference collectionReference = firebaseFirestore.collection("Empresas");

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    list.add(documentSnapshot.getId());
                }

                adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, list);
                spnEmpresa.setAdapter(adapter);

                spnEmpresa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        empresaEscolhida = spnEmpresa.getSelectedItem().toString();
                        spinnerSetores(spnSetor);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });
    }

    public void spinnerSetores(final Spinner spnSetor) {
        final ArrayList<String> list = new ArrayList<>();
        CollectionReference collectionReference = firebaseFirestore.collection("Empresas").document(empresaEscolhida).collection("Setores");

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    list.add(documentSnapshot.getId());
                }

                adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, list);
                spnSetor.setAdapter(adapter);

                spnSetor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        setorEscolhido = spnSetor.getSelectedItem().toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });
    }

    public void btnFiltrar(Button btnFiltrar) {
        btnFiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query query = collectionReference.document(empresaEscolhida).collection("Patrimonios").orderBy("plaqueta", Query.Direction.ASCENDING);
                firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Patrimonio>().setQuery(query, Patrimonio.class).build();
                patrimonioAdapter = new PatrimonioAdapter(firestoreRecyclerOptions);

                rcyPatrimonios.setHasFixedSize(true);
                rcyPatrimonios.setLayoutManager(new LinearLayoutManager(getActivity()));
                rcyPatrimonios.setAdapter(patrimonioAdapter);

                patrimonioAdapter.startListening();

                getAdapterItemTouch();
                getAdapterItemClick();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        patrimonioAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        patrimonioAdapter.stopListening();
    }


    public void getAdapterItemClick() {
        patrimonioAdapter.setOnItemClickListener(new PatrimonioAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int posicao) {
                Patrimonio patrimonio = documentSnapshot.toObject(Patrimonio.class);
                Toast.makeText(getActivity(), "Marca: " + patrimonio.getMarca(), Toast.LENGTH_SHORT).show();
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
                patrimonioAdapter.excluir(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(rcyPatrimonios);
    }

    public void pesquisar(String s) {
        if (!empresaEscolhida.equals("")) {
            Log.d("Empresa", empresaEscolhida);
            Query query = collectionReference.document(empresaEscolhida).collection("Patrimonios").whereEqualTo("plaqueta", s);
            FirestoreRecyclerOptions<Patrimonio> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Patrimonio>().setQuery(query, Patrimonio.class).build();
            patrimonioAdapter = new PatrimonioAdapter(firestoreRecyclerOptions);

            rcyPatrimonios.setHasFixedSize(true);
            rcyPatrimonios.setLayoutManager(new LinearLayoutManager(getActivity()));
            rcyPatrimonios.setAdapter(patrimonioAdapter);

            patrimonioAdapter.startListening();

            getAdapterItemTouch();
            getAdapterItemClick();
        } else {
            Toast.makeText(getActivity(), "Nenhuma empresa escolhida!", Toast.LENGTH_SHORT).show();
        }
    }


    // Dados entre Fragments
    public void getTextPesquisa(String texto) {
        if (texto != null) {
            pesquisar(texto);
        }
    }
}


