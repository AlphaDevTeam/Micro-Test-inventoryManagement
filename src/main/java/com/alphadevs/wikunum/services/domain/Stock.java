package com.alphadevs.wikunum.services.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Stock.
 */
@Entity
@Table(name = "stock")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Stock implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "stock_qty", nullable = false)
    private Double stockQty;

    @NotNull
    @Column(name = "location_code", nullable = false)
    private String locationCode;

    @NotNull
    @Column(name = "tenant_code", nullable = false)
    private String tenantCode;

    @ManyToOne
    private Item item;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Stock id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getStockQty() {
        return this.stockQty;
    }

    public Stock stockQty(Double stockQty) {
        this.setStockQty(stockQty);
        return this;
    }

    public void setStockQty(Double stockQty) {
        this.stockQty = stockQty;
    }

    public String getLocationCode() {
        return this.locationCode;
    }

    public Stock locationCode(String locationCode) {
        this.setLocationCode(locationCode);
        return this;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getTenantCode() {
        return this.tenantCode;
    }

    public Stock tenantCode(String tenantCode) {
        this.setTenantCode(tenantCode);
        return this;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public Item getItem() {
        return this.item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Stock item(Item item) {
        this.setItem(item);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Stock)) {
            return false;
        }
        return id != null && id.equals(((Stock) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Stock{" +
            "id=" + getId() +
            ", stockQty=" + getStockQty() +
            ", locationCode='" + getLocationCode() + "'" +
            ", tenantCode='" + getTenantCode() + "'" +
            "}";
    }
}
