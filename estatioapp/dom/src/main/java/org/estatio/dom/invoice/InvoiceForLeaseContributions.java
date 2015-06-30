package org.estatio.dom.invoice;

import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.lease.Lease;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class InvoiceForLeaseContributions {

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(named = "Invoices")
    public List<InvoiceForLease> findByLease(final Lease lease) {
        return invoicesForLeases.findByLease(lease);
    }


    private InvoiceForLeases invoicesForLeases;

}
