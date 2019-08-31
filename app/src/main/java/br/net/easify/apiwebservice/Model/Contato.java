package br.net.easify.apiwebservice.Model;

import java.io.Serializable;

public class Contato  implements Serializable {
    private String id;
    private String nome;
    private String telefone;
    private String email;

    public Contato(String id, String nome, String telefone, String email)
    {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
    }

    public Contato()
    {
        this.id = "";
        this.nome = "";
        this.telefone = "";
        this.email = "";
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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
