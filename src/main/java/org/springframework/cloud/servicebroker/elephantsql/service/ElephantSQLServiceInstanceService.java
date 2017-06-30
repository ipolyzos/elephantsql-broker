package org.springframework.cloud.servicebroker.elephantsql.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.elephantsql.config.BrokerConfig;
import org.springframework.cloud.servicebroker.elephantsql.exception.ElephantSQLServiceException;
import org.springframework.cloud.servicebroker.elephantsql.model.ElephantSQLCreateInstanceResponse;
import org.springframework.cloud.servicebroker.elephantsql.model.ServiceInstance;
import org.springframework.cloud.servicebroker.elephantsql.repository.ElephantSQLServiceInstanceRepository;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceExistsException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceUpdateNotSupportedException;
import org.springframework.cloud.servicebroker.model.*;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.stereotype.Service;

/**
 * Service Instance Service implementation to manage service instances.
 * <p>
 * NOTE:
 * Creating a service does the following:
 * <p>
 * 1. Creates a new database instance
 * 2. Saves the Service Instance info to the broker's database.
 *
 * @author ipolyzos
 */
@Service
public class ElephantSQLServiceInstanceService implements ServiceInstanceService {

    private BrokerConfig config;

    private ElephantSQLAdminService elephantSQLAdminService;

    private ElephantSQLServiceInstanceRepository repository;

    @Autowired
    public ElephantSQLServiceInstanceService(final BrokerConfig config,
                                             final ElephantSQLAdminService admin,
                                             final ElephantSQLServiceInstanceRepository repository) {
        this.config = config;
        this.elephantSQLAdminService = admin;
        this.repository = repository;
    }

    @Override
    public CreateServiceInstanceResponse createServiceInstance(final CreateServiceInstanceRequest request) {
        ServiceInstance instance = repository.findOne(request.getServiceInstanceId());
        if (instance != null) {
            throw new ServiceInstanceExistsException(request.getServiceInstanceId(), request.getServiceDefinitionId());
        }

        instance = new ServiceInstance(request);

        if (elephantSQLAdminService.serverInstanceExists(instance.getServiceInstanceId())) {
            // ensure the instance is empty
            elephantSQLAdminService.deleteServerInstance(instance.getServiceInstanceId());
        }

        //collect params
        final String iid = instance.getServiceInstanceId();
        final String iplan = instance.getPlanId().split("_")[0];

        final ElephantSQLCreateInstanceResponse response = elephantSQLAdminService.createInstance(iid, iplan);
        if (response == null) {
            throw new ServiceBrokerException("Failed to create new broker instance: " + instance.getServiceInstanceId());
        }

        //fill object with required information
        instance.setServerUrl(response.getUrl());
        instance.setElephantsqlId(response.getId());

        repository.save(instance);

        return new CreateServiceInstanceResponse();
    }

    @Override
    public GetLastServiceOperationResponse getLastOperation(final GetLastServiceOperationRequest request) {
        return new GetLastServiceOperationResponse().withOperationState(OperationState.SUCCEEDED);
    }

    public ServiceInstance getServiceInstance(String id) {
        return repository.findOne(id);
    }

    @Override
    public DeleteServiceInstanceResponse deleteServiceInstance(final DeleteServiceInstanceRequest request) throws ElephantSQLServiceException {
        final String instanceId = request.getServiceInstanceId();
        final ServiceInstance instance = repository.findOne(instanceId);

        if (instance == null) {
            throw new ServiceInstanceDoesNotExistException(instanceId);
        }

        elephantSQLAdminService.deleteServerInstance(instance.getElephantsqlId());
        repository.delete(instanceId);

        return new DeleteServiceInstanceResponse();
    }

    @Override
    public UpdateServiceInstanceResponse updateServiceInstance(final UpdateServiceInstanceRequest request) {
        final String instanceId = request.getServiceInstanceId();
        final ServiceInstance instance = repository.findOne(instanceId);

        if (instance == null) {
            throw new ServiceInstanceDoesNotExistException(instanceId);
        }

        if (request.equals(instance)){
            throw new ServiceInstanceUpdateNotSupportedException("No changes in the change request");
        }

        // extract required values
        final String reqplan = request.getPlanId().split("_")[0];
        final String brkId = instance.getElephantsqlId();

        // change instance to a new plan
        elephantSQLAdminService.changeInstance(brkId, instanceId, reqplan );
        instance.setPlanId(request.getPlanId());

        //replace old instance
        repository.delete(instanceId);
        repository.save(instance);

        return new UpdateServiceInstanceResponse();
    }
}