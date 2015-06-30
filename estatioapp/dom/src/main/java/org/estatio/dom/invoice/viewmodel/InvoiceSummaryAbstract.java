/*
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
package org.estatio.dom.invoice.viewmodel;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;

import org.estatio.app.EstatioViewModel;
import org.estatio.dom.invoice.InvoiceForLease;
import org.estatio.dom.invoice.InvoiceForLeases;
import org.estatio.dom.invoice.Invoices;

public abstract class InvoiceSummaryAbstract extends EstatioViewModel {

    public Object approveAll() {
        for (InvoiceForLease invoiceForLease : getInvoices()) {
            invoiceForLease.doApprove();
        }
        return this;
    }

    public Object collectAll(
            final @Named("Are you sure?") Boolean confirm
            ) {
        for (InvoiceForLease invoiceForLease : getInvoices()) {
            invoiceForLease.doCollect();
        }
        return this;
    }

    public Object invoiceAll(
            final @Named("Invoice Date") LocalDate invoiceDate,
            final @Named("Are you sure?") Boolean confirm
            ) {
        for (InvoiceForLease invoiceForLease : getInvoices()) {
            invoiceForLease.doInvoice(invoiceDate);
        }
        return this;
    }

    public LocalDate default0InvoiceAll() {
        return getClockService().now();
    }

    public Object removeAll(final @Named("Confirm") Boolean confirm) {
        for (InvoiceForLease invoiceForLease : getInvoices()) {
            invoiceForLease.remove();
        }
        return this;
    }

    @Prototype
    public Object zapAll(final @Named("Confirm") Boolean confirm) {
        for (InvoiceForLease invoiceForLease : getInvoices()) {
            invoiceForLease.doRemove();
        }
        return this;
    }

    @Render(Type.EAGERLY)
    public abstract List<InvoiceForLease> getInvoices();

    // //////////////////////////////////////

    @Inject
    protected Invoices invoicesService;

    @Inject
    protected InvoiceForLeases invoiceForLeasesService;

}
