package br.com.lucasfrancisco.modulopatrimonio.models;

import java.io.Serializable;

public class Empresa implements Serializable {
    private String nome;
    private String fantasia;
    private String codigo;
    private String CNPJ;
    private Endereco endereco;

    public Empresa() {
    }

    public Empresa(String nome, String fantasia, String codigo, String CNPJ) {
        this.nome = nome;
        this.fantasia = fantasia;
        this.codigo = codigo;
        this.CNPJ = CNPJ;
    }

    public Empresa(String nome, String fantasia, String codigo, String CNPJ, Endereco endereco) {
        this.nome = nome;
        this.fantasia = fantasia;
        this.codigo = codigo;
        this.CNPJ = CNPJ;
        this.endereco = endereco;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFantasia() {
        return fantasia;
    }

    public void setFantasia(String fantasia) {
        this.fantasia = fantasia;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCNPJ() {
        return CNPJ;
    }

    public void setCNPJ(String CNPJ) {
        this.CNPJ = CNPJ;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    @Override
    public String toString() {
        return this.codigo + " - " + this.fantasia + " " + this.endereco.getCidade();
    }
}
