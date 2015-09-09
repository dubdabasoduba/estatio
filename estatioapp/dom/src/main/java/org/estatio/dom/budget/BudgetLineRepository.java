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

import java.math.BigDecimal;
import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.UdoDomainRepositoryAndFactory;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = BudgetLine.class)
public class BudgetLineRepository extends UdoDomainRepositoryAndFactory<BudgetLine> {

    public BudgetLineRepository() {
        super(BudgetLineRepository.class, BudgetLine.class);
    }

    // //////////////////////////////////////

    public BudgetLine newBudgetLine(
            final BigDecimal value,
            final BudgetItem budgetItem,
            final BudgetKeyItem budgetKeyItem) {
        BudgetLine budgetLine = newTransientInstance();
        budgetLine.setValue(value);
        budgetLine.setBudgetItem(budgetItem);
        budgetLine.setBudgetKeyItem(budgetKeyItem);

        persistIfNotAlready(budgetLine);

        return budgetLine;
    }

    // //////////////////////////////////////

    public List<BudgetLine> allBudgetLines() {
        return allInstances();
    }

    // //////////////////////////////////////

    public List<BudgetLine> findByBudgetItem(final BudgetItem budgetItem) {
        return allMatches("findByBudgetItem", "budgetItem", budgetItem);
    }

}
