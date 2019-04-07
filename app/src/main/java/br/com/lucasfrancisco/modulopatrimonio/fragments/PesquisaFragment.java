package br.com.lucasfrancisco.modulopatrimonio.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import br.com.lucasfrancisco.modulopatrimonio.R;
import br.com.lucasfrancisco.modulopatrimonio.interfaces.CommunicatePesquisaFragment;

public class PesquisaFragment extends Fragment {
    private EditText edtPesquisa;
    private ImageView imvPesquisar;
    private CommunicatePesquisaFragment communicatePesquisaFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pesquisa, container, false);
        edtPesquisa = (EditText) view.findViewById(R.id.edtPesquisa);
        imvPesquisar = (ImageView) view.findViewById(R.id.imvPesquisar);

        imvPesquisar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (communicatePesquisaFragment != null) {
                    String texto = edtPesquisa.getText().toString();
                    communicatePesquisaFragment.onSetText(texto);
                    edtPesquisa.setText("");
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            communicatePesquisaFragment = (CommunicatePesquisaFragment) context;
        } catch (Exception e) {
            Log.w("PesquisaFragment", e.toString());
            Toast.makeText(getActivity(), "Erro: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}

