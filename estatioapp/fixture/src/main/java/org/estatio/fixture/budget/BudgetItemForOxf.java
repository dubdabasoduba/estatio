/*
 * Copyright 2015 Yodo Int. Projects and Consultancy
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.estatio.fixture.budget;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.estatio.dom.asset.PropertyMenu;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.budget.Budget;
import org.estatio.dom.budget.BudgetCostGroup;
import org.estatio.dom.budget.BudgetKeyTable;
import org.estatio.dom.budget.BudgetKeyTableRepository;
import org.estatio.dom.budget.BudgetRepository;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.dom.currency.CurrencyRepository;
import org.estatio.dom.currency.Currency;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.charge.ChargeRefData;
import org.estatio.fixture.currency.CurrenciesRefData;

/**
 * Created by jodo on 22/04/15.
 */
public class BudgetItemForOxf extends BudgetItemAbstact {

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        if (isExecutePrereqs()) {
            executionContext.executeChild(this, new PropertyForOxfGb());
            executionContext.executeChild(this, new BudgetKeyTablesForOxf());
            executionContext.executeChild(this, new CurrenciesRefData());
            executionContext.executeChild(this, new ChargeRefData());
            executionContext.executeChild(this, new BudgetForOxf());
        }

        // exec
        Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
        Budget budget = budgetRepository.findBudgetByProperty(property).get(0);
        BudgetKeyTable budgetKeyTable = budgetKeyTableRepository.findBudgetKeyTableByName(BudgetKeyTablesForOxf.NAME2);
        final BigDecimal VALUE = new BigDecimal(40000);
        final Currency currency = currencyRepository.findCurrency(CurrenciesRefData.EUR);
        final Charge charge = chargeRepository.findByReference(ChargeRefData.IT_SERVICE_CHARGE);
        final BudgetCostGroup budgetCostGroup = BudgetCostGroup.UTENZE;

        createBudgetItem(
                budget,
                budgetKeyTable,
                VALUE,
                currency,
                charge,
                budgetCostGroup,
                executionContext);
    }

    @Inject
    BudgetRepository budgetRepository;

    @Inject
    BudgetKeyTableRepository budgetKeyTableRepository;

    @Inject
    CurrencyRepository currencyRepository;

    @Inject
    ChargeRepository chargeRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    PropertyMenu propertyMenu;

}
