package org.springframework.cloud.servicebroker.elephantsql.exception;

import org.springframework.cloud.servicebroker.exception.ServiceBrokerException;

/**
 * Exception thrown when issues with the underlying ElephantSQL service occur.
 *
 * @author ipolyzos
 */
public class ElephantSQLServiceException extends ServiceBrokerException {

    private static final long serialVersionUID = 1432950105016369392L;

    public ElephantSQLServiceException(String message) {
        super(message);
    }

}
