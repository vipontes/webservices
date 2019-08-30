package br.net.easify.apiwebservice.Model;

import java.io.Serializable;

public class Empresa implements Serializable {
    private String id;
    private String nome;
    private String logo;
    private double latitude;
    private double longitide;

    public Empresa(String id, String nome, String logo) {
        this.id = id;
        this.nome = nome;
        this.logo = logo;
        this.latitude = 0;
        this.longitide = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitide() {
        return longitide;
    }

    public void setLongitide(double longitide) {
        this.longitide = longitide;
    }
}
