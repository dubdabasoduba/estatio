package org.estatio.dom.lease;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.VersionStrategy;

import com.google.common.base.Objects;
import com.google.common.collect.Ordering;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Paged;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.util.ObjectContracts;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.WithInterval;
import org.estatio.dom.WithSequence;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.Leases.InvoiceRunType;
import org.estatio.dom.utils.CalendarUtils;
import org.estatio.dom.valuetypes.LocalDateInterval;
import org.estatio.services.clock.ClockService;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Indices({ @javax.jdo.annotations.Index(name = "LEASE_INDEX_IDX", members = { "lease", "type", "sequence" }), @javax.jdo.annotations.Index(name = "LEASE_INDEX2_IDX", members = { "lease", "type", "startDate" }) })
@Bookmarkable(BookmarkPolicy.AS_CHILD)
public class LeaseItem extends EstatioTransactionalObject<LeaseItem> implements /*Comparable<LeaseItem>, */ WithInterval, WithSequence {

    public LeaseItem() {
        super("lease, type, sequence desc");
    }
    
    // //////////////////////////////////////

    private Lease lease;

    @Hidden(where = Where.PARENTED_TABLES)
    @Title(sequence = "1", append = ":")
    @MemberOrder(sequence = "1")
    public Lease getLease() {
        return lease;
    }

    public void setLease(final Lease lease) {
        this.lease = lease;
    }

    public void modifyLease(final Lease lease) {
        Lease currentLease = getLease();
        if (lease == null || lease.equals(currentLease)) {
            return;
        }
        lease.addToItems(this);
    }

    public void clearLease() {
        Lease currentLease = getLease();
        if (currentLease == null) {
            return;
        }
        currentLease.removeFromItems(this);
    }

    // //////////////////////////////////////

    private BigInteger sequence;

    @MemberOrder(sequence = "1")
    @Hidden
    public BigInteger getSequence() {
        return sequence;
    }

    public void setSequence(final BigInteger sequence) {
        this.sequence = sequence;
    }

    @Programmatic
    public LeaseTerm findTermWithSequence(BigInteger sequence) {
        // for (LeaseTerm term : getTerms()) {
        // if (sequence.equals(term.getSequence())) {
        // return term;
        // }
        // }
        // return null;
        // TODO: the code above proved to be very unreliable when using the api.
        // Have to investigate further
        return leaseTerms.findLeaseTermWithSequence(this, sequence);
    }

    // //////////////////////////////////////

    private LeaseItemType type;

    @Title(sequence = "2")
    @MemberOrder(sequence = "2")
    public LeaseItemType getType() {
        return type;
    }

    public void setType(final LeaseItemType type) {
        this.type = type;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate startDate;

    @MemberOrder(sequence = "3")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    @javax.jdo.annotations.Persistent
    private LocalDate endDate;

    @MemberOrder(sequence = "4")
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    @Programmatic
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }

    @Programmatic
    public LocalDate calculatedEndDate() {
        return getEndDate() == null ? getLease().getEndDate() : getEndDate();
    }

    // //////////////////////////////////////

    private InvoicingFrequency invoicingFrequency;

    @MemberOrder(sequence = "12")
    @Hidden(where = Where.PARENTED_TABLES)
    public InvoicingFrequency getInvoicingFrequency() {
        return invoicingFrequency;
    }

    public void setInvoicingFrequency(final InvoicingFrequency invoicingFrequency) {
        this.invoicingFrequency = invoicingFrequency;
    }

    // //////////////////////////////////////

    private PaymentMethod paymentMethod;

    @MemberOrder(sequence = "13")
    @Hidden(where = Where.PARENTED_TABLES)
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(final PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    // //////////////////////////////////////

    private Charge charge;

    @MemberOrder(sequence = "14")
    public Charge getCharge() {
        return charge;
    }

    public void setCharge(final Charge charge) {
        this.charge = charge;
    }

    public List<Charge> choicesCharge() {
        return charges.allCharges();
    }

    // //////////////////////////////////////

    @Disabled
    @Optional
    @MemberOrder(sequence = "15", name = "Current Value") 
    public BigDecimal getTrialValue() {
        LeaseTerm currentTerm = currentTerm(clockService.now());
        if (currentTerm != null)
            return currentTerm.getTrialValue();
        return null;
    }

    // //////////////////////////////////////

    @Disabled
    @Optional
    @MemberOrder(sequence = "16", name = "Current Value") 
       public BigDecimal getApprovedValue() {
        LeaseTerm currentTerm = currentTerm(clockService.now());
        if (currentTerm != null)
            return currentTerm.getApprovedValue();
        return null;
    }

    // //////////////////////////////////////

    @Programmatic
    public LeaseTerm currentTerm(LocalDate date) {
        for (LeaseTerm term : getTerms()) {
            if (term.getInterval().contains(date)) {
                return term;
            }
        }
        return null;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(mappedBy = "leaseItem")
    private SortedSet<LeaseTerm> terms = new TreeSet<LeaseTerm>();

    @Render(Type.EAGERLY)
    @MemberOrder(name = "Terms", sequence = "15")
    @Paged(15)
    public SortedSet<LeaseTerm> getTerms() {
        return terms;
    }

    public void setTerms(final SortedSet<LeaseTerm> terms) {
        this.terms = terms;
    }

    public void addToTerms(final LeaseTerm term) {
        if (term == null || getTerms().contains(term)) {
            return;
        }
        term.clearLeaseItem();
        term.setLeaseItem(this);
        getTerms().add(term);
    }

    public void removeFromTerms(final LeaseTerm term) {
        if (term == null || !getTerms().contains(term)) {
            return;
        }
        term.setLeaseItem(null);
        getTerms().remove(term);
    }

    @Programmatic
    public LeaseTerm findTerm(LocalDate startDate) {
        for (LeaseTerm term : getTerms()) {
            if (startDate.equals(term.getStartDate())) {
                return term;
            }
        }
        return null;
    }

    // //////////////////////////////////////

    @MemberOrder(name = "terms", sequence = "11")
    public LeaseTerm createInitialTerm() {
        LeaseTerm term = leaseTerms.newLeaseTerm(this);
        return term;
    }

    public String disableCreateInitialTerm() {
        return getTerms().size() > 0 ? "Use either 'Verify' or 'Create Next Term' on last term" : null;
    }

    // //////////////////////////////////////

    @Programmatic
    public LeaseTerm createNextTerm(LeaseTerm currentTerm) {
        LeaseTerm term = leaseTerms.newLeaseTerm(this, currentTerm);
        return term;
    }

    // //////////////////////////////////////

    public LeaseItem verify() {
        for (LeaseTerm term : getTerms()) {
            if (term.getPreviousTerm() == null) {
                // since verify is recursive on terms only start on the main
                // term
                term.verify();
            }
        }
        return this;
    }

    // //////////////////////////////////////

    public LeaseItem calculate(@Named("Period Start Date") LocalDate startDate, @Named("Due date") LocalDate dueDate, @Named("Run Type") InvoiceRunType runType) {
        for (LeaseTerm term : getTerms()) {
            term.calculate(startDate, dueDate, runType);
        }
        return this;
    }

    BigDecimal valueForPeriod(InvoicingFrequency frequency, LocalDate periodStartDate, LocalDate dueDate) {
        BigDecimal total = new BigDecimal(0);
        for (LeaseTerm term : getTerms()) {
            total = total.add(term.valueForPeriod(frequency, periodStartDate, dueDate));
        }
        return total;
    }

    // //////////////////////////////////////

//    @Override
//    public String toString() {
//        return Objects.toStringHelper(this).add("lease", getLease() != null ? getLease().getReference() : null).add("type", getType()).add("sequence", getSequence()).toString();
//    }

    // //////////////////////////////////////

//    @Override
//    public int compareTo(LeaseItem other) {
//        //return ORDERING_BY_LEASE.compound(ORDERING_BY_TYPE).compound(ORDERING_BY_SEQUENCE_DESC).compare(this, other);
//        return ObjectContracts.compare(this, other, "lease, type, sequence desc");
//    }

//    public final static Ordering<LeaseItem> ORDERING_BY_LEASE = new Ordering<LeaseItem>() {
//        public int compare(LeaseItem p, LeaseItem q) {
//            return Ordering.natural().nullsFirst().compare(p.getLease(), q.getLease());
//        }
//    };
//
//    public final static Ordering<LeaseItem> ORDERING_BY_TYPE = new Ordering<LeaseItem>() {
//        public int compare(LeaseItem p, LeaseItem q) {
//            return Ordering.<LeaseItemType> natural().nullsFirst().compare(p.getType(), q.getType());
//        }
//    };
//
//    @SuppressWarnings({ "unchecked", "rawtypes" })
//    public final static Ordering<LeaseItem> ORDERING_BY_SEQUENCE_DESC = (Ordering) WithSequence.ORDERING_BY_SEQUENCE_DESC;;
//
//    @SuppressWarnings("unused")
//    private final static Ordering<LeaseItem> ORDERING_BY_START_DATE_DESC = new Ordering<LeaseItem>() {
//        public int compare(LeaseItem p, LeaseItem q) {
//            return Ordering.natural().nullsLast().reverse().compare(p.getStartDate(), q.getStartDate());
//        }
//    };

    // //////////////////////////////////////

    private Charges charges;

    public void injectCharges(Charges charges) {
        this.charges = charges;
    }

    private LeaseTerms leaseTerms;

    public void injectLeaseTerms(LeaseTerms leaseTerms) {
        this.leaseTerms = leaseTerms;
    }

    private ClockService clockService;

    public void injectClockService(final ClockService clockService) {
        this.clockService = clockService;
    }

}
