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
package org.estatio.dom.index;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.Dflt;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.apptenancy.ApplicationTenancyRepository;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = Index.class)
public class IndexRepository extends UdoDomainRepositoryAndFactory<Index> {

    public IndexRepository() {
        super(IndexRepository.class, Index.class);
    }

    // //////////////////////////////////////

    public Index newIndex(
            final String reference,
            final String name,
            final ApplicationTenancy applicationTenancy) {
        final Index index = newTransientInstance();
        index.setApplicationTenancyPath(applicationTenancy.getPath());
        index.setReference(reference);
        index.setName(name);
        persist(index);
        return index;
    }

    public List<ApplicationTenancy> choices2NewIndex() {
        return applicationTenancyRepository.countryTenanciesForCurrentUser();
    }

    public ApplicationTenancy default2NewIndex() {
        return Dflt.of(choices2NewIndex());
    }

    // //////////////////////////////////////

    public List<Index> allIndices() {
        return allInstances();
    }

    // //////////////////////////////////////

    public Index findIndex(final String reference) {
        return firstMatch("findByReference", "reference", reference);
    }

    public Index findOrCreateIndex(
            final ApplicationTenancy applicationTenancy,
            final String reference,
            final String name) {
        Index index = findIndex(reference);
        if (index == null) {
            index = newIndex(reference, name, applicationTenancy);
        }
        return index;
    }

    // //////////////////////////////////////

    @Inject
    private ApplicationTenancyRepository applicationTenancyRepository;
}
