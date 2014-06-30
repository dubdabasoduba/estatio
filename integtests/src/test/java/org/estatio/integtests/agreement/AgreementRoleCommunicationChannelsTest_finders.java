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
package org.estatio.integtests.agreement;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.agreement.AgreementRoleCommunicationChannels;
import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.communicationchannel.CommunicationChannels;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.lease.LeaseForOxfTopModel001;
import org.estatio.integtests.EstatioIntegrationTest;

public class AgreementRoleCommunicationChannelsTest_finders extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                execute(new EstatioBaseLineFixture(), executionContext);

                execute(new LeaseForOxfTopModel001(), executionContext);
            }
        });
    }

    @Inject
    private CommunicationChannels communicationChannels;

    @Inject
    private AgreementRoleCommunicationChannels agreementRoleCommunicationChannels;

    @Inject
    Parties parties;

    private CommunicationChannel communicationChannel;

    private Party party;

    @Before
    public void setUp() throws Exception {
        party = parties.findPartyByReference(LeaseForOxfTopModel001.TENANT_REFERENCE);
        communicationChannel = communicationChannels.findByOwnerAndType(party, CommunicationChannelType.POSTAL_ADDRESS).first();
    }

    @Test
    public void findByCommunicationChannel() throws Exception {
        assertThat(agreementRoleCommunicationChannels.findByCommunicationChannel(communicationChannel).get(0).getCommunicationChannel(), is(communicationChannel));
    }
}
