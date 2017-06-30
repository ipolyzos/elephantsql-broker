package org.springframework.cloud.servicebroker.elephantsql.repository;

import org.springframework.cloud.servicebroker.elephantsql.model.ServiceInstance;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository for Service Instance objects
 *
 * @author ipolyzos
 */
public interface ElephantSQLServiceInstanceRepository extends MongoRepository<ServiceInstance, String> {
}