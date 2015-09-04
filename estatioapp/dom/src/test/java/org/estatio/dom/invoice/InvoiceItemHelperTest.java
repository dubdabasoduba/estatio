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
import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.estatio.dom.lease.invoicing.InvoiceItemForLease;
import org.estatio.dom.valuetypes.LocalDateInterval;

import static org.assertj.core.api.Assertions.assertThat;

public class InvoiceItemHelperTest {


    List<InvoiceItemForLease> items = new ArrayList<>();
    InvoiceItemForLease item1 = new InvoiceItemForLease();
    InvoiceItemForLease item2 = new InvoiceItemForLease();
    InvoiceItemForLease item3 = new InvoiceItemForLease();
    InvoiceItemForLease item4 = new InvoiceItemForLease();
    InvoiceItemForLease item5 = new InvoiceItemForLease();
    InvoiceItemForLease item6 = new InvoiceItemForLease();

    @Before
    public void setup() {

        //given
        item1.setEffectiveStartDate(new LocalDate(2015, 1, 1));
        item1.setEffectiveEndDate(new LocalDate(2015, 7, 31));
        item1.setNetAmount(BigDecimal.ZERO);

        item2.setEffectiveStartDate(new LocalDate(2015, 1, 1));
        item2.setEffectiveEndDate(new LocalDate(2015, 8, 31));
        item2.setNetAmount(BigDecimal.ZERO);

        item3.setEffectiveStartDate(new LocalDate(2015, 2, 1));
        item3.setEffectiveEndDate(new LocalDate(2015, 8, 31));
        item3.setNetAmount(BigDecimal.ZERO);

        item4.setEffectiveStartDate(new LocalDate(2014, 12, 31));
        item4.setEffectiveEndDate(new LocalDate(2015, 8, 31));
        item4.setNetAmount(BigDecimal.ZERO);

        item5.setEffectiveStartDate(new LocalDate(2014, 12, 31));
        item5.setEffectiveEndDate(new LocalDate(2015, 9, 1));
        item5.setNetAmount(BigDecimal.ZERO);

        item6.setEffectiveStartDate(new LocalDate(2015, 10, 1));
        item6.setEffectiveEndDate(new LocalDate(2015, 10, 31));
        item6.setNetAmount(BigDecimal.ZERO);
    }


    @Test
    public void happyCase() {

        //when overlapping enddate
        items.add(item1);
        items.add(item2);
        InvoiceItemHelper invoiceItemHelper = new InvoiceItemHelper(items);
        //then
        assertThat(invoiceItemHelper.getMaxInterval()).isEqualTo(new LocalDateInterval(new LocalDate(2015, 1, 1), new LocalDate(2015, 8, 31)));

        //when overlapping startdate
        items.add(item3);
        InvoiceItemHelper invoiceItemHelper2 = new InvoiceItemHelper(items);
        //then
        assertThat(invoiceItemHelper2.getMaxInterval()).isEqualTo(new LocalDateInterval(new LocalDate(2015, 1, 1), new LocalDate(2015, 8, 31)));

        //when new min enddate
        items.add(item4);
        InvoiceItemHelper invoiceItemHelper3 = new InvoiceItemHelper(items);
        //then
        assertThat(invoiceItemHelper3.getMaxInterval()).isEqualTo(new LocalDateInterval(new LocalDate(2014, 12, 31), new LocalDate(2015, 8, 31)));

        //when new max startdate
        items.add(item5);
        InvoiceItemHelper invoiceItemHelper4 = new InvoiceItemHelper(items);
        //then
        assertThat(invoiceItemHelper4.getMaxInterval()).isEqualTo(new LocalDateInterval(new LocalDate(2014, 12, 31), new LocalDate(2015, 9, 1)));

        //when new startdate after max endate
        items.add(item6);
        InvoiceItemHelper invoiceItemHelper5 = new InvoiceItemHelper(items);
        //then
        assertThat(invoiceItemHelper5.getMaxInterval()).isEqualTo(new LocalDateInterval(new LocalDate(2014, 12, 31), new LocalDate(2015, 10, 31)));

    }




}