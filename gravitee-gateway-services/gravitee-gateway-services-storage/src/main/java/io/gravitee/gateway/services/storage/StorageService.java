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
package io.gravitee.gateway.services.storage;

import io.gravitee.common.service.AbstractService;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.swisspush.reststorage.RestStorageMod;
import org.swisspush.reststorage.util.ModuleConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Starts one or more resource storages.
 *
 * Configuration properties are those of https://github.com/swisspush/vertx-rest-storage#configuration-1 plus
 * the <code>instances</code> property to choose the number of verticle instances to spawn.
 *
 * The configuration path for keys is <code>services.storage.[storage-name].[property]</code>
 *
 * @author Laurent Bovet <laurent.bovet@swisspush.org>
 */
public class StorageService extends AbstractService {

    private static final Pattern PROPERTY_PATTERN =
            Pattern.compile("services\\.storage\\.([^\\.]*)\\.([^\\.]*)");

    private static final int DEFAULT_INSTANCE_COUNT = 4;
    private static final String INSTANCES_KEY = "instances";
    private static final List<String> INT_PROPERTIES = Arrays.asList("port", "redisPort");
    private static final List<String> LONG_PROPERTIES = Arrays.asList("resourceCleanupAmount", "freeMemoryCheckIntervalMs");
    private static final List<String> BOOLEAN_PROPERTIES = Arrays.asList("confirmCollectionDelete", "rejectStorageWriteOnLowMemory");

    @Autowired
    private Vertx vertx;

    @Autowired
    private ConfigurableEnvironment environment;

    private List<String> verticleIds;

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        verticleIds =
                Stream.of(environment.getPropertySources().iterator())
                        .filter(source -> source instanceof EnumerablePropertySource)
                        // get all matching property names
                        .flatMap(source ->
                                Stream.of(((EnumerablePropertySource) source).getPropertyNames())
                                        .map(PROPERTY_PATTERN::matcher)
                                        .filter(Matcher::matches))
                        // group by storage name
                        .collect(Collectors.groupingBy(matcher -> matcher.group(1)))
                        .entrySet().stream()
                        .map(entry -> {
                            // Prepare storage config
                            JsonObject config = defaultConfig(entry.getKey());
                            entry.getValue().forEach(matcher -> {
                                String property = matcher.group(2);
                                config.put(property, environment.getProperty(matcher.group(0), propertyType(property)));
                            });
                            return createStorage(config);
                        })
                        .collect(Collectors.toList());

        // starts the default storage with default config if nothing configured
        if(verticleIds.isEmpty()) {
            verticleIds = Collections.singletonList(createStorage(defaultConfig("default")));
        }
    }

    private String createStorage(JsonObject config) {
        int instances = config.containsKey(INSTANCES_KEY) ? config.getInteger(INSTANCES_KEY) : DEFAULT_INSTANCE_COUNT;
        CompletableFuture<String> future = new CompletableFuture<>();
        vertx.deployVerticle(RestStorageMod.class,
                new DeploymentOptions().setConfig(config).setInstances(instances),
                res -> {
                    if (res.succeeded()) {
                        future.complete(res.result());
                    } else {
                        future.completeExceptionally(res.cause());
                    }
                });
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private JsonObject defaultConfig(String name) {
        return ModuleConfiguration.with()
                .collectionsPrefix("storage:"+name+":collections")
                .deltaEtagsPrefix("storage:"+name+":delta:etags")
                .deltaResourcesPrefix("storage:"+name+":delta:resources")
                .expirablePrefix("storage:"+name+":expirable")
                .lockPrefix("storage:"+name+":locks")
                .storageAddress("io.gravitee.gateway.services.storage."+name)
                .storageType(ModuleConfiguration.StorageType.redis)
                .build()
                .asJsonObject();
    }

    private Class<?> propertyType(String property) {
        if (INT_PROPERTIES.contains(property)) {
            return int.class;
        } else if (LONG_PROPERTIES.contains(property)) {
            return long.class;
        } else if (BOOLEAN_PROPERTIES.contains(property)) {
            return boolean.class;
        } else {
            return String.class;
        }
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        if (verticleIds != null) {
            verticleIds.forEach(vertx::undeploy);
        }
    }
}
