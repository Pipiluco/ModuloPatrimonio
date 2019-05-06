package br.com.lucasfrancisco.modulopatrimonio.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import br.com.lucasfrancisco.modulopatrimonio.R;

public class OpcoesNovoConteudoFragment extends Fragment {
    private static final int MENU_ITEM01 = Menu.FIRST;
    private static final int MENU_ITEM02 = Menu.FIRST + 1;
    private static final int MENU_ITEM03 = Menu.FIRST + 2;
    private static final int MENU_ITEM04 = Menu.FIRST + 3;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_opcoes_novo_conteudo, container, false);
        return view;
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        menu.add(0, MENU_ITEM01, Menu.NONE, "Add").setIcon(R.drawable.ic_add).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, MENU_ITEM02, Menu.NONE, "Endereco").setIcon(R.drawable.ic_address_01).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, MENU_ITEM03, Menu.NONE, "Fecha").setIcon(R.drawable.ic_close).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, MENU_ITEM04, Menu.NONE, "Edita").setIcon(R.drawable.ic_edit);

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case MENU_ITEM01:
                break;
            case MENU_ITEM02:
                break;
            case MENU_ITEM03:
                break;
            case MENU_ITEM04:
                break;
        }
        return false;
    }
}
