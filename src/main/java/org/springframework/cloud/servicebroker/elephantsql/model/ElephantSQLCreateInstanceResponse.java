package org.springframework.cloud.servicebroker.elephantsql.model;

/**
 * An instance of a ElephantSQL Create Instance Response Definition
 *
 * @author ipolyzos
 */
public class ElephantSQLCreateInstanceResponse {

    private String id;
    private String url;

    public ElephantSQLCreateInstanceResponse() {
        super();
    }

    public ElephantSQLCreateInstanceResponse(final String url) {
        this.url = url;
    }

    public ElephantSQLCreateInstanceResponse(final String id,
                                             final String url) {
        this.id = id;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }
}
