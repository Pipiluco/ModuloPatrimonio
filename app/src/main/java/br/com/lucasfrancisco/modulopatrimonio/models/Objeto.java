package br.com.lucasfrancisco.modulopatrimonio.models;

import java.io.Serializable;
import java.util.Date;

public class Objeto extends Rastreamento implements Serializable {
    private String tipo;
    private String marca;
    private String modelo;


    public Objeto() {
    }

    public Objeto(Usuario criador, Usuario editor, Date dataCriacao, Date dataEdicao, String tipo, String marca, String modelo) {
        super(criador, editor, dataCriacao, dataEdicao);
        this.tipo = tipo;
        this.marca = marca;
        this.modelo = modelo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    @Override
    public String toString() {
        return this.tipo + " - " + this.marca + " " + this.modelo;
    }
}
