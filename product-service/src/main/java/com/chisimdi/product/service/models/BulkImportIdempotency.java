package com.chisimdi.product.service.models;

import com.chisimdi.product.service.utils.BulkImportStatus;
import jakarta.persistence.*;

@Entity
public class BulkImportIdempotency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING)
    private BulkImportStatus bulkImportStatus;
    @Column(unique = true)
    private String idempotencyKey;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public BulkImportStatus getBulkImportStatus() {
        return bulkImportStatus;
    }

    public void setBulkImportStatus(BulkImportStatus bulkImportStatus) {
        this.bulkImportStatus = bulkImportStatus;
    }

}
