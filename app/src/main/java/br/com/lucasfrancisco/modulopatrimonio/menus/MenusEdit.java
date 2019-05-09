package br.com.lucasfrancisco.modulopatrimonio.menus;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;

import br.com.lucasfrancisco.modulopatrimonio.R;
import br.com.lucasfrancisco.modulopatrimonio.interfaces.CommunicateOpcoesMenuFragment;

public class MenusEdit {
    private Context context;

    public MenusEdit(Context context) {
        this.context = context;
    }

    public Menu menuPrepareOptionsEndereco(Menu menu) {
        menu.clear();
        menu.add(0, Menu.FIRST, Menu.NONE, context.getString(R.string.editar)).setIcon(R.drawable.ic_edit).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, Menu.FIRST + 1, Menu.NONE, context.getString(R.string.salvar)).setIcon(R.drawable.ic_save).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, Menu.FIRST + 2, Menu.NONE, context.getString(R.string.excluir)).setIcon(R.drawable.ic_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return menu;
    }

    public void menuSelectedOptionsEndereco(CommunicateOpcoesMenuFragment communicateOpcoesMenuFragment, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case 1:
                communicateOpcoesMenuFragment.onSetMenuItem(context.getString(R.string.editar));
                break;
            case 2:
                communicateOpcoesMenuFragment.onSetMenuItem(context.getString(R.string.salvar));
                break;
            case 3:
                communicateOpcoesMenuFragment.onSetMenuItem(context.getString(R.string.excluir));
                break;
        }
    }


    ////////////////////////////
    public Menu menuPrepareOptionsPatrimonio(Menu menu) {
        menu.clear();
        menu.add(0, Menu.FIRST, Menu.NONE, context.getString(R.string.editar)).setIcon(R.drawable.ic_edit).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, Menu.FIRST + 1, Menu.NONE, context.getString(R.string.salvar)).setIcon(R.drawable.ic_save).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, Menu.FIRST + 2, Menu.NONE, context.getString(R.string.excluir)).setIcon(R.drawable.ic_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, Menu.FIRST + 3, Menu.NONE, context.getString(R.string.camera)).setIcon(R.drawable.ic_add_photo).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return menu;
    }

    public void menuSelectedOptionsPatrimonio(CommunicateOpcoesMenuFragment communicateOpcoesMenuFragment, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case 1:
                communicateOpcoesMenuFragment.onSetMenuItem(context.getString(R.string.editar));
                break;
            case 2:
                communicateOpcoesMenuFragment.onSetMenuItem(context.getString(R.string.salvar));
                break;
            case 3:
                communicateOpcoesMenuFragment.onSetMenuItem(context.getString(R.string.excluir));
                break;
            case 4:
                communicateOpcoesMenuFragment.onSetMenuItem(context.getString(R.string.camera));
                break;
        }
    }
}
