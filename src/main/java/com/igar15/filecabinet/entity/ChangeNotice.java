package com.igar15.filecabinet.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="change_notices")
public class ChangeNotice extends AbstractNamedEntity {

    @Column(name = "change_code")
    private Integer changeCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "developer_id")
    private Developer developer;

    @ManyToMany()
    @JoinTable(name = "document_change_notices",
            joinColumns = @JoinColumn(name = "change_notice_id"),
            inverseJoinColumns = @JoinColumn(name = "document_id"))
    private List<Document> documents;

    public ChangeNotice() {

    }

    public ChangeNotice(Integer id, String name, Integer changeCode, Developer developer, List<Document> documents) {
        super(id, name);
        this.changeCode = changeCode;
        this.developer = developer;
        this.documents = documents;
    }

    public Integer getChangeCode() {
        return changeCode;
    }

    public void setChangeCode(Integer changeCode) {
        this.changeCode = changeCode;
    }

    public Developer getDeveloper() {
        return developer;
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
}