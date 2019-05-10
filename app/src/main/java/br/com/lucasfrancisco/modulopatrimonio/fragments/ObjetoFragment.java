package br.com.lucasfrancisco.modulopatrimonio.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
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
import br.com.lucasfrancisco.modulopatrimonio.adapters.ObjetoAdapter;
import br.com.lucasfrancisco.modulopatrimonio.fragments.news.NovoObjetoFragment;
import br.com.lucasfrancisco.modulopatrimonio.interfaces.CommunicatePesquisaFragment;
import br.com.lucasfrancisco.modulopatrimonio.interfaces.RCYDocumentSnapshotClickListener;
import br.com.lucasfrancisco.modulopatrimonio.models.Objeto;

public class ObjetoFragment extends Fragment {
    private RecyclerView rcyObjetos;
    private FloatingActionMenu famObjeto;
    private FloatingActionButton fabNovo, fabXls;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = firebaseFirestore.collection("Objetos");
    private FirestoreRecyclerOptions<Objeto> firestoreRecyclerOptions;

    private CommunicatePesquisaFragment communicatePesquisaFragment;

    private ObjetoAdapter objetoAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_objeto, container, false);
        communicatePesquisaFragment.onSetFilter(setListFiltros());

        rcyObjetos = (RecyclerView) view.findViewById(R.id.rcyObjetos);
        famObjeto = (FloatingActionMenu) view.findViewById(R.id.famObjeto);
        fabNovo = (FloatingActionButton) view.findViewById(R.id.fabNovo);
        fabXls = (FloatingActionButton) view.findViewById(R.id.fabXls);

        // Carrrego inicial do rcyEnderecos
        Query query = collectionReference.orderBy("cep").limit(100);
        firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Objeto>().setQuery(query, Objeto.class).build();
        objetoAdapter = new ObjetoAdapter(firestoreRecyclerOptions);

        rcyObjetos.setHasFixedSize(true);
        rcyObjetos.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcyObjetos.setAdapter(objetoAdapter);

        // Métodos para eventos
        getRecyclerViewClickListener();
        getFabNovo();

        return view;
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
        objetoAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        objetoAdapter.stopListening();
    }

    private void getRecyclerViewClickListener() {
        objetoAdapter.setRcyDocumentSnapshotClickListener(new RCYDocumentSnapshotClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int posicao) {
                Toast.makeText(getActivity(), "Normal", Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onItemLongClick(DocumentSnapshot documentSnapshot, int posicao) {
                Toast.makeText(getActivity(), "Longo", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    public void pesquisar(String pesquisa, String filtro, long limite) {
        filtro = filtro.toLowerCase();

        Query query = collectionReference.orderBy(filtro).startAt(pesquisa).endAt(pesquisa + "\uf8ff").limit(limite);
        FirestoreRecyclerOptions<Objeto> options = new FirestoreRecyclerOptions.Builder<Objeto>().setQuery(query, Objeto.class).build();
        objetoAdapter = new ObjetoAdapter(options);

        rcyObjetos.setHasFixedSize(true);
        rcyObjetos.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcyObjetos.setAdapter(objetoAdapter);

        objetoAdapter.startListening();

        // getAdapterItemTouch();
        getRecyclerViewClickListener();
    }

    public void getFabNovo() {
        fabNovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fmlPesquisa, new OpcoesMenuFragment()).commit();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fmlConteudo, new NovoObjetoFragment()).commit();
                MainActivity.fragment = new NovoObjetoFragment();
            }
        });
    }

    private ArrayList<String> setListFiltros() {
        ArrayList<String> listFiltros = new ArrayList<>();
        listFiltros.add("Tipo");
        listFiltros.add("Marca");
        listFiltros.add("Modelo");
        return listFiltros;
    }
}
