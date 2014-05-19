/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.integtests.party;

import javax.inject.Inject;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Person;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.party.PersonsAndOrganisationsAndCommunicationChannelsFixture;
import org.estatio.integtests.EstatioIntegrationTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.CompositeFixtureScript;

public class PersonTest_setNameIGNORED extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        scenarioExecution().install(new CompositeFixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                execute(new EstatioBaseLineFixture(), executionContext);
                execute("parties", new PersonsAndOrganisationsAndCommunicationChannelsFixture(), executionContext);
            }
        });
    }

    @Inject
    private Parties parties;

    private Person personJoeDoe;

    @Before
    public void setUp() throws Exception {
        personJoeDoe = (Person)parties.findParties("Doe, Jo*").get(0);
    }

    @Ignore // have raised EST-200
    @Test
    public void cannotModifyName() throws Exception {
        expectedExceptions.expectMessage("Cannot be updated directly; derived from first and last names");
        wrap(personJoeDoe).setName("Cannot change name directly");
    }

}