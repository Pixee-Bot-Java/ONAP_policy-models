/*-
 * ============LICENSE_START=======================================================
 * ONAP
 * ================================================================================
 * Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.models.tosca.authorative.concepts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.models.base.PfConceptKey;
import org.onap.policy.models.base.PfKey;

public class ToscaNameVersionTest {

    private static final String MY_NAME = "MyName";
    private static final String MY_VERSION = "1.2.3";

    private ToscaNameVersion tosca;

    @Before
    public void setUp() {
        tosca = new ToscaNameVersion(MY_NAME, MY_VERSION);
    }

    @Test
    public void testToscaNameVersionPfKey() {
        tosca = new ToscaNameVersion(new PfConceptKey(MY_NAME, MY_VERSION));
        assertEquals(MY_NAME, tosca.getName());
        assertEquals(MY_VERSION, tosca.getVersion());

        assertThatThrownBy(() -> new ToscaNameVersion((PfKey) null)).isInstanceOf(NullPointerException.class)
                        .hasMessageContaining("key").hasMessageContaining("is null");
    }

    @Test
    public void testToscaNameVersionToscaNameVersion() {
        tosca = new ToscaNameVersion(tosca);
        assertEquals(MY_NAME, tosca.getName());
        assertEquals(MY_VERSION, tosca.getVersion());
    }

    @Test
    public void testAsConceptKey() {
        assertEquals(new PfConceptKey(MY_NAME, MY_VERSION), tosca.asConceptKey());
    }

    @Test
    public void testCommonCompareTo() {
        assertThat(tosca.commonCompareTo(tosca)).isZero();
        assertThat(tosca.commonCompareTo(null)).isNotZero();
        assertThat(tosca.commonCompareTo(new MyNameVersion(MY_NAME, MY_VERSION))).isNotZero();
        assertThat(tosca.commonCompareTo(new ToscaNameVersion(tosca))).isZero();
        assertThat(tosca.commonCompareTo(new ToscaNameVersion(MY_NAME, null))).isNotZero();
        assertThat(tosca.commonCompareTo(new ToscaNameVersion(null, MY_VERSION))).isNotZero();
    }

    @Test
    public void testToString() {
        assertEquals(MY_NAME + " " + MY_VERSION, tosca.toString());
    }

    @Test
    public void testToscaNameVersion() {
        tosca = new ToscaNameVersion();
        assertNull(tosca.getName());
        assertNull(tosca.getVersion());
    }

    @Test
    public void testToscaNameVersionStringString() {
        assertEquals(MY_NAME, tosca.getName());
        assertEquals(MY_VERSION, tosca.getVersion());
    }

    private static class MyNameVersion extends ToscaNameVersion {
        public MyNameVersion(String name, String version) {
            super(name, version);
        }
    }
}
