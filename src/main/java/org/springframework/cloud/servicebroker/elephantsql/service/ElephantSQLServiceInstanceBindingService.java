package org.springframework.cloud.servicebroker.elephantsql.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.elephantsql.config.BrokerConfig;
import org.springframework.cloud.servicebroker.elephantsql.exception.ElephantSQLServiceException;
import org.springframework.cloud.servicebroker.elephantsql.model.ServiceInstance;
import org.springframework.cloud.servicebroker.elephantsql.model.ServiceInstanceBinding;
import org.springframework.cloud.servicebroker.elephantsql.repository.ElephantSQLServiceInstanceBindingRepository;
import org.springframework.cloud.servicebroker.elephantsql.repository.ElephantSQLServiceInstanceRepository;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceAppBindingResponse;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

/**
 * Service instance binding service.
 * <p>
 * NOTE:
 * Binding a service does the following:
 * 1. Creates a PostgeSQL instance
 * 2. Retrieve instance credentials
 * 3. Saves the ServiceInstanceBinding info to the broker's
 *    repository along with credential and URI
 *
 * @author ipolyzos
 */
@Service
public class ElephantSQLServiceInstanceBindingService implements ServiceInstanceBindingService {

    private ElephantSQLAdminService elephantSQLAdminService;

    private BrokerConfig brokerConfig;

    private ElephantSQLServiceInstanceRepository instanceRepository;

    private ElephantSQLServiceInstanceBindingRepository bindingRepository;

    @Autowired
    public ElephantSQLServiceInstanceBindingService(final ElephantSQLAdminService elephantSQLAdminService,
                                                    final BrokerConfig brokerConfig,
                                                    final ElephantSQLServiceInstanceRepository instanceRepository,
                                                    final ElephantSQLServiceInstanceBindingRepository bindingRepository) {
        this.elephantSQLAdminService = elephantSQLAdminService;
        this.brokerConfig = brokerConfig;
        this.instanceRepository = instanceRepository;
        this.bindingRepository = bindingRepository;
    }

    @Override
    public CreateServiceInstanceBindingResponse createServiceInstanceBinding(final CreateServiceInstanceBindingRequest request) {
        final String bindingId = request.getBindingId();
        final String serviceInstanceId = request.getServiceInstanceId();

        ServiceInstanceBinding binding = bindingRepository.findOne(bindingId);
        if (binding != null) {
            throw new ServiceInstanceBindingExistsException(serviceInstanceId, bindingId);
        }

        ServiceInstance instance = instanceRepository.findOne(serviceInstanceId);
        if (instance == null) {
            throw new ElephantSQLServiceException("Instance don't exist :" + serviceInstanceId);
        }

        final Map<String, Object> credentials = Collections.singletonMap("uri", instance.getServerUrl());
        binding = new ServiceInstanceBinding(bindingId, serviceInstanceId, credentials, null, request.getBoundAppGuid());
        bindingRepository.save(binding);

        return new CreateServiceInstanceAppBindingResponse().withCredentials(credentials);
    }

    @Override
    public void deleteServiceInstanceBinding(final DeleteServiceInstanceBindingRequest request) {
        final String bindingId = request.getBindingId();
        final String serviceInstanceId = request.getServiceInstanceId();

        final ServiceInstanceBinding binding = getServiceInstanceBinding(bindingId);
        if (binding == null) {
            throw new ServiceInstanceBindingDoesNotExistException(bindingId);
        }

        bindingRepository.delete(bindingId);
    }

    protected ServiceInstanceBinding getServiceInstanceBinding(final String bindingId) {
        return bindingRepository.findOne(bindingId);
    }
}