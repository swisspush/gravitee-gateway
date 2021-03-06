/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.gateway.services.endpoint.discovery.consul.verticle;

import io.gravitee.common.event.Event;
import io.gravitee.common.event.EventListener;
import io.gravitee.common.event.EventManager;
import io.gravitee.definition.model.Endpoint;
import io.gravitee.definition.model.HttpClientOptions;
import io.gravitee.definition.model.services.discovery.EndpointDiscoveryProvider;
import io.gravitee.definition.model.services.discovery.EndpointDiscoveryService;
import io.gravitee.definition.model.services.discovery.consul.ConsulEndpointDiscoveryConfiguration;
import io.gravitee.gateway.handlers.api.definition.Api;
import io.gravitee.gateway.reactor.Reactable;
import io.gravitee.gateway.reactor.ReactorEvent;
import io.gravitee.gateway.services.endpoint.discovery.consul.endpoint.DiscoveredEndpoint;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.consul.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class EndpointDiscoveryConsulVerticle extends AbstractVerticle implements EventListener<ReactorEvent, Reactable> {

    private final static Logger LOGGER = LoggerFactory.getLogger(EndpointDiscoveryConsulVerticle.class);

    private static final int CONSUL_DEFAULT_PORT = 8500;

    private final Map<String, Watch<ServiceEntryList>> watches = new HashMap<>();

    @Autowired
    private EventManager eventManager;

    @Override
    public void start(final Future<Void> startedResult) {
        eventManager.subscribeForEvents(this, ReactorEvent.class);
    }

    @Override
    public void onEvent(Event<ReactorEvent, Reactable> event) {
        final Api api = (Api) event.content().item();

        switch (event.type()) {
            case DEPLOY:
                startWatch(api);
                break;
            case UNDEPLOY:
                stopWatch(api);
                break;
            case UPDATE:
                stopWatch(api);
                startWatch(api);
                break;
        }
    }

    private void startWatch(Api api) {
        EndpointDiscoveryService discoveryService = api.getServices().get(EndpointDiscoveryService.class);

        if (api.isEnabled() && discoveryService != null && discoveryService.getProvider() == EndpointDiscoveryProvider.CONSUL) {
            LOGGER.info("Add Consul.io support for API: id[{}] name[{}]", api.getId(), api.getName());

            ConsulEndpointDiscoveryConfiguration providerConfiguration =
                    (ConsulEndpointDiscoveryConfiguration) discoveryService.getConfiguration();

            URI consulUri = URI.create(providerConfiguration.getUrl());

            ConsulClientOptions options = new ConsulClientOptions()
                    .setHost(consulUri.getHost())
                    .setPort((consulUri.getPort() != -1) ? consulUri.getPort() : CONSUL_DEFAULT_PORT)
                    .setDc(providerConfiguration.getDc())
                    .setAclToken(providerConfiguration.getAcl());

            LOGGER.info("Consul.io configuration: endpoint[{}] dc[{}] acl[{}]", consulUri.toString(), options.getDc(), options.getAclToken());

            ConsulClient consulClient = ConsulClient.create(vertx);
            consulClient.catalogServiceNodes(providerConfiguration.getService(), event -> {
                if (event.succeeded()) {
                    for (Service service : event.result().getList()) {
                        handleRegisterService(api, service);
                    }
                } else {
                    LOGGER.info("Unexpected error while getting services catalog", event.cause());
                }
            });

            Watch<ServiceEntryList> watch = Watch.service(providerConfiguration.getService(), vertx, options);
            watch.setHandler(event -> {
                if (event.succeeded()) {
                    LOGGER.debug("Receiving a Consul.io watch event for service: name[{}]", providerConfiguration.getService());

                    List<ServiceEntry> entries = event.nextResult().getList();
                    // Handle new services or updated services
                    for (ServiceEntry service : event.nextResult().getList()) {
                        handleRegisterService(api, service.getService());
                    }

                    // Handle de-registered services
                    if (event.prevResult() != null) {
                        List<ServiceEntry> oldEntries = event.prevResult().getList();
                        if (oldEntries.size() > entries.size()) {
                            // Select only de-registered services
                            oldEntries.removeAll(entries);
                            for (ServiceEntry oldEntry : oldEntries) {
                                handleDeregisterService(api, oldEntry.getService());
                            }
                        }
                    }
                } else {
                    LOGGER.info("Unexpected error while watching services catalog", event.cause());
                }
            }).start();

            this.watches.put(api.getId(), watch);
        }
    }

    private void handleDeregisterService(Api api, Service service) {
        LOGGER.info("Remove a de-registered endpoint from Consul.io: id[{}] name[{}]",
                service.getId(), service.getName());
        Endpoint endpoint = createEndpoint(service);
        api.getProxy().getEndpoints().remove(endpoint);
    }

    private void handleRegisterService(Api api, Service service) {
        LOGGER.info("Add a new endpoint from Consul.io: id[{}] name[{}]",
                service.getId(), service.getName());
        Endpoint endpoint = createEndpoint(service);

        Set<Endpoint> managedEndpoints = api.getProxy().getEndpoints();
        if (managedEndpoints.contains(endpoint)) {
            managedEndpoints.remove(endpoint);
        }

        managedEndpoints.add(endpoint);
    }

    private void stopWatch(Api api) {
        Watch<ServiceEntryList> watch = this.watches.remove(api.getId());
        if (watch != null) {
            watch.stop();
        }
    }

    private Endpoint createEndpoint(Service service) {
        String address =service.getAddress();
        if (address == null || address.isEmpty()) {
            address = service.getNodeAddress();
        }

        DiscoveredEndpoint endpoint = new DiscoveredEndpoint(
                "consul:" + service.getId(),
                "http://" + address + ':' + service.getPort());
        endpoint.setHttpClientOptions(new HttpClientOptions());

        return endpoint;
    }
}
