package br.com.lucasfrancisco.modulopatrimonio.interfaces;

import java.util.ArrayList;

public interface CommunicatePesquisaFragment {
    //public void onSetText(String texto);
    public void onSetText(String texto, String filtro);

    public void onSetFilter(ArrayList arrayList);
}
