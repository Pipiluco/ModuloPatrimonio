package br.com.lucasfrancisco.modulopatrimonio.models;

import java.io.Serializable;

public class Imagem implements Serializable {
    private String nome;
    private String urlLocal;
    private String urlRemota;
    private boolean isEnviada;

    public Imagem() {
    }

    public Imagem(String nome, String urlLocal, String urlRemota, boolean isEnviada) {
        this.nome = nome;
        this.urlLocal = urlLocal;
        this.urlRemota = urlRemota;
        this.isEnviada = isEnviada;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUrlLocal() {
        return urlLocal;
    }

    public void setUrlLocal(String urlLocal) {
        this.urlLocal = urlLocal;
    }

    public String getUrlRemota() {
        return urlRemota;
    }

    public void setUrlRemota(String urlRemota) {
        this.urlRemota = urlRemota;
    }

    public boolean isEnviada() {
        return isEnviada;
    }

    public void setEnviada(boolean enviada) {
        isEnviada = enviada;
    }
}
