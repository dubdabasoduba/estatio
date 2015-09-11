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
package org.estatio.dom.currency;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;

import org.estatio.dom.RegexValidation;
import org.estatio.dom.UdoDomainRepositoryAndFactory;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = Currency.class)
public class CurrencyRepository extends UdoDomainRepositoryAndFactory<Currency> {

    public CurrencyRepository() {
        super(CurrencyRepository.class, Currency.class);
    }

    // //////////////////////////////////////

    public List<Currency> newCurrency(
            final @ParameterLayout(named = "Reference") @Parameter(regexPattern = RegexValidation.REFERENCE) String reference,
            final @ParameterLayout(named = "Name") @Parameter(optionality = Optionality.OPTIONAL) String name) {
        findOrCreateCurrency(reference, name);
        return allCurrencies();
    }

    // //////////////////////////////////////

    public List<Currency> allCurrencies() {
        return allInstances();
    }

    // //////////////////////////////////////

    public Currency findOrCreateCurrency(final String reference, final String name) {
        Currency currency = findCurrency(reference);
        if (currency == null) {
            currency = newTransientInstance();
            currency.setReference(reference);
            currency.setName(name);
            persist(currency);
        }
        return currency;
    }

    public Currency findCurrency(final String reference) {
        return uniqueMatch("findByReference", "reference", reference);
    }

    public List<Currency> autoComplete(final String searchArg) {
        return allMatches("matchByReferenceOrDescription", "searchArg", searchArg);
    }

}
