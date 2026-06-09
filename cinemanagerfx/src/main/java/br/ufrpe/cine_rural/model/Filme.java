package br.ufrpe.cine_rural.model;

import br.ufrpe.cine_rural.enums.ClassificacaoIndicativa;
import br.ufrpe.cine_rural.enums.Genero;

public class Filme {

    private String titulo;
    private String sinopse;
    private int duracao;
    private Genero genero;
    private ClassificacaoIndicativa classificacao;
    private String caminhoPoster;

    public Filme(String titulo,
                 String sinopse,
                 int duracao,
                 Genero genero,
                 ClassificacaoIndicativa classificacao,
                 String caminhoPoster) {

        this.titulo = titulo;
        this.sinopse = sinopse;
        this.duracao = duracao;
        this.genero = genero;
        this.classificacao = classificacao;
        this.caminhoPoster = caminhoPoster;
    }

    // --- GETTERS ---
    public String getTitulo() {
        return titulo;
    }

    public String getSinopse() {
        return sinopse;
    }

    public int getDuracao() {
        return duracao;
    }

    public Genero getGenero() {
        return genero;
    }

    public ClassificacaoIndicativa getClassificacao() {
        return classificacao;
    }

    public String getCaminhoPoster() {
        return caminhoPoster;
    }


    // --- SETTERS (Adicionados aqui para permitir alteração dos dados) ---
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public void setDuracao(int duracao) {
        this.duracao = duracao;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public void setClassificacao(ClassificacaoIndicativa classificacao) {
        this.classificacao = classificacao;
    }

    public void setCaminhoPoster(String caminhoPoster) {
        this.caminhoPoster = caminhoPoster;
    }


    @Override
    public String toString() {
        return titulo;
    }
}