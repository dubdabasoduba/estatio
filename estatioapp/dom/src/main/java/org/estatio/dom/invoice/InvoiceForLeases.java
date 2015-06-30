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

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.security.app.user.MeService;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.apptenancy.EstatioApplicationTenancies;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.currency.Currency;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.invoicing.InvoiceCalculationParameters;
import org.estatio.dom.party.Party;
import org.estatio.services.settings.EstatioSettingsService;

@DomainService(repositoryFor = InvoiceForLease.class)
@DomainServiceLayout(
        named = "Invoices",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "50.4")
public class InvoiceForLeases extends UdoDomainRepositoryAndFactory<InvoiceForLease> {

    public InvoiceForLeases() {
        super(InvoiceForLeases.class, InvoiceForLease.class);
    }

    // //////////////////////////////////////

    @Programmatic
    public List<InvoiceForLease> findByLease(final Lease lease) {
        return allMatches("findByLease",
                "lease", lease);
    }

    @Programmatic
    public List<InvoiceForLease> findInvoices(
            final FixedAsset fixedAsset,
            final InvoiceStatus status) {
        return allMatches("findByFixedAssetAndStatus",
                "fixedAsset", fixedAsset,
                "status", status);
    }

    @Programmatic
    public List<InvoiceForLease> findInvoices(
            final FixedAsset fixedAsset,
            final LocalDate dueDate) {
        return allMatches("findByFixedAssetAndDueDate",
                "fixedAsset", fixedAsset,
                "dueDate", dueDate);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "2")
    public List<InvoiceForLease> findInvoices(
            final FixedAsset fixedAsset,
            final @ParameterLayout(named = "Due Date") @Parameter(optionality = Optionality.OPTIONAL) LocalDate dueDate,
            final @Parameter(optionality = Optionality.OPTIONAL) InvoiceStatus status) {
        if (status == null) {
            return findInvoices(fixedAsset, dueDate);
        } else if (dueDate == null) {
            return findInvoices(fixedAsset, status);
        } else {
            return allMatches("findByFixedAssetAndDueDateAndStatus",
                    "fixedAsset", fixedAsset,
                    "dueDate", dueDate,
                    "status", status);
        }
    }

    // //////////////////////////////////////

    @NotContributed
    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public InvoiceForLease newInvoiceForLease(
            final @ParameterLayout(named = "Lease") Lease lease,
            final @ParameterLayout(named = "Due date") LocalDate dueDate,
            final PaymentMethod paymentMethod,
            final Currency currency,
            final ApplicationTenancy applicationTenancy) {
        return newInvoice(applicationTenancy,
                lease.getPrimaryParty(),
                lease.getSecondaryParty(),
                paymentMethod,
                currency,
                dueDate,
                lease, null);
    }

    public List<ApplicationTenancy> choices4NewInvoiceForLease() {
        return estatioApplicationTenancies.selfOrChildrenOf(meService.me().getTenancy());
    }


    // //////////////////////////////////////

    @Programmatic
    public InvoiceForLease newInvoice(
            final ApplicationTenancy applicationTenancy,
            final @ParameterLayout(named = "Seller") Party seller,
            final @ParameterLayout(named = "Buyer") Party buyer,
            final PaymentMethod paymentMethod,
            final Currency currency,
            final @ParameterLayout(named = "Due date") LocalDate dueDate,
            final Lease lease,
            final String interactionId
    ) {
        InvoiceForLease invoiceForLease = newTransientInstance();
        invoiceForLease.setApplicationTenancyPath(applicationTenancy.getPath());
        invoiceForLease.setBuyer(buyer);
        invoiceForLease.setSeller(seller);
        invoiceForLease.setPaymentMethod(paymentMethod);
        invoiceForLease.setStatus(InvoiceStatus.NEW);
        invoiceForLease.setCurrency(currency);
        invoiceForLease.setLease(lease);
        invoiceForLease.setDueDate(dueDate);
        invoiceForLease.setUuid(java.util.UUID.randomUUID().toString());
        invoiceForLease.setRunId(interactionId);

        // copy down form the agreement, we require all invoice items to relate
        // back to this (root) fixed asset
        invoiceForLease.setPaidBy(lease.getPaidBy());
        invoiceForLease.setFixedAsset(lease.getProperty());

        persistIfNotAlready(invoiceForLease);
        getContainer().flush();
        return invoiceForLease;
    }

    @Programmatic
    public InvoiceForLease findOrCreateMatchingInvoice(
            final ApplicationTenancy applicationTenancy,
            final PaymentMethod paymentMethod,
            final Lease lease,
            final InvoiceStatus invoiceStatus,
            final LocalDate dueDate,
            final String interactionId) {
        Party buyer = lease.getSecondaryParty();
        Party seller = lease.getPrimaryParty();
        return findOrCreateMatchingInvoice(
                applicationTenancy, seller, buyer, paymentMethod, lease, invoiceStatus, dueDate, interactionId);
    }

    @Programmatic
    public InvoiceForLease findMatchingInvoice(
            final Party seller,
            final Party buyer,
            final PaymentMethod paymentMethod,
            final Lease lease,
            final InvoiceStatus invoiceStatus,
            final LocalDate dueDate) {
        final List<InvoiceForLease> invoiceForLeases = findMatchingInvoices(
                seller, buyer, paymentMethod, lease, invoiceStatus, dueDate);
        if (invoiceForLeases == null || invoiceForLeases.size() == 0) {
            return null;
        }
        return invoiceForLeases.get(0);
    }

    @Programmatic
    public InvoiceForLease findOrCreateMatchingInvoice(
            final ApplicationTenancy applicationTenancy,
            final Party seller,
            final Party buyer,
            final PaymentMethod paymentMethod,
            final Lease lease,
            final InvoiceStatus invoiceStatus,
            final LocalDate dueDate,
            final String interactionId) {
        final List<InvoiceForLease> invoiceForLeases = findMatchingInvoices(
                seller, buyer, paymentMethod, lease, invoiceStatus, dueDate);
        if (invoiceForLeases == null || invoiceForLeases.size() == 0) {
            return newInvoice(applicationTenancy, seller, buyer, paymentMethod, settings.systemCurrency(), dueDate, lease, interactionId);
        }
        return invoiceForLeases.get(0);
    }

    @Programmatic
    public List<InvoiceForLease> findMatchingInvoices(
            final Party seller,
            final Party buyer,
            final PaymentMethod paymentMethod,
            final Lease lease,
            final InvoiceStatus invoiceStatus,
            final LocalDate dueDate) {
        return allMatches(
                "findMatchingInvoices",
                "seller", seller,
                "buyer", buyer,
                "paymentMethod", paymentMethod,
                "lease", lease,
                "status", invoiceStatus,
                "dueDate", dueDate);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    @MemberOrder(sequence = "98")
    public List<InvoiceForLease> allInvoiceForLeases() {
        return allInstances();
    }

    // //////////////////////////////////////

    @Programmatic
    public void removeRuns(InvoiceCalculationParameters parameters) {
        List<InvoiceForLease> invoiceForLeases = findInvoices(parameters.property(), parameters.invoiceDueDate(), InvoiceStatus.NEW);
        for (InvoiceForLease invoiceForLease : invoiceForLeases) {
            invoiceForLease.remove();
        }
    }

    // //////////////////////////////////////

    @javax.inject.Inject
    private EstatioSettingsService settings;

    @javax.inject.Inject
    private EstatioApplicationTenancies estatioApplicationTenancies;

    @javax.inject.Inject
    private MeService meService;



}
