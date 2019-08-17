package br.net.easify.apiwebservice.Model;

public class OrgaoMembro {
    private String cargo;
    private String ideCadastro;
    private String nome;
    private String partido;
    private String uf;
    private String situacao;

    public OrgaoMembro(String cargo, String ideCadastro, String nome, String partido, String uf, String situacao) {
        this.cargo = cargo;
        this.ideCadastro = ideCadastro;
        this.nome = nome;
        this.partido = partido;
        this.uf = uf;
        this.situacao = situacao;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getIdeCadastro() {
        return ideCadastro;
    }

    public void setIdeCadastro(String ideCadastro) {
        this.ideCadastro = ideCadastro;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPartido() {
        return partido;
    }

    public void setPartido(String partido) {
        this.partido = partido;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }
}
