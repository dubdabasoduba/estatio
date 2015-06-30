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

import java.math.BigDecimal;
import java.util.List;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.InheritanceStrategy;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.InvokeOn;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;
import org.estatio.dom.numerator.Numerator;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findMatchingInvoices", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.invoice.InvoiceForLease " +
                        "WHERE " +
                        "lease == :lease && " +
                        "seller == :seller && " +
                        "buyer == :buyer && " +
                        "paymentMethod == :paymentMethod && " +
                        "status == :status && " +
                        "dueDate == :dueDate"),
        @javax.jdo.annotations.Query(
                name = "findByFixedAssetAndStatus", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.invoice.InvoiceForLease " +
                        "WHERE " +
                        "fixedAsset == :fixedAsset && " +
                        "status == :status " +
                        "ORDER BY invoiceNumber"),
        @javax.jdo.annotations.Query(
                name = "findByFixedAssetAndDueDateAndStatus", language = "JDOQL",
                value = "SELECT FROM org.estatio.dom.invoice.InvoiceForLease " +
                        "WHERE " +
                        "fixedAsset == :fixedAsset && " +
                        "status == :status && " +
                        "dueDate == :dueDate " +
                        "ORDER BY invoiceNumber"),
        @javax.jdo.annotations.Query(
                name = "findByFixedAssetAndDueDate", language = "JDOQL",
                value = "SELECT FROM org.estatio.dom.invoice.InvoiceForLease " +
                        "WHERE " +
                        "fixedAsset == :fixedAsset && " +
                        "dueDate == :dueDate " +
                        "ORDER BY invoiceNumber"),
        @javax.jdo.annotations.Query(
                name = "findByLease", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.invoice.InvoiceForLease " +
                        "WHERE lease == :lease ")
})
@Indices({
        @Index(name = "Invoice_fixedAsset_status_IDX",
                members = { "fixedAsset", "status" }),
        @Index(name = "Invoice_fixedAsset_dueDate_IDX",
                members = { "fixedAsset", "dueDate" }),
        @Index(name = "Invoice_fixedAsset_dueDate_status_IDX",
                members = { "fixedAsset", "dueDate", "status" }),
        @Index(name = "Invoice_Lease_Seller_Buyer_PaymentMethod_DueDate_Status_IDX",
                members = { "lease", "seller", "buyer", "paymentMethod", "dueDate", "status" })
})
@DomainObject(editing = Editing.DISABLED)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
public class InvoiceForLease
        extends Invoice {

    public InvoiceForLease() {
        super("invoiceNumber, collectionNumber, buyer, dueDate, lease, uuid");
    }

    // //////////////////////////////////////

    private Lease lease;

    @javax.jdo.annotations.Column(name = "leaseId", allowsNull = "true")
    @Property(editing = Editing.DISABLED, optionality = Optionality.OPTIONAL)
    public Lease getLease() {
        return lease;
    }

    public void setLease(final Lease lease) {
        this.lease = lease;
    }

    // //////////////////////////////////////

    // //////////////////////////////////////

    @Action(invokeOn = InvokeOn.OBJECT_AND_COLLECTION)
    public InvoiceForLease collect(
            final @ParameterLayout(named = "Are you sure?") Boolean confirm
            ) {
        return doCollect();
    }

    public boolean hideCollect() {
        // only applies to direct debits
        return !getPaymentMethod().isDirectDebit();
    }

    public String disableCollect(Boolean confirm) {
        if (getCollectionNumber() != null) {
            return "Collection number already assigned";
        }
        final Numerator numerator = collectionNumerators.findCollectionNumberNumerator();
        if (numerator == null) {
            return "No 'collection number' numerator found for invoice's property";
        }
        if (getStatus() != InvoiceStatus.APPROVED) {
            return "Must be in status of 'approved'";
        }
        if (getLease() == null) {
            return "No lease related to invoice";
        }
        if (getLease().getPaidBy() == null) {
            return String.format("No mandate assigned to invoice's lease");
        }
        final BankAccount bankAccount = (BankAccount) getLease().getPaidBy().getBankAccount();
        if (!bankAccount.isValidIban()) {
            return "The Iban code is invalid";
        }
        return null;
    }

    // perhaps we should also store the specific bank mandate on the invoice
    // that we want to deduct the money from
    // is this a concept of account then?

    @Programmatic
    public InvoiceForLease doCollect() {
        if (hideCollect()) {
            return this;
        }
        if (disableCollect(true) != null) {
            return this;
        }
        final Numerator numerator = collectionNumerators.findCollectionNumberNumerator();
        setCollectionNumber(numerator.nextIncrementStr());
        return this;
    }

    // //////////////////////////////////////

    @Programmatic
    @Override
    public InvoiceForLease doInvoice(
            final @ParameterLayout(named = "Invoice date") LocalDate invoiceDate) {
        // bulk action, so need these guards
        if (disableInvoice(invoiceDate, true) != null) {
            return this;
        }
        if (!validInvoiceDate(invoiceDate)) {
            warnUser(String.format(
                    "Invoice date %d is invalid for %s becuase it's before the invoice date of the last invoice",
                    invoiceDate.toString(),
                    getContainer().titleOf(this)));
            return this;
        }
        final Numerator numerator = collectionNumerators.findInvoiceNumberNumerator(getFixedAsset());
        setInvoiceNumber(numerator.nextIncrementStr());
        setInvoiceDate(invoiceDate);
        this.setStatus(InvoiceStatus.INVOICED);
        informUser("Assigned " + this.getInvoiceNumber() + " to invoice " + getContainer().titleOf(this));
        return this;
    }

    public String disableInvoice(final LocalDate invoiceDate, Boolean confirm) {
        if (getInvoiceNumber() != null) {
            return "Invoice number already assigned";
        }
        final Numerator numerator = collectionNumerators.findInvoiceNumberNumerator(getFixedAsset());
        if (numerator == null) {
            return "No 'invoice number' numerator found for invoice's property";
        }
        if (getStatus() != InvoiceStatus.APPROVED) {
            return "Must be in status of 'Invoiced'";
        }
        return null;
    }

    // //////////////////////////////////////

    @Programmatic
    boolean validInvoiceDate(LocalDate invoiceDate) {
        if (getDueDate() != null && getDueDate().compareTo(invoiceDate) < 0) {
            return false;
        }
        final Numerator numerator = collectionNumerators.findInvoiceNumberNumerator(getFixedAsset());
        if (numerator != null) {
            final String invoiceNumber = numerator.lastIncrementStr();
            if (invoiceNumber != null) {
                List<InvoiceForLease> result = (List<InvoiceForLease>) invoices.findInvoicesByInvoiceNumber(invoiceNumber);
                if (result.size() > 0) {
                    return result.get(0).getInvoiceDate().compareTo(invoiceDate) <= 0;
                }
            }
        }
        return true;
    }

    // //////////////////////////////////////

    @Override
    public InvoiceItem newItem(
            final Charge charge,
            final @ParameterLayout(named = "Quantity") BigDecimal quantity,
            final @ParameterLayout(named = "Net amount") BigDecimal netAmount,
            final @ParameterLayout(named = "Start date") @Parameter(optionality = Optionality.OPTIONAL) LocalDate startDate,
            final @ParameterLayout(named = "End date") @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate) {
        InvoiceItem invoiceItem = invoiceItems.newInvoiceItem(this, getDueDate());
        invoiceItem.setQuantity(quantity);
        invoiceItem.setCharge(charge);
        invoiceItem.setDescription(charge.getDescription());
        invoiceItem.setTax(charge.getTax());
        invoiceItem.setNetAmount(netAmount);
        invoiceItem.setStartDate(startDate);
        invoiceItem.setEndDate(endDate);
        invoiceItem.verify();
        // TODO: we need to create a new subclass InvoiceForLease but that
        // requires a database change so this is quick fix
        InvoiceItemForLease invoiceItemForLease = (InvoiceItemForLease) invoiceItem;
        invoiceItemForLease.setLease(getLease());
        if (getLease() != null && getLease().getOccupancies() != null && getLease().getOccupancies().first() != null) {
            invoiceItemForLease.setFixedAsset(getLease().getOccupancies().first().getUnit());
        }
        return invoiceItemForLease;
    }

    public BigDecimal default1NewItem() {
        return BigDecimal.ONE;
    }

    public String validateNewItem(
            final Charge charge,
            final BigDecimal quantity,
            final BigDecimal netAmount,
            final LocalDate startDate,
            final LocalDate endDate) {
        if (startDate != null && endDate == null) {
            return "Also enter an end date when using a start date";
        }
        if (ObjectUtils.compare(startDate, endDate) > 0) {
            return "Start date must be before end date";
        }
        return null;
    }

    // //////////////////////////////////////

    private FixedAsset fixedAsset;

    /**
     * Derived from the {@link #getLease() lease}, but safe to persist since
     * business rule states that we never generate invoices for invoice items
     * that relate to different properties.
     * 
     * <p>
     * Another reason for persisting this is that it allows eager validation
     * when attaching additional {@link InvoiceItem}s to an invoice, to check
     * that they relate to the same fixed asset.
     */
    @javax.jdo.annotations.Column(name = "fixedAssetId", allowsNull = "false")
    // for the moment, might be generalized (to the user) in the future
    @Property(editing = Editing.DISABLED, hidden = Where.PARENTED_TABLES)
    @PropertyLayout(named = "Property")
    public FixedAsset getFixedAsset() {
        return fixedAsset;
    }

    public void setFixedAsset(final FixedAsset fixedAsset) {
        this.fixedAsset = fixedAsset;
    }

    // //////////////////////////////////////


    @javax.inject.Inject
    CollectionNumerators collectionNumerators;

    @javax.inject.Inject
    Invoices invoices;

    @javax.inject.Inject
    InvoiceItems invoiceItems;

}
