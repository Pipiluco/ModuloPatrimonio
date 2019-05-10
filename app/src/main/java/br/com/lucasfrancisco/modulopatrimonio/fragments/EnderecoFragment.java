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
import br.com.lucasfrancisco.modulopatrimonio.activities.MainActivity;
import br.com.lucasfrancisco.modulopatrimonio.activities.news.NovoEnderecoActivity;
import br.com.lucasfrancisco.modulopatrimonio.adapters.EnderecoAdapter;
import br.com.lucasfrancisco.modulopatrimonio.fragments.edits.EditEnderecoFragment;
import br.com.lucasfrancisco.modulopatrimonio.fragments.news.NovoEnderecoFragment;
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
                Bundle bundle = new Bundle();
                bundle.putSerializable("endereco", endereco);

                Fragment fragment = new EditEnderecoFragment();
                fragment.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fmlPesquisa, new OpcoesMenuFragment()).commit();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fmlConteudo, fragment).commit();
                MainActivity.fragment = fragment;

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
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fmlPesquisa, new OpcoesMenuFragment()).commit();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fmlConteudo, new NovoEnderecoFragment()).commit();
                MainActivity.fragment = new NovoEnderecoFragment();
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
