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

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;

import org.estatio.dom.UdoDomainRepositoryAndFactory;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = IndexBase.class)
public class IndexBaseRepository
        extends UdoDomainRepositoryAndFactory<IndexBase> {

    public IndexBaseRepository() {
        super(IndexBaseRepository.class, IndexBase.class);
    }

    // //////////////////////////////////////

    public IndexBase newIndexBase(
            final @ParameterLayout(named = "Index") Index index,
            final @ParameterLayout(named = "Previous Base") IndexBase previousBase,
            final @ParameterLayout(named = "Start Date") LocalDate startDate,
            final @ParameterLayout(named = "Factor") BigDecimal factor) {
        IndexBase indexBase = newTransientInstance();
        indexBase.modifyPrevious(previousBase);
        indexBase.setStartDate(startDate);
        indexBase.setFactor(factor);
        indexBase.setIndex(index);
        persistIfNotAlready(indexBase);
        return indexBase;
    }

    // //////////////////////////////////////

    public IndexBase findByIndexAndDate(final Index index, final LocalDate date) {
        return firstMatch("findByIndexAndDate", "index", index, "date", date);
    }

    // //////////////////////////////////////

    public List<IndexBase> allIndexBases() {
        return allInstances();
    }

}
