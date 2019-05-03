package br.com.lucasfrancisco.modulopatrimonio.models;

import java.io.Serializable;
import java.util.List;

public class Patrimonio implements Serializable {
    private Usuario criador;
    private String plaqueta;
    private boolean isAtivo;
    private Setor setor;
    private Objeto objeto;
    private List<Imagem> imagens;

    public Patrimonio() {
    }

    public Patrimonio(Usuario criador, String plaqueta, boolean isAtivo, Setor setor, Objeto objeto, List<Imagem> imagens) {
        this.criador = criador;
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
