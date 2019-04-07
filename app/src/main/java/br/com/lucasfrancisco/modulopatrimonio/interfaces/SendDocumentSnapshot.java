package br.com.lucasfrancisco.modulopatrimonio.interfaces;

import com.google.firebase.firestore.DocumentSnapshot;

public interface SendDocumentSnapshot {
    public DocumentSnapshot onDocument(DocumentSnapshot documentSnapshot);

}
