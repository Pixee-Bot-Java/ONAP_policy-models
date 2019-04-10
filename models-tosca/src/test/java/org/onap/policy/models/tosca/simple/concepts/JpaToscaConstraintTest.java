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

package org.onap.policy.models.tosca.simple.concepts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.onap.policy.models.tosca.authorative.concepts.ToscaConstraint;
import org.onap.policy.models.tosca.simple.concepts.JpaToscaConstraintLogical;

/**
 * DAO test for ToscaConstraintLogicalString.
 *
 * @author Liam Fallon (liam.fallon@est.tech)
 */
public class JpaToscaConstraintTest {

    @Test
    public void testConstraintLogicalStringPojo() {
        assertNotNull(new JpaToscaConstraintLogical(JpaToscaConstraintOperation.EQ, "Constraint"));

        try {
            new JpaToscaConstraintLogical((JpaToscaConstraintOperation) null, null);
            fail("test should throw an exception");
        } catch (Exception exc) {
            assertEquals("operation is marked @NonNull but is null", exc.getMessage());
        }

        try {
            new JpaToscaConstraintLogical((JpaToscaConstraintOperation) null, "Hello");
            fail("test should throw an exception");
        } catch (Exception exc) {
            assertEquals("operation is marked @NonNull but is null", exc.getMessage());
        }

        try {
            new JpaToscaConstraintLogical(JpaToscaConstraintOperation.EQ, null);
            fail("test should throw an exception");
        } catch (Exception exc) {
            assertEquals("compareTo is marked @NonNull but is null", exc.getMessage());
        }

        assertNotNull(new JpaToscaConstraintLogical(JpaToscaConstraintOperation.EQ, "Constraint"));

        assertEquals(0, new JpaToscaConstraintLogical(JpaToscaConstraintOperation.EQ, "")
                .compareTo(new JpaToscaConstraintLogical(JpaToscaConstraintOperation.EQ, "")));

        JpaToscaConstraintOperation op = JpaToscaConstraintOperation.EQ;
        assertEquals("equal_to", op.getToscaToken());

        List<String> validValues = new ArrayList<>();
        validValues.add("hello");
        validValues.add("goodbye");
        JpaToscaConstraintValidValues cvv0 = new JpaToscaConstraintValidValues(validValues);
        assertEquals(-1, cvv0.compareTo(null));
        assertEquals(0, cvv0.compareTo(cvv0));
        assertNotEquals(0, cvv0.compareTo(new JpaToscaConstraintLogical(JpaToscaConstraintOperation.EQ, "Constraint")));
        JpaToscaConstraintValidValues cvv1 = new JpaToscaConstraintValidValues(validValues);
        assertEquals(0, cvv0.compareTo(cvv1));

        cvv1.fromAuthorative(new ToscaConstraint());
        assertNotNull(cvv1.getValidValues());
    }
}