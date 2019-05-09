package br.com.lucasfrancisco.modulopatrimonio.fragments.edits;

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
import br.com.lucasfrancisco.modulopatrimonio.interfaces.CommunicateOpcoesMenuFragment;
import br.com.lucasfrancisco.modulopatrimonio.models.Endereco;
import br.com.lucasfrancisco.modulopatrimonio.models.Usuario;

public class EditEnderecoFragment extends Fragment {
    private EditText edtRua, edtNumero, edtCEP, edtBairro, edtCidade, edtEstado, edtPais;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private ArrayList<String> listEnderecos;
    private Endereco endereco;

    private CommunicateOpcoesMenuFragment communicateOpcoesMenuFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_endereco, container, false);
        communicateOpcoesMenuFragment.onSetFragment(setFragment());

        getListEnderecos();

        Bundle bundle = getArguments();
        endereco = (Endereco) bundle.getSerializable("endereco");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        edtRua = (EditText) view.findViewById(R.id.edtRua);
        edtNumero = (EditText) view.findViewById(R.id.edtNumero);
        edtCEP = (EditText) view.findViewById(R.id.edtCEP);
        edtBairro = (EditText) view.findViewById(R.id.edtBairro);
        edtCidade = (EditText) view.findViewById(R.id.edtCidade);
        edtEstado = (EditText) view.findViewById(R.id.edtEstado);
        edtPais = (EditText) view.findViewById(R.id.edtPais);

        edtRua.setText(endereco.getRua());
        edtNumero.setText(String.valueOf(endereco.getNumero()));
        edtCEP.setText(endereco.getCEP());
        edtBairro.setText(endereco.getBairro());
        edtCidade.setText(endereco.getCidade());
        edtEstado.setText(endereco.getEstado());
        edtPais.setText(endereco.getPais());

        desativaEdicao();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            communicateOpcoesMenuFragment = (CommunicateOpcoesMenuFragment) context;
        } catch (Exception e) {
            Log.w("EditEnderecoFragment", e.toString());
            Toast.makeText(getActivity(), "Erro: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void setMenuItem(String opcao) {
        Toast.makeText(getActivity(), opcao, Toast.LENGTH_LONG).show();

        switch (opcao) {
            case "Editar":
                ativaEdicao();
                break;
            case "Salvar":
                atualizar();
                break;
            case "Excluir":
                excluir();
                break;
        }
    }

    public void ativaEdicao() {
        // edtRua.setEnabled(true);
        // edtNumero.setEnabled(true);
        // edtCEP.setEnabled(true);
        edtBairro.setEnabled(true);
        edtCidade.setEnabled(true);
        edtEstado.setEnabled(true);
        edtPais.setEnabled(true);
    }

    public void desativaEdicao() {
        edtRua.setEnabled(false);
        edtNumero.setEnabled(false);
        edtCEP.setEnabled(false);
        edtBairro.setEnabled(false);
        edtCidade.setEnabled(false);
        edtEstado.setEnabled(false);
        edtPais.setEnabled(false);
    }

    public void atualizar() {
        String rua = edtRua.getText().toString();
        String numero = edtNumero.getText().toString();
        String cep = edtCEP.getText().toString();
        String bairro = edtBairro.getText().toString();
        String cidade = edtCidade.getText().toString();
        String estado = edtEstado.getText().toString();
        String pais = edtPais.getText().toString();
        String documento = cep + " - " + rua + " - " + numero;
        Usuario criador = endereco.getCriador();
        Usuario editor = new Usuario(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail(), firebaseUser.getPhotoUrl().toString(), null, null);
        Date dataCriacao = endereco.getDataCriacao();
        Date dataEdicao = Timestamp.now().toDate();
        CollectionReference collectionReference = firebaseFirestore.collection("Enderecos");

        Log.d("Endereços", "" + listEnderecos.size());

        if (rua.trim().isEmpty() || numero.trim().isEmpty() || cep.trim().isEmpty() || bairro.trim().isEmpty() || cidade.trim().isEmpty() || estado.trim().isEmpty() || pais.trim().isEmpty()) {
            Toast.makeText(getActivity(), getString(R.string.dados_incompletos), Toast.LENGTH_SHORT).show();
        } else {
            endereco = new Endereco(criador, editor, dataCriacao, dataEdicao, rua, Integer.parseInt(numero), cep, bairro, cidade, estado, pais);
            collectionReference.document(documento).set(endereco);
            Toast.makeText(getActivity(), getString(R.string.endereco_atualizado), Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }

    public void excluir() {
        String rua = edtRua.getText().toString();
        String numero = edtNumero.getText().toString();
        String cep = edtCEP.getText().toString();
        String documento = cep + " - " + rua + " - " + numero;
        Boolean isEndereco = false;
        CollectionReference collectionReference = firebaseFirestore.collection("Enderecos");

        if (listEnderecos != null) {
            for (int i = 0; i < listEnderecos.size(); i++) {
                if (documento.equals(listEnderecos.get(i))) {
                    isEndereco = true;
                    collectionReference.document(documento).delete();
                    Toast.makeText(getActivity(), getString(R.string.endereco_excluido), Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
            }

            if (!isEndereco) {
                Toast.makeText(getActivity(), getString(R.string.endereco_nao_encontrado), Toast.LENGTH_SHORT).show();
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

    public String setFragment() {
        return "EditEnderecoFragment";
    }
}
