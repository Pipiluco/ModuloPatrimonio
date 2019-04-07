package br.com.lucasfrancisco.modulopatrimonio.interfaces;

import com.google.firebase.firestore.DocumentSnapshot;

public interface RecyclerViewClickListener {
    public void onItemClick(DocumentSnapshot documentSnapshot, int posicao);

    public boolean onItemLongClick(DocumentSnapshot documentSnapshot, int posicao);
}
