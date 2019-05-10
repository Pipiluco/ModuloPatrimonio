package br.com.lucasfrancisco.modulopatrimonio.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import br.com.lucasfrancisco.modulopatrimonio.R;
import br.com.lucasfrancisco.modulopatrimonio.interfaces.CommunicateOpcoesMenuFragment;
import br.com.lucasfrancisco.modulopatrimonio.menus.MenusEdit;
import br.com.lucasfrancisco.modulopatrimonio.menus.MenusNew;

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
        MenusNew menusNew = new MenusNew(getActivity());
        MenusEdit menusEdit = new MenusEdit(getActivity());

        switch (fragment) {
            case "EditPatrimonioFragment":
                super.onPrepareOptionsMenu(menusEdit.menuPrepareOptionsPatrimonio(menu));
                break;
            case "EditEnderecoFragment":
                super.onPrepareOptionsMenu(menusEdit.menuPrepareOptionsEndereco(menu));
                break;
            case "NovoPatrimonioFragment":
                super.onPrepareOptionsMenu(menusNew.menuPrepareOptionsPatrimonio(menu));
                break;
            case "NovoEnderecoFragment":
                super.onPrepareOptionsMenu(menusNew.menuPrepareOptionsEndereco(menu));
                break;
            case "NovaEmpresaFragment":
                super.onPrepareOptionsMenu(menusNew.menuPrepareOptionsEmpresa(menu));
                break;
            case "NovoObjetoFragment":
                super.onPrepareOptionsMenu(menusNew.menuPrepareOptionsObjeto(menu));
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        MenusNew menusNew = new MenusNew(getActivity());
        MenusEdit menusEdit = new MenusEdit(getActivity());

        switch (fragment) {
            case "EditPatrimonioFragment":
                menusEdit.menuSelectedOptionsPatrimonio(communicateOpcoesMenuFragment, item);
                break;
            case "EditEnderecoFragment":
                menusEdit.menuSelectedOptionsEndereco(communicateOpcoesMenuFragment, item);
                break;
            case "NovoPatrimonioFragment":
                menusNew.menuSelectedOptionsPatrimonio(communicateOpcoesMenuFragment, item);
                break;
            case "NovoEnderecoFragment":
                menusNew.menuSelectedOptionsEndereco(communicateOpcoesMenuFragment, item);
                break;
            case "NovaEmpresaFragment":
                menusNew.menuSelectedOptionsEmpresa(communicateOpcoesMenuFragment, item);
                break;
            case "NovoObjetoFragment":
                menusNew.menuSelectedOptionsObjeto(communicateOpcoesMenuFragment, item);
                break;
        }
        return false;
    }

    public void setFragment(String fragment) {
        this.fragment = fragment;
    }
}
