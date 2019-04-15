package br.com.lucasfrancisco.modulopatrimonio.interfaces;

import java.util.ArrayList;

public interface CommunicatePesquisaFragment {

    public void onSetText(String texto, String filtro, long limite);

    public void onSetFilter(ArrayList arrayList);
}
