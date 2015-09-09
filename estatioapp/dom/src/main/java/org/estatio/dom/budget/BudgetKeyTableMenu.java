/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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

package org.estatio.dom.budget;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.asset.Property;

@DomainService(repositoryFor = BudgetKeyTable.class)
@DomainServiceLayout(menuBar = DomainServiceLayout.MenuBar.PRIMARY, named = "Budgets")
public class BudgetKeyTableMenu extends UdoDomainRepositoryAndFactory<BudgetKeyTable> {

    public BudgetKeyTableMenu() {
        super(BudgetKeyTableMenu.class, BudgetKeyTable.class);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public BudgetKeyTable newBudgetKeyTable(
            final @ParameterLayout(named = "Property") Property property,
            final @ParameterLayout(named = "Name") String name,
            final @ParameterLayout(named = "Start Date") LocalDate startDate,
            final @ParameterLayout(named = "End Date") LocalDate endDate,
            final @ParameterLayout(named = "Foundation Value Type") BudgetFoundationValueType foundationValueType,
            final @ParameterLayout(named = "Key Value Method") BudgetKeyValueMethod keyValueMethod,
            final @ParameterLayout(named = "Number Of Digits") Integer numberOfDigits) {
        return budgetKeyTableRepository.newBudgetKeyTable(property, name, startDate, endDate, foundationValueType, keyValueMethod, numberOfDigits);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    public List<BudgetKeyTable> allBudgetKeyTables() {
        return budgetKeyTableRepository.allBudgetKeyTables();
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_NEITHER)
    public List<BudgetKeyTable> findBudgetKeyTableByProperty(Property property) {
        return budgetKeyTableRepository.findBudgetKeyTableByProperty(property);
    }

    @Inject
    BudgetKeyTableRepository budgetKeyTableRepository;
}
