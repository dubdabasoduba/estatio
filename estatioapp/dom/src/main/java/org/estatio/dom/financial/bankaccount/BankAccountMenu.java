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

package org.estatio.dom.financial.bankaccount;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.party.Party;

@DomainService(menuOrder = "30", repositoryFor = FinancialAccount.class)
@DomainServiceLayout(named = "Accounts")
public class BankAccountMenu extends UdoDomainRepositoryAndFactory<BankAccount> {

    public BankAccountMenu() {
        super(BankAccountMenu.class, BankAccount.class);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_NEITHER)
    public BankAccount newBankAccount(
            final @ParameterLayout(named = "Owner") Party owner,
            final @ParameterLayout(named = "IBAN", typicalLength = JdoColumnLength.BankAccount.IBAN) String iban) {
        return bankAccountRepository.newBankAccount(owner, iban);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    @MemberOrder(sequence = "99")
    public List<BankAccount> allBankAccounts() {
        return bankAccountRepository.allBankAccounts();
    }

    // //////////////////////////////////////

    @Inject
    BankAccountRepository bankAccountRepository;
}
