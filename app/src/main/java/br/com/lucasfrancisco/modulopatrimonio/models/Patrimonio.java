package br.com.lucasfrancisco.modulopatrimonio.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Patrimonio implements Serializable {
    private Usuario criador;
    private Usuario editor;
    private Date dataCriacao;
    private Date dataEdicao;
    private String plaqueta;
    private boolean isAtivo;
    private Setor setor;
    private Objeto objeto;
    private List<Imagem> imagens;

    public Patrimonio() {
    }

    public Patrimonio(Usuario criador, Usuario editor, Date dataCriacao, Date dataEdicao, String plaqueta, boolean isAtivo, Setor setor, Objeto objeto, List<Imagem> imagens) {
        this.criador = criador;
        this.editor = editor;
        this.dataCriacao = dataCriacao;
        this.dataEdicao = dataEdicao;
        this.plaqueta = plaqueta;
        this.isAtivo = isAtivo;
        this.setor = setor;
        this.objeto = objeto;
        this.imagens = imagens;
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

    public String getPlaqueta() {
        return plaqueta;
    }

    public void setPlaqueta(String plaqueta) {
        this.plaqueta = plaqueta;
    }

    public boolean isAtivo() {
        return isAtivo;
    }

    public void setAtivo(boolean ativo) {
        isAtivo = ativo;
    }

    public Setor getSetor() {
        return setor;
    }

    public void setSetor(Setor setor) {
        this.setor = setor;
    }

    public List<Imagem> getImagens() {
        return imagens;
    }

    public void setImagens(List<Imagem> imagens) {
        this.imagens = imagens;
    }

    public Objeto getObjeto() {
        return objeto;
    }

    public void setObjeto(Objeto objeto) {
        this.objeto = objeto;
    }
}
