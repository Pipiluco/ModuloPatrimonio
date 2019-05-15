package br.com.lucasfrancisco.modulopatrimonio.fragments.news;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

import br.com.lucasfrancisco.modulopatrimonio.R;
import br.com.lucasfrancisco.modulopatrimonio.activities.MainActivity;
import br.com.lucasfrancisco.modulopatrimonio.activities.news.NovaEmpresaActivity;
import br.com.lucasfrancisco.modulopatrimonio.fragments.OpcoesMenuFragment;
import br.com.lucasfrancisco.modulopatrimonio.interfaces.CommunicateOpcoesMenuFragment;
import br.com.lucasfrancisco.modulopatrimonio.models.Empresa;
import br.com.lucasfrancisco.modulopatrimonio.models.Setor;
import br.com.lucasfrancisco.modulopatrimonio.models.Usuario;

public class NovoSetorFragment extends Fragment {
    private CommunicateOpcoesMenuFragment communicateOpcoesMenuFragment;

    private Spinner spnEmpresa;
    private EditText edtTipo, edtBloco, edtSala;
    private FloatingActionButton fabNovaEmpresa;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private ArrayAdapter adapter;
    private ArrayList<String> listSetores;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_novo_setor, container, false);
        communicateOpcoesMenuFragment.onSetFragment(setFragment());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        spnEmpresa = (Spinner) view.findViewById(R.id.spnEmpresa);
        edtTipo = (EditText) view.findViewById(R.id.edtTipo);
        edtBloco = (EditText) view.findViewById(R.id.edtBloco);
        edtSala = (EditText) view.findViewById(R.id.edtSala);
        fabNovaEmpresa = (FloatingActionButton) view.findViewById(R.id.fabNovaEmpresa);

        getFabNovaEmpresa();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            communicateOpcoesMenuFragment = (CommunicateOpcoesMenuFragment) context;
        } catch (Exception e) {
            Log.w("NovoSetorFragment", e.toString());
            Toast.makeText(getActivity(), "Erro: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getSpinnerEmpresas();
    }


    public void salvar() {
        if (spnEmpresa.getSelectedItem() == null) {
            Toast.makeText(getActivity(), getString(R.string.necessario_empresa), Toast.LENGTH_SHORT).show();
            return;
        }

        Empresa empresa = (Empresa) spnEmpresa.getSelectedItem();
        String nomeEmpresa = spnEmpresa.getSelectedItem().toString();
        String tipo = edtTipo.getText().toString();
        String bloco = edtBloco.getText().toString();
        String sala = edtSala.getText().toString();
        Boolean isSetor = false;
        Usuario criador = new Usuario(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail(), firebaseUser.getPhotoUrl().toString(), null, null);
        Date dataCriacao = Timestamp.now().toDate();
        CollectionReference collectionReference = firebaseFirestore.collection("Empresas");
        Setor setor;

        for (int i = 0; i < listSetores.size(); i++) {
            if (listSetores.get(i).equals(bloco + " - " + sala)) {
                isSetor = true;
                Toast.makeText(getActivity(), getString(R.string.setor_ja_existe), Toast.LENGTH_SHORT).show();
            }
        }

        if (!isSetor) {
            if (tipo.trim().isEmpty() || bloco.trim().isEmpty() || sala.trim().isEmpty()) {
                Toast.makeText(getActivity(), getString(R.string.dados_incompletos), Toast.LENGTH_SHORT).show();
            } else {
                setor = new Setor(criador, null, dataCriacao, null, bloco, tipo, sala, empresa);
                collectionReference.document(nomeEmpresa).collection("Setores").document(bloco + " - " + sala).set(setor);
                Toast.makeText(getActivity(), getString(R.string.setor_salvo), Toast.LENGTH_SHORT).show();
                getListSetores();
                isSetor = true;
                voltaFragments();
            }
        }
    }

    public void getSpinnerEmpresas() {
        final ArrayList<Empresa> list = new ArrayList<>();
        final CollectionReference collectionReference = firebaseFirestore.collection("Empresas");

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    collectionReference.document(documentSnapshot.getId()).collection("Sobre").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                Empresa empresa = snapshot.toObject(Empresa.class);
                                list.add(empresa);
                            }

                            adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, list);
                            spnEmpresa.setAdapter(adapter);
                        }
                    });
                }

                spnEmpresa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        getListSetores();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });
    }

    public void getListSetores() {
        final ArrayList<String> list = new ArrayList<>();
        CollectionReference collectionReference = firebaseFirestore.collection("Empresas").document(spnEmpresa.getSelectedItem().toString()).collection("Setores");

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    list.add(documentSnapshot.getId());
                }
                listSetores = list;
            }
        });
    }

    public void getFabNovaEmpresa() {
        fabNovaEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NovaEmpresaActivity.class);
                startActivity(intent);
            }
        });
    }

    public void voltaFragments() {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fmlPesquisa, new OpcoesMenuFragment()).commit();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fmlConteudo, new NovoPatrimonioFragment()).commit();
        MainActivity.fragment = new NovoPatrimonioFragment();
    }

    public void setMenuItem(String opcao) {
        Toast.makeText(getActivity(), opcao, Toast.LENGTH_LONG).show();

        switch (opcao) {
            case "Salvar":
                salvar();
                break;
        }
    }

    public String setFragment() {
        return "NovoSetorFragment";
    }
}
