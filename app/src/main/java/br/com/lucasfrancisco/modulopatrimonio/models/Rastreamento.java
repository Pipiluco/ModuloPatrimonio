package br.com.lucasfrancisco.modulopatrimonio.models;

import java.io.Serializable;
import java.util.Date;

public class Rastreamento implements Serializable {
    private Usuario criador;
    private Usuario editor;
    private Date dataCriacao;
    private Date dataEdicao;

    public Rastreamento() {
    }

    public Rastreamento(Usuario criador, Usuario editor, Date dataCriacao, Date dataEdicao) {
        this.criador = criador;
        this.editor = editor;
        this.dataCriacao = dataCriacao;
        this.dataEdicao = dataEdicao;
    }

    public Usuario getCriador() {
        return criador;
    }

    public void setCriador(Usuario criador) {
        this.criador = criador;
    }

    public Usuario getEditor() {
        return editor;
    }

    public void setEditor(Usuario editor) {
        this.editor = editor;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Date getDataEdicao() {
        return dataEdicao;
    }

    public void setDataEdicao(Date dataEdicao) {
        this.dataEdicao = dataEdicao;
    }
}
