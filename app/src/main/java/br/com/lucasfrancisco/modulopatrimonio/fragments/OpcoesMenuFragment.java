package br.com.lucasfrancisco.modulopatrimonio.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Objects;

import br.com.lucasfrancisco.modulopatrimonio.R;
import br.com.lucasfrancisco.modulopatrimonio.interfaces.CommunicateOpcoesMenuFragment;
import br.com.lucasfrancisco.modulopatrimonio.menus.MenusEdit;

public class OpcoesMenuFragment extends Fragment {
    private CommunicateOpcoesMenuFragment communicateOpcoesMenuFragment;
    private String fragment;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setSubtitle("Teste");
        return inflater.inflate(R.layout.fragment_opcoes_menu, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            communicateOpcoesMenuFragment = (CommunicateOpcoesMenuFragment) context;
        } catch (Exception e) {
            Log.w("OpcoesMenuFragment", e.toString());
            Toast.makeText(getActivity(), "Erro: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenusEdit menusEdit = new MenusEdit(getActivity());

        switch (fragment) {
            case "EditPatrimonioFragment":
                super.onPrepareOptionsMenu(menusEdit.menuPrepareOptionsPatrimonio(menu));
                break;
            case "EditEnderecoFragment":
                super.onPrepareOptionsMenu(menusEdit.menuPrepareOptionsEndereco(menu));
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        MenusEdit menusEdit = new MenusEdit(getActivity());

        switch (fragment) {
            case "EditPatrimonioFragment":
                menusEdit.menuSelectedOptionsPatrimonio(communicateOpcoesMenuFragment, item);
                break;
            case "EditEnderecoFragment":
                menusEdit.menuSelectedOptionsEndereco(communicateOpcoesMenuFragment, item);
                break;
        }
        return false;
    }

    public void setFragment(String fragment) {
        this.fragment = fragment;
    }
}
