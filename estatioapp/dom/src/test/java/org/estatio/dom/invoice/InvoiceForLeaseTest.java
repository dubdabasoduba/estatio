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

import java.math.BigInteger;
import java.util.Arrays;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Ignoring;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.Property;
import org.estatio.dom.bankmandate.BankMandate;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.numerator.Numerator;
import org.estatio.services.clock.ClockService;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class InvoiceForLeaseTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    InvoiceForLease invoiceForLease;

    Numerator numerator;

    @Mock
    Invoices mockInvoices;

    @Mock
    CollectionNumerators mockCollectionNumerators;

    @Mock
    ClockService mockClockService;

    @Mock
    Lease lease;

    @Mock
    Property invoiceProperty;

    @Mock
    @Ignoring
    DomainObjectContainer mockContainer;

    @Before
    public void setUp() throws Exception {
        numerator = new Numerator();
        numerator.setFormat("XXX-%05d");
        numerator.setLastIncrement(BigInteger.TEN);

        context.checking(new Expectations() {
            {
                allowing(mockClockService).now();
                will(returnValue(LocalDate.now()));
            }
        });

    }

    void allowingMockInvoicesToReturnNumerator(final Numerator numerator) {
        context.checking(new Expectations() {
            {
                allowing(mockCollectionNumerators).findInvoiceNumberNumerator(with(any(Property.class)));
                will(returnValue(numerator));
            }
        });
    }

    void allowingMockInvoicesToReturnCollectionNumerator(final Numerator numerator) {
        context.checking(new Expectations() {
            {
                allowing(mockCollectionNumerators).findCollectionNumberNumerator();
                will(returnValue(numerator));
            }
        });
    }

    void allowingMockInvoicesToReturnInvoice(final String invoiceNumber, final LocalDate invoiceDate) {
        context.checking(new Expectations() {
            {
                allowing(mockInvoices).findInvoicesByInvoiceNumber(with(any(String.class)));
                will(returnValue(Arrays.asList(new InvoiceForLease() {
                    @Override
                    public String getInvoiceNumber() {
                        return invoiceNumber;
                    };

                    @Override
                    public LocalDate getInvoiceDate() {
                        return invoiceDate;
                    };
                })));
            }
        });
    }

    InvoiceForLease createInvoice(final FixedAsset fixedAsset, final InvoiceStatus invoiceStatus) {
        final InvoiceForLease invoiceForLease = new InvoiceForLease() {
            @Override
            public FixedAsset getFixedAsset() {
                return fixedAsset;
            }
        };
        invoiceForLease.setStatus(invoiceStatus);
        invoiceForLease.setContainer(mockContainer);
        invoiceForLease.invoices = mockInvoices;
        invoiceForLease.collectionNumerators = mockCollectionNumerators;
        invoiceForLease.injectClockService(mockClockService);
        return invoiceForLease;
    }

    public static class AssignInvoiceNumber extends InvoiceForLeaseTest {

        @Test
        public void happyCase_whenNoInvoiceNumberPreviouslyAssigned() {
            allowingMockInvoicesToReturnNumerator(numerator);
            allowingMockInvoicesToReturnInvoice("XXX-00010", new LocalDate(2012, 1, 1));
            invoiceForLease = createInvoice(invoiceProperty, InvoiceStatus.APPROVED);

            assertThat(invoiceForLease.disableInvoice(null, true), is(nullValue()));
            invoiceForLease.doInvoice(mockClockService.now());

            assertThat(invoiceForLease.getInvoiceNumber(), is("XXX-00011"));
            assertThat(invoiceForLease.getStatus(), is(InvoiceStatus.INVOICED));
        }

        @Test
        public void whenInvoiceNumberAlreadyAssigned() {
            allowingMockInvoicesToReturnNumerator(numerator);
            invoiceForLease = createInvoice(invoiceProperty, InvoiceStatus.APPROVED);
            invoiceForLease.setInvoiceNumber("SOME-INVOICE-NUMBER");

            assertThat(invoiceForLease.disableInvoice(null, true), is("Invoice number already assigned"));
            invoiceForLease.doInvoice(mockClockService.now());

            assertThat(invoiceForLease.getInvoiceNumber(), is("SOME-INVOICE-NUMBER"));
        }

        @Test
        public void whenNoProperty() {

            allowingMockInvoicesToReturnNumerator(null);
            invoiceForLease = createInvoice(invoiceProperty, InvoiceStatus.APPROVED);

            assertThat(invoiceForLease.disableInvoice(null, true), is("No 'invoice number' numerator found for invoice's property"));

            invoiceForLease.doInvoice(mockClockService.now());
            assertThat(invoiceForLease.getInvoiceNumber(), is(nullValue()));
        }

        @Test
        public void whenNotInCollectedState() {

            allowingMockInvoicesToReturnNumerator(null);
            invoiceForLease = createInvoice(invoiceProperty, InvoiceStatus.APPROVED);

            assertThat(invoiceForLease.disableInvoice(null, true), is("No 'invoice number' numerator found for invoice's property"));

            invoiceForLease.doInvoice(mockClockService.now());
            assertThat(invoiceForLease.getInvoiceNumber(), is(nullValue()));
        }

    }

    public static class Collect extends InvoiceForLeaseTest {

        private InvoiceForLease createInvoice(final Property property, final PaymentMethod paymentMethod, final InvoiceStatus statusForTest) {
            final InvoiceForLease invoiceForLease = new InvoiceForLease() {

                @Override
                public PaymentMethod getPaymentMethod() {
                    return paymentMethod;
                }

                @Override
                public InvoiceStatus getStatus() {
                    return statusForTest;
                }
            };
            invoiceForLease.setContainer(mockContainer);
            invoiceForLease.invoices = mockInvoices;
            invoiceForLease.collectionNumerators = mockCollectionNumerators;
            return invoiceForLease;
        }

        @Test
        public void happyCase_directDebit_and_collected_andWhenNoInvoiceNumberPreviouslyAssigned() {
            allowingMockInvoicesToReturnCollectionNumerator(numerator);
            context.checking(new Expectations() {
                {
                    allowing(lease).getPaidBy();
                    will(returnValue(new BankMandate() {
                        public org.estatio.dom.financial.FinancialAccount getBankAccount() {
                            return new BankAccount() {
                                public boolean isValidIban() {
                                    return true;
                                };
                            };
                        };
                    }));
                }
            });

            invoiceForLease = createInvoice(invoiceProperty, PaymentMethod.DIRECT_DEBIT, InvoiceStatus.APPROVED);
            invoiceForLease.setLease(lease);

            assertThat(invoiceForLease.hideCollect(), is(false));
            assertNull(invoiceForLease.disableCollect(true));
            invoiceForLease.doCollect();

            assertThat(invoiceForLease.getCollectionNumber(), is("XXX-00011"));
        }

        @Test
        public void whenNoMandateAssigned() {
            allowingMockInvoicesToReturnCollectionNumerator(numerator);

            invoiceForLease = createInvoice(invoiceProperty, PaymentMethod.DIRECT_DEBIT, InvoiceStatus.APPROVED);
            invoiceForLease.setLease(new Lease());

            assertThat(invoiceForLease.hideCollect(), is(false));
            assertThat(invoiceForLease.disableCollect(true), is("No mandate assigned to invoice's lease"));
            invoiceForLease.doCollect();
            assertNull(invoiceForLease.getCollectionNumber());
        }

        @Test
        public void whenInvoiceNumberAlreadyAssigned() {
            allowingMockInvoicesToReturnNumerator(numerator);

            invoiceForLease = createInvoice(invoiceProperty, PaymentMethod.DIRECT_DEBIT, InvoiceStatus.APPROVED);

            invoiceForLease.setCollectionNumber("SOME-COLLECTION-NUMBER");

            assertThat(invoiceForLease.hideCollect(), is(false));
            assertThat(invoiceForLease.disableCollect(true), is("Collection number already assigned"));
            invoiceForLease.doCollect();

            assertThat(invoiceForLease.getCollectionNumber(), is("SOME-COLLECTION-NUMBER"));
        }

        @Test
        public void whenNoProperty() {
            allowingMockInvoicesToReturnCollectionNumerator(null);

            invoiceForLease = createInvoice(invoiceProperty, PaymentMethod.DIRECT_DEBIT, InvoiceStatus.APPROVED);

            assertThat(invoiceForLease.hideCollect(), is(false));
            assertThat(invoiceForLease.disableCollect(true), is("No 'collection number' numerator found for invoice's property"));

            invoiceForLease.doCollect();
            assertThat(invoiceForLease.getCollectionNumber(), is(nullValue()));
        }

        @Test
        public void whenNotDirectDebit() {
            allowingMockInvoicesToReturnCollectionNumerator(numerator);

            invoiceForLease = createInvoice(invoiceProperty, PaymentMethod.BANK_TRANSFER, InvoiceStatus.APPROVED);
            invoiceForLease.setLease(new Lease());

            assertThat(invoiceForLease.hideCollect(), is(true));
            assertThat(invoiceForLease.disableCollect(true), is("No mandate assigned to invoice's lease"));

            invoiceForLease.doCollect();

            assertThat(invoiceForLease.getCollectionNumber(), is(nullValue()));
        }

        @Test
        public void whenNotCollected() {
            // given
            allowingMockInvoicesToReturnCollectionNumerator(numerator);

            // when
            invoiceForLease = createInvoice(invoiceProperty, PaymentMethod.DIRECT_DEBIT, InvoiceStatus.NEW);

            // then
            assertThat(invoiceForLease.hideCollect(), is(false));
            assertThat(invoiceForLease.disableCollect(true), is("Must be in status of 'approved'"));

            // and
            invoiceForLease.doCollect();

            // then
            assertThat(invoiceForLease.getCollectionNumber(), is(nullValue()));
        }

    }

//    public static class CompareTo extends ComparableContractTest_compareTo<InvoiceForLease> {
//
//        @SuppressWarnings("unchecked")
//        @Override
//        protected List<List<InvoiceForLease>> orderedTuples() {
//            return listOf(listOf(
//                    newInvoice(null),
//                    newInvoice("0000123"),
//                    newInvoice("0000123"),
//                    newInvoice("0000124")));
//        }
//
//        private InvoiceForLease newInvoice(String number) {
//            final InvoiceForLease inv = new InvoiceForLease();
//            inv.setInvoiceNumber(number);
//            return inv;
//        }
//
//    }

    public static class ValidInvoiceDate extends InvoiceForLeaseTest {

        @Before
        public void setUp() throws Exception {
            invoiceForLease = new InvoiceForLease();
            invoiceForLease.setDueDate(new LocalDate(2012, 2, 2));
            invoiceForLease.invoices = mockInvoices;
            invoiceForLease.collectionNumerators = mockCollectionNumerators;
            invoiceForLease.setFixedAsset(invoiceProperty);
        }

        @Test
        public void invoiceDateIsAfterDueDate() {
            assertFalse(invoiceForLease.validInvoiceDate(new LocalDate(2012, 2, 3)));
        }

        @Test
        public void invoiceDateIsBeforeDueDate() {
            // given
            allowingMockInvoicesToReturnNumerator(numerator);
            allowingMockInvoicesToReturnInvoice("XXX-0010", new LocalDate(2012, 1, 1));

            // when,then
            assertTrue(invoiceForLease.validInvoiceDate(new LocalDate(2012, 2, 1)));
        }

    }
}