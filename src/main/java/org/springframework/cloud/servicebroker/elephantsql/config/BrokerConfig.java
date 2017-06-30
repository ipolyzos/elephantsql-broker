package org.springframework.cloud.servicebroker.elephantsql.config;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

@Configuration
@EnableMongoRepositories(basePackages = "org.springframework.cloud.servicebroker.elephantsql.repository")
public class BrokerConfig {

    @Value("${ELEPHANTSQL_API_URL:https://customer.elephantsql.com/api}")
    private String elephantSqlApiUrl;

    @Value("${ELEPHANTSQL_API_KEY}")
    private String elephantSqlApiKey;

    @Value("${ELEPHANTSQL_REGION:amazon-web-services::eu-west-1}")
    private String elephantSqlRegion;

    /**
     * Build a Jersey http client instance
     *
     * @return Client
     */
    @Bean
    public Client restClient() {
        final ClientConfig cc = new ClientConfig().register(new JacksonFeature());
        final HttpAuthenticationFeature httpAuthenticationFeature = HttpAuthenticationFeature.basic(elephantSqlApiKey, "");

        final Client client = ClientBuilder.newClient(cc);
        client.register(httpAuthenticationFeature);

        return client;
    }


    public String getElephantSqlApiUrl() {
        return elephantSqlApiUrl;
    }

    public void setElephantSqlApiUrl(String elephantSqlApiUrl) {
        this.elephantSqlApiUrl = elephantSqlApiUrl;
    }

    public String getElephantSqlApiKey() {
        return elephantSqlApiKey;
    }

    public void setElephantSqlApiKey(String elephantSqlApiKey) {
        this.elephantSqlApiKey = elephantSqlApiKey;
    }

    public String getElephantSqlRegion() {
        return elephantSqlRegion;
    }

    public void setElephantSqlRegion(String elephantSqlRegion) {
        this.elephantSqlRegion = elephantSqlRegion;
    }
}
