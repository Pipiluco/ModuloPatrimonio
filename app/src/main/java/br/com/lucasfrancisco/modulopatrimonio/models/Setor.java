package br.com.lucasfrancisco.modulopatrimonio.models;

import java.io.Serializable;
import java.util.Date;

public class Setor extends Rastreamento implements Serializable {
    private String bloco;
    private String tipo;
    private String sala;
    private Empresa empresa;

    public Setor() {
    }

    public Setor(Usuario criador, Usuario editor, Date dataCriacao, Date dataEdicao, String bloco, String tipo, String sala, Empresa empresa) {
        super(criador, editor, dataCriacao, dataEdicao);
        this.bloco = bloco;
        this.tipo = tipo;
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
