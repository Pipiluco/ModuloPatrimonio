package br.com.lucasfrancisco.modulopatrimonio.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import br.com.lucasfrancisco.modulopatrimonio.R;
import br.com.lucasfrancisco.modulopatrimonio.dao.preferences.SharedPreferencesEmpresa;
import br.com.lucasfrancisco.modulopatrimonio.dao.sqlite.InsertSQLite;
import br.com.lucasfrancisco.modulopatrimonio.fragments.EmpresaFragment;
import br.com.lucasfrancisco.modulopatrimonio.fragments.EnderecoFragment;
import br.com.lucasfrancisco.modulopatrimonio.fragments.PatrimonioFragment;
import br.com.lucasfrancisco.modulopatrimonio.fragments.PesquisaFragment;
import br.com.lucasfrancisco.modulopatrimonio.interfaces.CommunicatePesquisaFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, CommunicatePesquisaFragment {
    private DrawerLayout dwlMain;
    private Toolbar tbMain;
    private NavigationView ngvMain;

    // Fragment
    private Fragment fragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tbMain = (Toolbar) findViewById(R.id.tbMain);
        setSupportActionBar(tbMain);

        dwlMain = (DrawerLayout) findViewById(R.id.dwlMain);

        ngvMain = (NavigationView) findViewById(R.id.ngvMain);
        ngvMain.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, dwlMain, tbMain, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        dwlMain.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (dwlMain.isDrawerOpen(GravityCompat.START)) {
            dwlMain.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferencesEmpresa sharedPreferencesEmpresa = new SharedPreferencesEmpresa();
        sharedPreferencesEmpresa.inserirEmpresas(getApplicationContext());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        setFragment(null);

        switch (menuItem.getItemId()) {
            case R.id.itPatrimonio:
                getSupportFragmentManager().beginTransaction().replace(R.id.fmlPesquisa, new PesquisaFragment()).commit();
                getSupportFragmentManager().beginTransaction().replace(R.id.fmlConteudo, new PatrimonioFragment()).commit();
                setFragment(new PatrimonioFragment());
                break;
            case R.id.itEmpresa:
                getSupportFragmentManager().beginTransaction().replace(R.id.fmlPesquisa, new PesquisaFragment()).commit();
                getSupportFragmentManager().beginTransaction().replace(R.id.fmlConteudo, new EmpresaFragment()).commit();
                setFragment(new EmpresaFragment());
                break;
            case R.id.itEndereco:
                getSupportFragmentManager().beginTransaction().replace(R.id.fmlPesquisa, new PesquisaFragment()).commit();
                getSupportFragmentManager().beginTransaction().replace(R.id.fmlConteudo, new EnderecoFragment()).commit();
                setFragment(new EnderecoFragment());
                break;
            case R.id.itPerfil:
                Intent intent = new Intent(MainActivity.this, NovoObjetoActivity.class);
                startActivity(intent);
                break;
            case R.id.itShare:
                Toast.makeText(getApplicationContext(), "Compartilhar", Toast.LENGTH_SHORT).show();
                break;
            case R.id.itSend:
                Toast.makeText(getApplicationContext(), "Enviar", Toast.LENGTH_SHORT).show();
                break;
        }
        dwlMain.closeDrawer(GravityCompat.START);

        return true;
    }


    // Dados entre Fragments
    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onSetText(String texto, String filtro, long limite) {
        Fragment fragment = getFragment();

        if (fragment instanceof PatrimonioFragment) {
            PatrimonioFragment patrimonioFragment = (PatrimonioFragment) getSupportFragmentManager().findFragmentById(R.id.fmlConteudo);
            patrimonioFragment.pesquisar(texto, filtro, limite);
        } else if (fragment instanceof EmpresaFragment) {
            EmpresaFragment empresaFragment = (EmpresaFragment) getSupportFragmentManager().findFragmentById(R.id.fmlConteudo);
            empresaFragment.getTextPesquisa(texto, filtro);
        } else if (fragment instanceof EnderecoFragment) {
            EnderecoFragment enderecoFragment = (EnderecoFragment) getSupportFragmentManager().findFragmentById(R.id.fmlConteudo);
            enderecoFragment.getTextPesquisa(texto, filtro, limite);
        } else {
            Log.d("FRAGMENT", "Sem opção");
        }
    }

    @Override
    public void onSetFilter(ArrayList arrayList) {
        PesquisaFragment pesquisaFragment = (PesquisaFragment) getSupportFragmentManager().findFragmentById(R.id.fmlPesquisa);
        pesquisaFragment.setFilter(arrayList);
    }
}

/*

 */