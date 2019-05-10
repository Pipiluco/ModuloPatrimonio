package br.com.lucasfrancisco.modulopatrimonio.fragments.news;

import android.content.Context;
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
import java.util.List;
import java.util.Objects;

import br.com.lucasfrancisco.modulopatrimonio.R;
import br.com.lucasfrancisco.modulopatrimonio.interfaces.CommunicateOpcoesMenuFragment;
import br.com.lucasfrancisco.modulopatrimonio.models.Objeto;
import br.com.lucasfrancisco.modulopatrimonio.models.Usuario;

public class NovoObjetoFragment extends Fragment {
    private CommunicateOpcoesMenuFragment communicateOpcoesMenuFragment;

    private EditText edtTipo, edtMarca, edtModelo;

    private List<Objeto> objetos;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_novo_objeto, container, false);
        communicateOpcoesMenuFragment.onSetFragment(setFragment());

        getListObjetos();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        edtTipo = (EditText) view.findViewById(R.id.edtTipo);
        edtMarca = (EditText) view.findViewById(R.id.edtMarca);
        edtModelo = (EditText) view.findViewById(R.id.edtModelo);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            communicateOpcoesMenuFragment = (CommunicateOpcoesMenuFragment) context;
        } catch (Exception e) {
            Log.w("NovoObjetoFragment", e.toString());
            Toast.makeText(getActivity(), "Erro: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void salvar() {
        String tipo = edtTipo.getText().toString();
        String marca = edtMarca.getText().toString();
        String modelo = edtModelo.getText().toString();
        String documento = tipo + " - " + marca + " " + modelo;
        boolean isObjeto = false;
        Usuario criador = new Usuario(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail(), firebaseUser.getPhotoUrl().toString(), null, null);
        Date dataCriacao = Timestamp.now().toDate();
        CollectionReference collectionReference = firebaseFirestore.collection("Objetos");
        Objeto objeto;

        if (objetos != null) {
            for (int i = 0; i < objetos.size(); i++) {
                String document = objetos.get(i).getTipo() + " - " + objetos.get(i).getMarca() + " " + objetos.get(i).getModelo();
                if (documento.equals(document)) {
                    isObjeto = true;
                    Toast.makeText(getActivity(), getString(R.string.objeto_ja_existe) + " (" + documento + ")", Toast.LENGTH_SHORT).show();
                }
            }

            if (!isObjeto) {
                if (tipo.trim().isEmpty() || marca.trim().isEmpty() || modelo.trim().isEmpty()) {
                    Toast.makeText(getActivity(), getString(R.string.dados_incompletos), Toast.LENGTH_SHORT).show();
                } else {
                    objeto = new Objeto(criador, null, dataCriacao, null, tipo, marca, modelo);
                    collectionReference.document(documento).set(objeto);
                    Toast.makeText(getActivity(), getString(R.string.objeto_salvo), Toast.LENGTH_SHORT).show();
                    getListObjetos();
                    isObjeto = true;
                    clearForm();
                }
            }
        } else {
            if (tipo.trim().isEmpty() || marca.trim().isEmpty() || modelo.trim().isEmpty()) {
                Toast.makeText(getActivity(), getString(R.string.dados_incompletos), Toast.LENGTH_SHORT).show();
            } else {
                objeto = new Objeto(criador, null, dataCriacao, null, tipo, marca, modelo);
                collectionReference.document(documento).set(objeto);
                Toast.makeText(getActivity(), getString(R.string.objeto_salvo), Toast.LENGTH_SHORT).show();
                getListObjetos();
                isObjeto = true;
                clearForm();
            }
        }
    }

    public void getListObjetos() {
        final List<Objeto> list = new ArrayList<>();
        CollectionReference collectionReference = firebaseFirestore.collection("Objetos");

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Objeto objeto = documentSnapshot.toObject(Objeto.class);
                    list.add(objeto);
                }
                objetos = list;
            }
        });
    }

    private void clearForm() {
        edtTipo.setText("");
        edtMarca.setText("");
        edtModelo.setText("");
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
        return "NovoObjetoFragment";
    }
}
