/*-
 * ============LICENSE_START=======================================================
 * ONAP
 * ================================================================================
 * Copyright (C) 2020-2021 AT&T Intellectual Property. All rights reserved.
 * Modifications Copyright (C) 2023 Nordix Foundation.
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

package org.onap.policy.controlloop.actor.aai;

import jakarta.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.onap.policy.controlloop.actorserviceprovider.parameters.ControlLoopOperationParams;

/**
 * Utilities used by A&AI classes.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AaiUtil {

    /**
     * Makes standard request headers for A&AI requests.
     *
     * @param params operation parameters
     * @return new request headers
     */
    public static Map<String, Object> makeHeaders(ControlLoopOperationParams params) {
        Map<String, Object> headers = new HashMap<>();

        headers.put("X-FromAppId", "POLICY");
        headers.put("X-TransactionId", params.getRequestId().toString());
        headers.put("Accept", MediaType.APPLICATION_JSON);

        return headers;
    }
}
