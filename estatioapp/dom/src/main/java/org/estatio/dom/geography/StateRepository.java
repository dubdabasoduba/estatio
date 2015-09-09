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
package org.estatio.dom.geography;

import java.util.Collections;
import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;

import org.estatio.dom.UdoDomainRepositoryAndFactory;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = State.class)
public class StateRepository
        extends UdoDomainRepositoryAndFactory<State> {

    public StateRepository() {
        super(StateRepository.class, State.class);
    }

    // //////////////////////////////////

    public State createState(final String reference, final String name, final Country country) {
        State state = newTransientInstance();
        state.setReference(reference);
        state.setName(name);
        state.setCountry(country);
        persist(state);
        return state;
    }

    // //////////////////////////////////////

    public List<State> allStates() {
        return allInstances();
    }

    // //////////////////////////////////////

    public List<State> findStatesByCountry(final Country country) {
        return country != null ? allMatches("findByCountry", "country", country) : Collections.<State>emptyList();
    }

    // //////////////////////////////////////

    public State findState(final @ParameterLayout(named = "Reference") String reference) {
        return firstMatch("findByReference", "reference", reference);
    }

}
