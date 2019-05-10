package br.com.lucasfrancisco.modulopatrimonio.menus;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import br.com.lucasfrancisco.modulopatrimonio.R;
import br.com.lucasfrancisco.modulopatrimonio.activities.MainActivity;
import br.com.lucasfrancisco.modulopatrimonio.fragments.EmpresaFragment;
import br.com.lucasfrancisco.modulopatrimonio.fragments.EnderecoFragment;
import br.com.lucasfrancisco.modulopatrimonio.fragments.PatrimonioFragment;
import br.com.lucasfrancisco.modulopatrimonio.fragments.PesquisaFragment;
import br.com.lucasfrancisco.modulopatrimonio.interfaces.CommunicateOpcoesMenuFragment;

public class MenusNew {
    private Context context;

    public MenusNew(Context context) {
        this.context = context;
    }

    public Menu menuPrepareOptionsEndereco(Menu menu) {
        menu.clear();
        menu.add(0, Menu.FIRST, Menu.NONE, context.getString(R.string.voltar)).setIcon(R.drawable.ic_back_04).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, Menu.FIRST + 1, Menu.NONE, context.getString(R.string.salvar)).setIcon(R.drawable.ic_save).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return menu;
    }

    public void menuSelectedOptionsEndereco(CommunicateOpcoesMenuFragment communicateOpcoesMenuFragment, MenuItem menuItem) {
        AppCompatActivity activity = (AppCompatActivity) context;

        switch (menuItem.getItemId()) {
            case 1:
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fmlPesquisa, new PesquisaFragment()).commit();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fmlConteudo, new EnderecoFragment()).commit();
                MainActivity.fragment = new EnderecoFragment();
                communicateOpcoesMenuFragment.onSetMenuItem(context.getString(R.string.voltar));
                break;
            case 2:
                communicateOpcoesMenuFragment.onSetMenuItem(context.getString(R.string.salvar));
                break;
        }
    }


    ////////////////////////////
    public Menu menuPrepareOptionsPatrimonio(Menu menu) {
        menu.clear();
        menu.add(0, Menu.FIRST, Menu.NONE, context.getString(R.string.voltar)).setIcon(R.drawable.ic_back_04).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, Menu.FIRST + 1, Menu.NONE, context.getString(R.string.salvar)).setIcon(R.drawable.ic_save).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return menu;
    }

    public void menuSelectedOptionsPatrimonio(CommunicateOpcoesMenuFragment communicateOpcoesMenuFragment, MenuItem menuItem) {
        AppCompatActivity activity = (AppCompatActivity) context;

        switch (menuItem.getItemId()) {
            case 1:
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fmlPesquisa, new PesquisaFragment()).commit();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fmlConteudo, new PatrimonioFragment()).commit();
                MainActivity.fragment = new PatrimonioFragment();
                communicateOpcoesMenuFragment.onSetMenuItem(context.getString(R.string.voltar));
                break;
            case 2:
                communicateOpcoesMenuFragment.onSetMenuItem(context.getString(R.string.salvar));
                break;
        }
    }

    ////////////////////////////
    public Menu menuPrepareOptionsEmpresa(Menu menu) {
        menu.clear();
        menu.add(0, Menu.FIRST, Menu.NONE, context.getString(R.string.voltar)).setIcon(R.drawable.ic_back_04).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, Menu.FIRST + 1, Menu.NONE, context.getString(R.string.salvar)).setIcon(R.drawable.ic_save).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return menu;
    }

    public void menuSelectedOptionsEmpresa(CommunicateOpcoesMenuFragment communicateOpcoesMenuFragment, MenuItem menuItem) {
        AppCompatActivity activity = (AppCompatActivity) context;

        switch (menuItem.getItemId()) {
            case 1:
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fmlPesquisa, new PesquisaFragment()).commit();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fmlConteudo, new EmpresaFragment()).commit();
                MainActivity.fragment = new EmpresaFragment();
                communicateOpcoesMenuFragment.onSetMenuItem(context.getString(R.string.voltar));
                break;
            case 2:
                communicateOpcoesMenuFragment.onSetMenuItem(context.getString(R.string.salvar));
                break;
        }
    }
}
