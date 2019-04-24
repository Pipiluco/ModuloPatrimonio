package br.com.lucasfrancisco.modulopatrimonio.models;

import java.io.Serializable;
import java.util.List;

public class Patrimonio extends Objeto implements Serializable {
    private String plaqueta;
    private boolean isAtivo;
    private Setor setor;
    private List<Imagem> imagens;

    public Patrimonio() {
    }


    public Patrimonio(String tipo, String marca, String modelo, String plaqueta, boolean isAtivo, Setor setor) {
        super(tipo, marca, modelo);
        this.plaqueta = plaqueta;
        this.isAtivo = isAtivo;
        this.setor = setor;
    }

    public Patrimonio(String tipo, String marca, String modelo, String plaqueta, boolean isAtivo, Setor setor, List<Imagem> imagens) {
        super(tipo, marca, modelo);
        this.plaqueta = plaqueta;
        this.isAtivo = isAtivo;
        this.setor = setor;
        this.imagens = imagens;
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
}
