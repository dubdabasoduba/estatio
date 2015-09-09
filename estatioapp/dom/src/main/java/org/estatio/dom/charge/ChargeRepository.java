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
package org.estatio.dom.charge;

import java.util.List;

import javax.inject.Inject;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.RegexValidation;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.apptenancy.ApplicationTenancyRepository;
import org.estatio.dom.geography.Country;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.valuetypes.ApplicationTenancyLevel;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = Charge.class)
public class ChargeRepository extends UdoDomainRepositoryAndFactory<Charge> {

    public ChargeRepository() {
        super(ChargeRepository.class, Charge.class);
    }

    // //////////////////////////////////////

    public Charge newCharge(
            final ApplicationTenancy applicationTenancy,
            final @ParameterLayout(named = "Reference") @Parameter(regexPattern = RegexValidation.REFERENCE) String reference,
            final @ParameterLayout(named = "Name") String name,
            final @ParameterLayout(named = "Description") String description,
            final Tax tax,
            final ChargeGroup chargeGroup) {
        Charge charge = findByReference(reference);
        if (charge == null) {
            charge = newTransientInstance();
            charge.setReference(reference);
            persist(charge);
        }
        charge.setApplicationTenancyPath(applicationTenancy.getPath());
        charge.setName(name);
        charge.setDescription(description);
        charge.setTax(tax);
        charge.setGroup(chargeGroup);
        return charge;
    }

    // //////////////////////////////////////

    public List<Charge> allCharges() {
        return allInstances();
    }

    // //////////////////////////////////////

    public List<Charge> findChargesForCountry(final Country country) {
        final String countryPath = "/" + country.getAlpha2Code();
        return chargesForCountry(countryPath);
    }

    // //////////////////////////////////////

    public List<Charge> chargesForCountry(final ApplicationTenancy countryOrLowerLevel) {
        final ApplicationTenancyLevel level = ApplicationTenancyLevel.of(countryOrLowerLevel);
        final String countryPath = level.getCountryPath();
        return chargesForCountry(countryPath);
    }

    public List<Charge> chargesForCountry(final String applicationTenancyPath) {

        // assert the path (must not be root)
        final String countryPath = ApplicationTenancyLevel.of(applicationTenancyPath).getCountryPath();

        final List<Charge> charges = allInstances();
        return Lists.newArrayList(
                Iterables.filter(charges, new Predicate<Charge>() {
                    @Override
                    public boolean apply(final Charge charge) {
                        final ApplicationTenancyLevel chargeLevel = ApplicationTenancyLevel.of(charge);
                        return chargeLevel.isRoot() ||
                                chargeLevel.isCountry() && chargeLevel.getPath().equalsIgnoreCase(countryPath);
                    }
                })
        );
    }

    // //////////////////////////////////////

    public Charge findByReference(
            final String reference) {
        return firstMatch(
                "findByReference",
                "reference", reference);
    }

    // //////////////////////////////////////

    @Inject
    private ApplicationTenancyRepository applicationTenancyRepository;

}
