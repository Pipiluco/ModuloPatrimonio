package br.com.lucasfrancisco.modulopatrimonio.models;

import android.net.Uri;

import java.io.Serializable;

public class Imagem implements Serializable {
    private String nome;
    private Uri uri;
    private boolean isEnviada;

    public Imagem() {
    }

    public Imagem(String nome, Uri uri, boolean isEnviada) {
        this.nome = nome;
        this.uri = uri;
        this.isEnviada = isEnviada;
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

    public boolean isEnviada() {
        return isEnviada;
    }

    public void setEnviada(boolean enviada) {
        isEnviada = enviada;
    }
}
