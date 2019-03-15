/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
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
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.models.tosca.serialization.simple;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import lombok.NonNull;

import org.onap.policy.models.base.PfConceptKey;
import org.onap.policy.models.tosca.concepts.ToscaServiceTemplate;
import org.onap.policy.models.tosca.concepts.ToscaTopologyTemplate;

/**
 * GSON type adapter for TOSCA policies.
 *
 * @author Liam Fallon (liam.fallon@est.tech)
 */
public class ToscaServiceTemplateJsonAdapter
        implements JsonSerializer<ToscaServiceTemplate>, JsonDeserializer<ToscaServiceTemplate> {
    @Override
    public ToscaServiceTemplate deserialize(@NonNull final JsonElement serviceTemplateElement, @NonNull final Type type,
            @NonNull final JsonDeserializationContext context) {

        // The incoming JSON
        final JsonObject serviceTemplateJsonObject = serviceTemplateElement.getAsJsonObject();

        // The outgoing object
        final PfConceptKey serviceTemplateKey = new PfConceptKey("IncomingServiceTemplate", "0.0.1");
        final ToscaServiceTemplate serviceTemplate = new ToscaServiceTemplate(serviceTemplateKey);
        serviceTemplate
                .setToscaDefinitionsVersion(serviceTemplateJsonObject.get("tosca_definitions_version").getAsString());

        if (serviceTemplateJsonObject.has("topology_template")) {
            serviceTemplate.setTopologyTemplate(new ToscaTopologyTemplateJsonAdapter().deserialize(
                    serviceTemplateJsonObject.get("topology_template"), ToscaTopologyTemplate.class, context));
        }

        // Set the parent key of the topology template to be this service template
        serviceTemplate.getTopologyTemplate().getKey().setParentConceptKey(serviceTemplateKey);

        return serviceTemplate;
    }

    @Override
    public JsonElement serialize(@NonNull final ToscaServiceTemplate serviceTemplate, @NonNull final Type type,
            @NonNull final JsonSerializationContext context) {

        return null;
    }
}
