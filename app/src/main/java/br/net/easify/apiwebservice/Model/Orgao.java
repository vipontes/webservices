package br.net.easify.apiwebservice.Model;

public class Orgao {
    private String id;
    private String idTipodeOrgao;
    private String sigla;
    private String descricao;

    public Orgao(String id, String idTipodeOrgao, String sigla, String descricao) {
        this.id = id;
        this.idTipodeOrgao = idTipodeOrgao;
        this.sigla = sigla;
        this.descricao = descricao;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdTipodeOrgao() {
        return idTipodeOrgao;
    }

    public void setIdTipodeOrgao(String idTipodeOrgao) {
        this.idTipodeOrgao = idTipodeOrgao;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
