package org.springframework.cloud.servicebroker.elephantsql.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.elephantsql.config.BrokerConfig;
import org.springframework.cloud.servicebroker.elephantsql.exception.ElephantSQLServiceException;
import org.springframework.cloud.servicebroker.elephantsql.model.ElephantSQLCreateInstanceResponse;
import org.springframework.cloud.servicebroker.elephantsql.model.ElephantSQLInstance;
import org.springframework.stereotype.Service;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Utility class for manipulating ElephantSQL account
 *
 * @author ipolyzos
 */
@Service
public class ElephantSQLAdminService {

    private Logger logger = LoggerFactory.getLogger(ElephantSQLAdminService.class);

    /**
     * Service Broker Config
     */
    private BrokerConfig brokerConfig;

    /**
     * Jersey Rest Client
     */
    private Client client;

    @Autowired
    public ElephantSQLAdminService(final Client restClient,
                                   final BrokerConfig brokerConfig) {
        this.client = restClient;
        this.brokerConfig = brokerConfig;
    }

    /**
     * Create a broker Instance
     *
     * @param serverName
     * @return
     * @throws ElephantSQLServiceException
     */
    public ElephantSQLCreateInstanceResponse createInstance(final String serverName,
                                                            final String plan) throws ElephantSQLServiceException {
        // build input form
        final MultivaluedMap<String, String> form = new MultivaluedHashMap<>();
        form.add("name", serverName);
        form.add("plan", plan);
        form.add("region", brokerConfig.getElephantSqlRegion());

        //build post request
        final String target = String.format("%s/%s", brokerConfig.getElephantSqlApiUrl(), "instances");
        final WebTarget webTarget = client.target(target);

        // call create broker instances API
        return webTarget.request(MediaType.APPLICATION_JSON)
                .post(Entity.form(form), ElephantSQLCreateInstanceResponse.class);
    }

    /**
     * Change a broker Instance
     *
     * @param serverName
     * @throws ElephantSQLServiceException
     */
    public void changeInstance(final String instanseId,
                               final String serverName,
                               final String plan) throws ElephantSQLServiceException {
        // build input form
        final MultivaluedMap<String, String> form = new MultivaluedHashMap<>();
        form.add("name", serverName);
        form.add("plan", plan);

        //build post request
        final String target = String.format("%s/%s/%s", brokerConfig.getElephantSqlApiUrl(), "instances",instanseId);
        final WebTarget webTarget = client.target(target);

        // call create broker instances API
        webTarget.request(MediaType.APPLICATION_JSON).put(Entity.form(form));
    }

    /**
     * Check if Broker Instance exists
     *
     * @param serverName
     * @return
     * @throws ElephantSQLServiceException
     */
    public boolean serverInstanceExists(final String serverName) throws ElephantSQLServiceException {
        final String target = String.format("%s/%s/%s", brokerConfig.getElephantSqlApiUrl(), "instances", serverName);
        final WebTarget webTarget = client.target(target);

        try {
            // call create broker instances API
            final ElephantSQLInstance[] response = webTarget.request(MediaType.APPLICATION_JSON)
                    .get(ElephantSQLInstance[].class);

            // check brokers exist
            if (response.length > 0) {
                return true;
            }
        } catch (NotFoundException e) {
            return false;
        }

        return false;
    }


    /**
     * Delete a broker instance
     *
     * @param serverInstanceId
     * @throws ElephantSQLServiceException
     */
    public String deleteServerInstance(final String serverInstanceId) throws ElephantSQLServiceException {
        final String target = String.format("%s/%s/%s", brokerConfig.getElephantSqlApiUrl(), "instances", serverInstanceId);
        final WebTarget webTarget = client.target(target);

        // call delete broker instance API
        return webTarget.request().delete(String.class);
    }

}
