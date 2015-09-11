/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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

package org.estatio.dom.document.asset;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.document.Document;
import org.estatio.dom.document.DocumentRepository;

@DomainService(repositoryFor = Document.class)
@DomainServiceLayout(named = "Other", menuBar = DomainServiceLayout.MenuBar.PRIMARY, menuOrder = "80.10")
public class DocumentMenu extends UdoDomainRepositoryAndFactory<Document> {

    public DocumentMenu() {
        super(DocumentMenu.class, Document.class);
    }

    @MemberOrder(sequence = "2")
    public List<Document> allDocuments() {
        return documentRepository.allDocuments();
    }

    public List<Document> findByName(final String pattern) {
        return documentRepository.findByName(pattern);
    }

    @Inject
    DocumentRepository documentRepository;
}
