package br.com.lucasfrancisco.modulopatrimonio.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import br.com.lucasfrancisco.modulopatrimonio.R;
import br.com.lucasfrancisco.modulopatrimonio.interfaces.CommunicatePesquisaFragment;

public class PesquisaFragment extends Fragment implements SearchView.OnQueryTextListener {
    private CommunicatePesquisaFragment communicatePesquisaFragment;
    private ArrayList<String> listFilter;
    private SearchView shvPesquisa;
    private NumberPicker npLimite;
    private Spinner spnFiltro;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pesquisa, container, false);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_pesquisa, menu);

        MenuItem itPesquisa = menu.findItem(R.id.itPesquisa);
        MenuItem itFiltro = menu.findItem(R.id.itFiltro);
        MenuItem itLimite = menu.findItem(R.id.itLimite);

        shvPesquisa = (SearchView) itPesquisa.getActionView();
        shvPesquisa.setOnQueryTextListener(this);
        shvPesquisa.setQueryHint(getString(R.string.pesquisa));

        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, listFilter);
        spnFiltro = (Spinner) itFiltro.getActionView();
        spnFiltro.setAdapter(adapter);

        npLimite = (NumberPicker) itLimite.getActionView();
        npLimite.setMinValue(1);
        npLimite.setMaxValue(100);
        npLimite.setValue(10); // Valor padrão

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText != null || !newText.trim().isEmpty() || communicatePesquisaFragment != null) {
            String filter = spnFiltro.getSelectedItem().toString();
            int limit = npLimite.getValue();
            communicatePesquisaFragment.onSetText(newText, filter, limit);
        }
        return false;
    }

    public void setFilter(ArrayList<String> arrayList) {
        listFilter = arrayList;
    }
}
