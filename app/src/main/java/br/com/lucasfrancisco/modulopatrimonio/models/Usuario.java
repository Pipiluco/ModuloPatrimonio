package br.com.lucasfrancisco.modulopatrimonio.models;

import java.io.Serializable;

public class Usuario implements Serializable {
    private String id;
    private String nome;
    private String email;
    private String urlFoto;
    private String cargo;
    private String CPF;

    public Usuario() {
    }

    public Usuario(String id, String nome, String email, String urlFoto, String cargo, String CPF) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.urlFoto = urlFoto;
        this.cargo = cargo;
        this.CPF = CPF;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getCPF() {
        return CPF;
    }

    public void setCPF(String CPF) {
        this.CPF = CPF;
    }
}
