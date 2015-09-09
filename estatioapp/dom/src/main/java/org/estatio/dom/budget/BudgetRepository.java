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

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.asset.Property;
import org.estatio.dom.valuetypes.LocalDateInterval;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = Budget.class)
public class BudgetRepository extends UdoDomainRepositoryAndFactory<Budget> {

    public BudgetRepository() {
        super(BudgetRepository.class, Budget.class);
    }

    // //////////////////////////////////////

    public Budget newBudget(
            final Property property,
            final LocalDate startDate,
            final LocalDate endDate) {
        Budget budget = newTransientInstance();
        budget.setProperty(property);
        budget.setStartDate(startDate);
        budget.setEndDate(endDate);
        persistIfNotAlready(budget);

        return budget;
    }

    public String validateNewBudget(
            final Property property,
            final LocalDate startDate,
            final LocalDate endDate) {
        if (!new LocalDateInterval(startDate, endDate).isValid()) {
            return "End date can not be before start date";
        }

        for (Budget budget : this.findBudgetByProperty(property)) {
            if (budget.getInterval().overlaps(new LocalDateInterval(startDate, endDate))) {
                return "A new budget cannot overlap an existing budget.";
            }
        }

        return null;
    }

    // //////////////////////////////////////

    public List<Budget> allBudgets() {
        return allInstances();
    }

    // //////////////////////////////////////

    public List<Budget> findBudgetByProperty(Property property) {
        return allMatches("findByProperty", "property", property);
    }

}
