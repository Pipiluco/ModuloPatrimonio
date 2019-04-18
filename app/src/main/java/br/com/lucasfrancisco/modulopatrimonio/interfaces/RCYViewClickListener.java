package br.com.lucasfrancisco.modulopatrimonio.interfaces;

import android.view.View;

public interface RCYViewClickListener {

    public void onItemClick(View view, int posicao);

    public boolean onItemLongClick(View view, int posicao);
}
