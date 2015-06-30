package org.estatio.dom.invoice;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.party.Party;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class InvoiceContributions {

    @Action(semantics = SemanticsOf.SAFE)
    public List<? extends Invoice> invoices(final Party party) {
        return invoices.findByBuyer(party);
    }

    @Inject
    private Invoices invoices;

}
