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
package org.estatio.dom.invoice;

import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NotInServiceMenu;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.party.Party;
import org.estatio.dom.utils.StringUtils;

@DomainService(repositoryFor = Invoice.class)
@DomainServiceLayout(
        named = "Invoices",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "50.4")
public class Invoices extends UdoDomainRepositoryAndFactory<Invoice> {

    public Invoices() {
        super(Invoices.class, Invoice.class);
    }

    // //////////////////////////////////////

    @NotInServiceMenu
    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(named = "Invoices")
    public List<? extends Invoice> findInvoices(final Party party) {
        return allMatches("findByBuyer",
                "buyer", party);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "3")
    public List<? extends Invoice> findInvoicesByInvoiceNumber(
            final @ParameterLayout(named = "Invoice number") String invoiceNumber) {
        return allMatches("findByInvoiceNumber",
                "invoiceNumber", StringUtils.wildcardToCaseInsensitiveRegex(invoiceNumber));
    }

    // //////////////////////////////////////

    @Programmatic
    public List<? extends Invoice> findInvoicesByRunId(final String runId) {
        return allMatches("findByRunId",
                "runId", runId);
    }

    @Programmatic
    public List<? extends Invoice> findInvoices(
            final InvoiceStatus status) {
        return allMatches("findByStatus",
                "status", status);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    @MemberOrder(sequence = "98")
    public List<? extends Invoice> allInvoices() {
        return allInstances();
    }

}
