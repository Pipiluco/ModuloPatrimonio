package br.com.lucasfrancisco.modulopatrimonio.fragments.news;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

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
import br.com.lucasfrancisco.modulopatrimonio.fragments.EnderecoFragment;
import br.com.lucasfrancisco.modulopatrimonio.fragments.PesquisaFragment;
import br.com.lucasfrancisco.modulopatrimonio.interfaces.CommunicateOpcoesMenuFragment;
import br.com.lucasfrancisco.modulopatrimonio.models.Endereco;
import br.com.lucasfrancisco.modulopatrimonio.models.Usuario;

public class NovoEnderecoFragment extends Fragment {
    private CommunicateOpcoesMenuFragment communicateOpcoesMenuFragment;

    private EditText edtRua, edtNumero, edtCEP, edtBairro, edtCidade, edtEstado, edtPais;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private ArrayList<String> listEnderecos;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_novo_endereco, container, false);
        communicateOpcoesMenuFragment.onSetFragment(setFragment());

        getListEnderecos();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        edtRua = (EditText) view.findViewById(R.id.edtRua);
        edtNumero = (EditText) view.findViewById(R.id.edtNumero);
        edtCEP = (EditText) view.findViewById(R.id.edtCEP);
        edtBairro = (EditText) view.findViewById(R.id.edtBairro);
        edtCidade = (EditText) view.findViewById(R.id.edtCidade);
        edtEstado = (EditText) view.findViewById(R.id.edtEstado);
        edtPais = (EditText) view.findViewById(R.id.edtPais);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            communicateOpcoesMenuFragment = (CommunicateOpcoesMenuFragment) context;
        } catch (Exception e) {
            Log.w("NovoEnderecoFragment", e.toString());
            Toast.makeText(getActivity(), "Erro: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }


    public void salvar() {
        String rua = edtRua.getText().toString();
        String numero = edtNumero.getText().toString();
        String cep = edtCEP.getText().toString();
        String bairro = edtBairro.getText().toString();
        String cidade = edtCidade.getText().toString();
        String estado = edtEstado.getText().toString();
        String pais = edtPais.getText().toString();
        String documento = cep + " - " + rua + " - " + numero;
        Usuario criador = new Usuario(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail(), firebaseUser.getPhotoUrl().toString(), null, null);
        Date dataCriacao = Timestamp.now().toDate();
        Boolean isEndereco = false;
        CollectionReference collectionReference = firebaseFirestore.collection("Enderecos");
        Endereco endereco;

        Log.d("Endereços", "" + listEnderecos.size());

        if (listEnderecos != null) {
            for (int i = 0; i < listEnderecos.size(); i++) {
                if (documento.equals(listEnderecos.get(i))) {
                    isEndereco = true;
                    Toast.makeText(getActivity(), getString(R.string.endereco_ja_existe) + " (" + documento + ")", Toast.LENGTH_SHORT).show();
                }
            }

            if (!isEndereco) {
                if (rua.trim().isEmpty() || numero.trim().isEmpty() || cep.trim().isEmpty() || bairro.trim().isEmpty() || cidade.trim().isEmpty() || estado.trim().isEmpty() || pais.trim().isEmpty()) {
                    Toast.makeText(getActivity(), getString(R.string.dados_incompletos), Toast.LENGTH_SHORT).show();
                } else {
                    endereco = new Endereco(criador, null, dataCriacao, null, rua, Integer.parseInt(numero), cep, bairro, cidade, estado, pais);
                    collectionReference.document(documento).set(endereco);
                    Toast.makeText(getActivity(), getString(R.string.endereco_salvo), Toast.LENGTH_SHORT).show();
                    getListEnderecos();
                    isEndereco = true;
                    voltaFragments();
                }
            }
        } else {
            if (rua.trim().isEmpty() || numero.trim().isEmpty() || cep.trim().isEmpty() || bairro.trim().isEmpty() || cidade.trim().isEmpty() || estado.trim().isEmpty() || pais.trim().isEmpty()) {
                Toast.makeText(getActivity(), getString(R.string.dados_incompletos), Toast.LENGTH_SHORT).show();
            } else {
                endereco = new Endereco(criador, null, dataCriacao, null, rua, Integer.parseInt(numero), cep, bairro, cidade, estado, pais);
                collectionReference.document(documento).set(endereco);
                Toast.makeText(getActivity(), getString(R.string.endereco_salvo), Toast.LENGTH_SHORT).show();
                getListEnderecos();
                isEndereco = true;
                voltaFragments();
            }
        }
    }

    public void getListEnderecos() {
        final ArrayList<String> list = new ArrayList<>();
        CollectionReference collectionReference = firebaseFirestore.collection("Enderecos");

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    list.add(documentSnapshot.getId());
                }
                listEnderecos = list;
            }
        });
    }

    public void voltaFragments() {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fmlPesquisa, new PesquisaFragment()).commit();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fmlConteudo, new EnderecoFragment()).commit();
        MainActivity.fragment = new EnderecoFragment();
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
        return "NovoEnderecoFragment";
    }
}
