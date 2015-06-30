package org.estatio.dom.invoice;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.lease.Lease;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class InvoiceForLeaseContributions {

    @Action(semantics = SemanticsOf.SAFE)
    public List<InvoiceForLease> invoices(final Lease lease) {
        return invoicesForLeases.findByLease(lease);
    }

    @Inject
    private InvoiceForLeases invoicesForLeases;

}
