package br.com.lucasfrancisco.modulopatrimonio.interfaces;

import android.view.View;

import com.google.firebase.firestore.DocumentSnapshot;

public interface RCYDocumentSnapshotClickListener {
    public void onItemClick(DocumentSnapshot documentSnapshot, int posicao);

    public boolean onItemLongClick(DocumentSnapshot documentSnapshot, int posicao);
}
