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
import java.util.HashMap;
import java.util.Map;

import br.com.lucasfrancisco.modulopatrimonio.R;
import br.com.lucasfrancisco.modulopatrimonio.activities.MainActivity;
import br.com.lucasfrancisco.modulopatrimonio.activities.news.NovoEnderecoActivity;
import br.com.lucasfrancisco.modulopatrimonio.fragments.EmpresaFragment;
import br.com.lucasfrancisco.modulopatrimonio.fragments.PesquisaFragment;
import br.com.lucasfrancisco.modulopatrimonio.interfaces.CommunicateOpcoesMenuFragment;
import br.com.lucasfrancisco.modulopatrimonio.models.Empresa;
import br.com.lucasfrancisco.modulopatrimonio.models.Endereco;
import br.com.lucasfrancisco.modulopatrimonio.models.Usuario;

public class NovaEmpresaFragment extends Fragment {
    private CommunicateOpcoesMenuFragment communicateOpcoesMenuFragment;

    private EditText edtNome, edtFantasia, edtCodigo, edtCNPJ;
    private Spinner spnEndereco;
    private FloatingActionButton fabNovoEndereco;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private ArrayAdapter adapter;
    private ArrayList<String> listEmpresas;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nova_empresa, container, false);
        communicateOpcoesMenuFragment.onSetFragment(setFragment());

        getListEmpresas();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        edtNome = (EditText) view.findViewById(R.id.edtNome);
        edtFantasia = (EditText) view.findViewById(R.id.edtFantasia);
        edtCodigo = (EditText) view.findViewById(R.id.edtCodigo);
        edtCNPJ = (EditText) view.findViewById(R.id.edtCNPJ);
        spnEndereco = (Spinner) view.findViewById(R.id.spnEndereco);
        fabNovoEndereco = (FloatingActionButton) view.findViewById(R.id.fabNovoEndereco);

        //getSpinnerEnderecos();
        getFabNovoEndereco();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            communicateOpcoesMenuFragment = (CommunicateOpcoesMenuFragment) context;
        } catch (Exception e) {
            Log.w("NovaEmpresaFragment", e.toString());
            Toast.makeText(getActivity(), "Erro: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getSpinnerEnderecos();
    }

    public void salvar() {
        Endereco endereco = (Endereco) spnEndereco.getSelectedItem();
        String nome = edtNome.getText().toString();
        String fantasia = edtFantasia.getText().toString();
        String codigo = edtCodigo.getText().toString();
        String cnpj = edtCNPJ.getText().toString();
        String cidade = endereco.getCidade();
        Boolean isEmpresa = false;
        Usuario criador = new Usuario(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail(), firebaseUser.getPhotoUrl().toString(), null, null);
        Date dataCriacao = Timestamp.now().toDate();
        CollectionReference collectionReference = firebaseFirestore.collection("Empresas");
        Empresa empresa;

        for (int i = 0; i < listEmpresas.size(); i++) {
            if (codigo.equals(listEmpresas.get(i))) {
                isEmpresa = true;
                Toast.makeText(getActivity(), getString(R.string.empresa_ja_existe) + " (" + codigo + ")", Toast.LENGTH_SHORT).show();
            }
        }

        if (!isEmpresa) {
            if (nome.trim().isEmpty() || fantasia.trim().isEmpty() || codigo.trim().isEmpty() || cnpj.trim().isEmpty()) {
                Toast.makeText(getActivity(), getString(R.string.dados_incompletos), Toast.LENGTH_SHORT).show();
            } else {
                empresa = new Empresa(criador, null, dataCriacao, null, nome, fantasia, codigo, cnpj, endereco);
                collectionReference.document(codigo + " - " + fantasia + " " + cidade).collection("Sobre").document(codigo + " - " + fantasia + " " + cidade).set(empresa);
                // Seta dados do documento para ativá-lo e facilitar nas buscas
                Map<String, Object> map = new HashMap<>();
                map.put("codigo", codigo);
                map.put("fantasia", fantasia);
                map.put("cnpj", cnpj);
                collectionReference.document(codigo + " - " + fantasia + " " + cidade).set(map);

                Toast.makeText(getActivity(), getString(R.string.empresa_salva), Toast.LENGTH_SHORT).show();
                getListEmpresas();
                isEmpresa = true;
                voltaFragments();
            }
        }
    }

    public void getFabNovoEndereco() {
        fabNovoEndereco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NovoEnderecoActivity.class);
                startActivity(intent);
            }
        });
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

    public void getSpinnerEnderecos() {
        final ArrayList<Endereco> list = new ArrayList<>();
        CollectionReference collectionReference = firebaseFirestore.collection("Enderecos");

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Endereco endereco = documentSnapshot.toObject(Endereco.class);
                    list.add(endereco);
                }

                adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, list);
                spnEndereco.setAdapter(adapter);

                spnEndereco.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });
    }

    public void voltaFragments() {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fmlPesquisa, new PesquisaFragment()).commit();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fmlConteudo, new EmpresaFragment()).commit();
        MainActivity.fragment = new EmpresaFragment();
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
        return "NovaEmpresaFragment";
    }
}
