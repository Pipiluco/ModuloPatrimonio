package br.com.lucasfrancisco.modulopatrimonio.models;

import java.io.Serializable;

public class Imagem implements Serializable {
    private String nome;
    private String urlImagem;

    public Imagem() {
    }

    public Imagem(String nome, String urlImagem) {
        this.nome = nome;
        this.urlImagem = urlImagem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }
}
