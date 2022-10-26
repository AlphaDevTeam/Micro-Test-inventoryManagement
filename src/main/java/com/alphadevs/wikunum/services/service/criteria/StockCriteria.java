package com.alphadevs.wikunum.services.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.alphadevs.wikunum.services.domain.Stock} entity. This class is used
 * in {@link com.alphadevs.wikunum.services.web.rest.StockResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /stocks?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private DoubleFilter stockQty;

    private StringFilter locationCode;

    private StringFilter tenantCode;

    private LongFilter itemId;

    private Boolean distinct;

    public StockCriteria() {}

    public StockCriteria(StockCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.stockQty = other.stockQty == null ? null : other.stockQty.copy();
        this.locationCode = other.locationCode == null ? null : other.locationCode.copy();
        this.tenantCode = other.tenantCode == null ? null : other.tenantCode.copy();
        this.itemId = other.itemId == null ? null : other.itemId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public StockCriteria copy() {
        return new StockCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public DoubleFilter getStockQty() {
        return stockQty;
    }

    public DoubleFilter stockQty() {
        if (stockQty == null) {
            stockQty = new DoubleFilter();
        }
        return stockQty;
    }

    public void setStockQty(DoubleFilter stockQty) {
        this.stockQty = stockQty;
    }

    public StringFilter getLocationCode() {
        return locationCode;
    }

    public StringFilter locationCode() {
        if (locationCode == null) {
            locationCode = new StringFilter();
        }
        return locationCode;
    }

    public void setLocationCode(StringFilter locationCode) {
        this.locationCode = locationCode;
    }

    public StringFilter getTenantCode() {
        return tenantCode;
    }

    public StringFilter tenantCode() {
        if (tenantCode == null) {
            tenantCode = new StringFilter();
        }
        return tenantCode;
    }

    public void setTenantCode(StringFilter tenantCode) {
        this.tenantCode = tenantCode;
    }

    public LongFilter getItemId() {
        return itemId;
    }

    public LongFilter itemId() {
        if (itemId == null) {
            itemId = new LongFilter();
        }
        return itemId;
    }

    public void setItemId(LongFilter itemId) {
        this.itemId = itemId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final StockCriteria that = (StockCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(stockQty, that.stockQty) &&
            Objects.equals(locationCode, that.locationCode) &&
            Objects.equals(tenantCode, that.tenantCode) &&
            Objects.equals(itemId, that.itemId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, stockQty, locationCode, tenantCode, itemId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (stockQty != null ? "stockQty=" + stockQty + ", " : "") +
            (locationCode != null ? "locationCode=" + locationCode + ", " : "") +
            (tenantCode != null ? "tenantCode=" + tenantCode + ", " : "") +
            (itemId != null ? "itemId=" + itemId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
