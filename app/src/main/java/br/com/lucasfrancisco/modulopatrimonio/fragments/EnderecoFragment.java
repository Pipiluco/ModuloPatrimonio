package br.com.lucasfrancisco.modulopatrimonio.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

import br.com.lucasfrancisco.modulopatrimonio.R;
import br.com.lucasfrancisco.modulopatrimonio.activities.EditEnderecoActivity;
import br.com.lucasfrancisco.modulopatrimonio.activities.NovoEnderecoActivity;
import br.com.lucasfrancisco.modulopatrimonio.adapters.EnderecoAdapter;
import br.com.lucasfrancisco.modulopatrimonio.interfaces.CommunicatePesquisaFragment;
import br.com.lucasfrancisco.modulopatrimonio.interfaces.RCYDocumentSnapshotClickListener;
import br.com.lucasfrancisco.modulopatrimonio.models.Endereco;

public class EnderecoFragment extends Fragment {
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = firebaseFirestore.collection("Enderecos");
    private Query query;
    private FirestoreRecyclerOptions<Endereco> firestoreRecyclerOptions;

    private CommunicatePesquisaFragment communicatePesquisaFragment;

    private EnderecoAdapter enderecoAdapter;

    private FloatingActionMenu famEndereco;
    private FloatingActionButton fabNovo, fabXls;
    private RecyclerView rcyEnderecos;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_endereco, container, false);
        communicatePesquisaFragment.onSetFilter(setListFiltros());

        rcyEnderecos = (RecyclerView) view.findViewById(R.id.rcyEnderecos);
        famEndereco = (FloatingActionMenu) view.findViewById(R.id.famEndereco);
        fabNovo = (FloatingActionButton) view.findViewById(R.id.fabNovo);
        fabXls = (FloatingActionButton) view.findViewById(R.id.fabXls);

        // Carrrego inicial do rcyEnderecos
        query = collectionReference.orderBy("cep").limit(100);
        firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Endereco>().setQuery(query, Endereco.class).build();
        enderecoAdapter = new EnderecoAdapter(firestoreRecyclerOptions);

        rcyEnderecos.setHasFixedSize(true);
        rcyEnderecos.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcyEnderecos.setAdapter(enderecoAdapter);

        // Métodos para eventos
        getFabXls();
        getFabNovo();
        getRecyclerViewClickListener();

        return view;
    }

    private void getRcyEnderecos() {
        rcyEnderecos.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                closeFloatingActionMenu();
                return true;
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            communicatePesquisaFragment = (CommunicatePesquisaFragment) context;
        } catch (Exception e) {
            Log.w("EnderecoFragment", e.toString());
            Toast.makeText(getActivity(), "Erro: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        enderecoAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        enderecoAdapter.stopListening();
    }

    public void closeFloatingActionMenu() {
        if (famEndereco.isOpened()) {
            famEndereco.close(true);
        }
    }


    public void pesquisar(String pesquisa, String filtro, long limite) {
        if (!pesquisa.equals("")) {
            pesquisa = pesquisa.substring(0, 1).toUpperCase().concat(pesquisa.substring(1));
        }
        filtro = filtro.toLowerCase();

        Query queryLocal = collectionReference.orderBy(filtro).startAt(pesquisa).endAt(pesquisa + "\uf8ff").limit(limite);
        FirestoreRecyclerOptions<Endereco> options = new FirestoreRecyclerOptions.Builder<Endereco>().setQuery(queryLocal, Endereco.class).build();
        enderecoAdapter = new EnderecoAdapter(options);

        rcyEnderecos.setHasFixedSize(true);
        rcyEnderecos.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcyEnderecos.setAdapter(enderecoAdapter);

        enderecoAdapter.startListening();

        // getAdapterItemTouch();
        getRecyclerViewClickListener();
    }


    public void getRecyclerViewClickListener() {
        enderecoAdapter.setRCYDocumentSnapshotClickListener(new RCYDocumentSnapshotClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int posicao) {
                Endereco endereco = documentSnapshot.toObject(Endereco.class);
                Intent intent = new Intent(getActivity(), EditEnderecoActivity.class);
                intent.putExtra("endereco", endereco);
                startActivity(intent);

                closeFloatingActionMenu();
            }

            @Override
            public boolean onItemLongClick(DocumentSnapshot documentSnapshot, int posicao) {
                Toast.makeText(getActivity(), "Longo" + documentSnapshot.getId(), Toast.LENGTH_SHORT).show();
                closeFloatingActionMenu();
                return true;
            }
        });
    }

    public void getFabXls() {
        fabXls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Funcionalidade em desenvolvimento", Toast.LENGTH_SHORT).show();
                closeFloatingActionMenu();
            }
        });
    }

    public void getFabNovo() {
        fabNovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFloatingActionMenu();
                Intent intent = new Intent(getActivity(), NovoEnderecoActivity.class);
                startActivity(intent);
            }
        });
    }

    public ArrayList<String> setListFiltros() {
        ArrayList<String> listFiltros = new ArrayList<>();
        listFiltros.add("CEP");
        listFiltros.add("Rua");
        listFiltros.add("Bairro");
        listFiltros.add("Cidade");
        listFiltros.add("Estado");

        return listFiltros;
    }
}



/*
  // Teste com paginação
    public void pesquisarTeste(String pesquisa, String filtro) {
        Query query = collectionReference.orderBy("cep", Query.Direction.ASCENDING).limit(10);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                final List<Endereco> enderecos = new ArrayList<>();
                rcyEnderecos.setLayoutManager(new LinearLayoutManager(getActivity()));
                rcyEnderecos.setHasFixedSize(true);

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Endereco endereco = documentSnapshot.toObject(Endereco.class);
                    enderecos.add(endereco);
                }
                adapter = new br.com.lucasfrancisco.modulopatrimonio.testes.EnderecoAdapter(enderecos);
                rcyEnderecos.setAdapter(adapter);

                ultimoResultado = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                Toast.makeText(getActivity(), "First page", Toast.LENGTH_SHORT).show();

                RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                            isSrolling = true;
                        }
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());

                        int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                        int visibleItemCount = linearLayoutManager.getChildCount();
                        int totalItemCount = linearLayoutManager.getItemCount();

                        if (isSrolling && (firstVisibleItem + visibleItemCount == totalItemCount) && !isLastItem) {
                            isSrolling = false;
                            Query nextQuery = collectionReference.orderBy("cep", Query.Direction.ASCENDING).startAfter(ultimoResultado).limit(10);

                            nextQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        Endereco endereco = documentSnapshot.toObject(Endereco.class);
                                        enderecos.add(endereco);
                                    }
                                    adapter.notifyDataSetChanged();

                                    ultimoResultado = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                                    Toast.makeText(getActivity(), "Next page", Toast.LENGTH_SHORT).show();

                                    if (queryDocumentSnapshots.size() < 10) {
                                        isLastItem = true;
                                    }
                                }
                            });
                        }
                    }
                };
                rcyEnderecos.addOnScrollListener(onScrollListener);
                adapter.notifyDataSetChanged();
            }
        });
    }
  * */