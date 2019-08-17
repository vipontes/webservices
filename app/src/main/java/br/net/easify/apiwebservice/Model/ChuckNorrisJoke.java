package br.net.easify.apiwebservice.Model;

public class ChuckNorrisJoke {
    private String createdAt;
    private String updatedAt;
    private String iconUrl;
    private String id;
    private String url;
    private String value;

    public ChuckNorrisJoke(String createdAt, String updatedAt, String iconUrl, String id, String url, String value) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.iconUrl = iconUrl;
        this.id = id;
        this.url = url;
        this.value = value;
    }

    public ChuckNorrisJoke() {
        this.createdAt = "";
        this.updatedAt = "";
        this.iconUrl = "";
        this.id = "";
        this.url = "";
        this.value = "";
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
