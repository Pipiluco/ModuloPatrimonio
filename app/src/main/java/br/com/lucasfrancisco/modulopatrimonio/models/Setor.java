package br.com.lucasfrancisco.modulopatrimonio.models;

public class Setor {
    private String bloco;
    private String tipo;
    private String sala;
    private Empresa empresa;

    public Setor() {
    }

    public Setor(String bloco, String nome, String sala) {
        this.bloco = bloco;
        this.tipo = nome;
        this.sala = sala;
    }

    public Setor(String bloco, String nome, String sala, Empresa empresa) {
        this.bloco = bloco;
        this.tipo = nome;
        this.sala = sala;
        this.empresa = empresa;
    }

    public String getBloco() {
        return bloco;
    }

    public void setBloco(String bloco) {
        this.bloco = bloco;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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

    @Override
    public String toString() {
        return this.bloco + " - " + this.sala;
    }
}
