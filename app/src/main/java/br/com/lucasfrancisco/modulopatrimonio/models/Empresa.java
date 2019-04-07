package br.com.lucasfrancisco.modulopatrimonio.models;

public class Empresa {
    private String nome;
    private String fantasia;
    private String codigo;
    private String CNPJ;

    public Empresa() {
    }

    public Empresa(String nome, String fantasia, String codigo, String CNPJ) {
        this.nome = nome;
        this.fantasia = fantasia;
        this.codigo = codigo;
        this.CNPJ = CNPJ;
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
}
