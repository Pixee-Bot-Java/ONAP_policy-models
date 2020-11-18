/*-
 * ============LICENSE_START=======================================================
 * ONAP
 * ================================================================================
 * Copyright (C) 2020 AT&T Intellectual Property. All rights reserved.
 * Modifications Copyright (C) 2020 Wipro Limited.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.controlloop.actor.so;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.aai.domain.yang.CloudRegion;
import org.onap.aai.domain.yang.GenericVnf;
import org.onap.aai.domain.yang.ModelVer;
import org.onap.aai.domain.yang.ServiceInstance;
import org.onap.aai.domain.yang.Tenant;
import org.onap.policy.common.endpoints.http.client.HttpClientFactoryInstance;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.controlloop.actorserviceprovider.OperationOutcome;
import org.onap.policy.controlloop.actorserviceprovider.OperationProperties;
import org.onap.policy.controlloop.actorserviceprovider.OperationResult;
import org.onap.policy.controlloop.actorserviceprovider.parameters.HttpPollingConfig;
import org.onap.policy.controlloop.actorserviceprovider.parameters.HttpPollingParams;
import org.onap.policy.so.SoRequest;
import org.onap.policy.so.SoResponse;

public class VfModuleCreateTest extends BasicSoOperation {


    private static final String MODEL_NAME2 = "my-model-name-B";
    private static final String MODEL_VERS2 = "my-model-version-B";
    private static final String SVC_INSTANCE_ID = "my-service-instance-id";
    private static final String VNF_ID = "my-vnf-id";

    private VfModuleCreate oper;

    public VfModuleCreateTest() {
        super(DEFAULT_ACTOR, VfModuleCreate.NAME);
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        initBeforeClass();
    }

    @AfterClass
    public static void tearDownAfterClass() {
        destroyAfterClass();
    }

    /**
     * Sets up.
     */
    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        oper = new VfModuleCreate(params, config);
        loadProperties();
    }

    /**
     * Tests "success" case with simulator.
     */
    @Test
    public void testSuccess() throws Exception {
        HttpPollingParams opParams = HttpPollingParams.builder().clientName(MY_CLIENT)
                        .path("serviceInstantiation/v7/serviceInstances").pollPath("orchestrationRequests/v5/")
                        .maxPolls(2).build();
        config = new HttpPollingConfig(blockingExecutor, opParams, HttpClientFactoryInstance.getClientFactory());

        params = params.toBuilder().retry(0).timeoutSec(5).executor(blockingExecutor).preprocessed(true).build();

        oper = new VfModuleCreate(params, config);

        loadProperties();

        // run the operation
        outcome = oper.start().get();
        assertEquals(OperationResult.SUCCESS, outcome.getResult());
        assertTrue(outcome.getResponse() instanceof SoResponse);

        int count = oper.getProperty(OperationProperties.DATA_VF_COUNT);
        assertEquals(VF_COUNT + 1, count);
    }

    @Test
    public void testConstructor() {
        assertEquals(DEFAULT_ACTOR, oper.getActorName());
        assertEquals(VfModuleCreate.NAME, oper.getName());
        assertTrue(oper.isUsePolling());

        // verify that target validation is done
        params = params.toBuilder().targetType(null).build();
        assertThatIllegalArgumentException().isThrownBy(() -> new VfModuleCreate(params, config))
                        .withMessageContaining("Target information");
    }

    @Test
    public void testGetPropertyNames() {
        // @formatter:off
        assertThat(oper.getPropertyNames()).isEqualTo(
                        List.of(
                            OperationProperties.AAI_SERVICE,
                            OperationProperties.AAI_SERVICE_MODEL,
                            OperationProperties.AAI_VNF,
                            OperationProperties.AAI_VNF_MODEL,
                            OperationProperties.AAI_DEFAULT_CLOUD_REGION,
                            OperationProperties.AAI_DEFAULT_TENANT,
                            OperationProperties.DATA_VF_COUNT));
        // @formatter:on
    }

    @Test
    public void testStartOperationAsync_testSuccessfulCompletion() throws Exception {
        when(client.post(any(), any(), any(), any())).thenAnswer(provideResponse(rawResponse));

        // use a real executor
        params = params.toBuilder().executor(ForkJoinPool.commonPool()).build();

        oper = new VfModuleCreate(params, config) {
            @Override
            protected long getPollWaitMs() {
                return 1;
            }
        };

        loadProperties();

        final int origCount = 30;
        oper.setVfCount(origCount);

        CompletableFuture<OperationOutcome> future2 = oper.start();

        outcome = future2.get(5, TimeUnit.SECONDS);
        assertEquals(OperationResult.SUCCESS, outcome.getResult());

        SoResponse resp = outcome.getResponse();
        assertNotNull(resp);
        assertEquals(REQ_ID.toString(), resp.getRequestReferences().getRequestId());

        assertEquals(origCount + 1, oper.getVfCount());
    }

    /**
     * Tests startOperationAsync() when polling is required.
     */
    @Test
    public void testStartOperationAsyncWithPolling() throws Exception {
        when(rawResponse.getStatus()).thenReturn(500, 500, 500, 500, 200, 200);

        when(client.post(any(), any(), any(), any())).thenAnswer(provideResponse(rawResponse));
        when(client.get(any(), any(), any())).thenAnswer(provideResponse(rawResponse));

        // use a real executor
        params = params.toBuilder().executor(ForkJoinPool.commonPool()).build();

        oper = new VfModuleCreate(params, config) {
            @Override
            public long getPollWaitMs() {
                return 1;
            }
        };

        loadProperties();

        CompletableFuture<OperationOutcome> future2 = oper.start();

        outcome = future2.get(5, TimeUnit.SECONDS);
        assertEquals(OperationResult.SUCCESS, outcome.getResult());
    }

    @Test
    public void testMakeRequest() throws CoderException {
        Pair<String, SoRequest> pair = oper.makeRequest();

        // @formatter:off
        assertEquals(
            "/my-service-instance-id/vnfs/my-vnf-id/vfModules/scaleOut",
            pair.getLeft());
        // @formatter:on

        verifyRequest("vfModuleCreate.json", pair.getRight());
    }

    private void loadProperties() {
        // set the properties
        ServiceInstance instance = new ServiceInstance();
        instance.setServiceInstanceId(SVC_INSTANCE_ID);
        oper.setProperty(OperationProperties.AAI_SERVICE, instance);

        ModelVer modelVers = new ModelVer();
        modelVers.setModelName(MODEL_NAME2);
        modelVers.setModelVersion(MODEL_VERS2);

        oper.setProperty(OperationProperties.AAI_SERVICE_MODEL, modelVers);
        oper.setProperty(OperationProperties.AAI_VNF_MODEL, modelVers);

        GenericVnf vnf = new GenericVnf();
        vnf.setVnfId(VNF_ID);
        oper.setProperty(OperationProperties.AAI_VNF, vnf);

        oper.setProperty(OperationProperties.AAI_DEFAULT_CLOUD_REGION, new CloudRegion());
        oper.setProperty(OperationProperties.AAI_DEFAULT_TENANT, new Tenant());

        oper.setProperty(OperationProperties.DATA_VF_COUNT, VF_COUNT);
    }
}
