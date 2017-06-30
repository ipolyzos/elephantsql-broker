package org.springframework.cloud.servicebroker.elephantsql.repository;

import org.springframework.cloud.servicebroker.elephantsql.model.ServiceInstanceBinding;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository for Service Instance Binding objects
 *
 * @author ipolyzos
 */
public interface ElephantSQLServiceInstanceBindingRepository extends MongoRepository<ServiceInstanceBinding, String> {
}