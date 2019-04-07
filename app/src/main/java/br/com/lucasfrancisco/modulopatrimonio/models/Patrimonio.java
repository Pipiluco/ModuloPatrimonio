package br.com.lucasfrancisco.modulopatrimonio.models;

public class Patrimonio extends Objeto {
    private String empresa;
    private String setor;
    private String plaqueta;
    private boolean isAtivo;

    public Patrimonio() {
    }

    public Patrimonio(String empresa, String setor, String plaqueta, boolean isAtivo, String tipo, String marca, String modelo) {
        super(tipo, marca, modelo);
        this.empresa = empresa;
        this.setor = setor;
        this.plaqueta = plaqueta;
        this.isAtivo = isAtivo;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
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
}
