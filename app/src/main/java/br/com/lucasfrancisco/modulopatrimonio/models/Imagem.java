package br.com.lucasfrancisco.modulopatrimonio.models;

import android.net.Uri;

import java.io.Serializable;

public class Imagem implements Serializable {
    private String nome;
    private Uri uri;

    public Imagem() {
    }

    public Imagem(String nome, Uri uri) {
        this.nome = nome;
        this.uri = uri;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
