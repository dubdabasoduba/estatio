package org.estatio.dom.invoice;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

import org.estatio.dom.charge.Charge;

public class InvoiceForTesting extends Invoice {
    @Override public Invoice doInvoice(final LocalDate invoiceDate) {
        return null;
    }

    @Override public InvoiceItem newItem(final Charge charge, final BigDecimal quantity, final BigDecimal netAmount, final LocalDate startDate, final LocalDate endDate) {
        return null;
    }
}
