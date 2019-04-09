package br.com.lucasfrancisco.modulopatrimonio.models;

public class Setor {
    private String bloco;
    private String nome;
    private String sala;
    private Empresa empresa;

    public Setor() {
    }

    public Setor(String bloco, String nome, String sala) {
        this.bloco = bloco;
        this.nome = nome;
        this.sala = sala;
    }

    public Setor(String bloco, String nome, String sala, Empresa empresa) {
        this.bloco = bloco;
        this.nome = nome;
        this.sala = sala;
        this.empresa = empresa;
    }

    public String getBloco() {
        return bloco;
    }

    public void setBloco(String bloco) {
        this.bloco = bloco;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSala() {
        return sala;
    }

    public void setSala(String sala) {
        this.sala = sala;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }
}
