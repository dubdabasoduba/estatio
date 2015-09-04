package org.estatio.dom.invoice;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.LocalDate;

import org.estatio.dom.valuetypes.LocalDateInterval;

/**
 * Created by jodo on 01/09/15.
 */
public class InvoiceItemHelper {

    private List<? extends InvoiceItem> items;
    private BigDecimal sum;
    private LocalDate minStartDate;
    private LocalDate maxEndDate;
    private boolean initialised;

    public InvoiceItemHelper(List<? extends InvoiceItem> items) {
        this.items = items;
    }

    private void init() {
        if (!initialised) {

            sum = BigDecimal.ZERO;
            for (InvoiceItem item : items) {

                minStartDate = ObjectUtils.min(minStartDate, item.getEffectiveStartDate());
                maxEndDate = ObjectUtils.max(maxEndDate, item.getEffectiveEndDate());

                sum = sum.add(item.getNetAmount());

            }
            initialised = true;
        }
    }

    public LocalDateInterval getMaxInterval() { // returs interval with lowest effective start date and highest effective end date init(); return new LocaDateInterval(minSartDate, maxEndDate); }
        init();
        return new LocalDateInterval(minStartDate, maxEndDate);
    }

    public BigDecimal getSumNetAmount() {
        init();
        return sum;
    }

}