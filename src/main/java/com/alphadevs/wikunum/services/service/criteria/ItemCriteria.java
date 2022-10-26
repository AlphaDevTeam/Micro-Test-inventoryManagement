package com.alphadevs.wikunum.services.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.alphadevs.wikunum.services.domain.Item} entity. This class is used
 * in {@link com.alphadevs.wikunum.services.web.rest.ItemResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /items?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ItemCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter itemCode;

    private StringFilter itemName;

    private InstantFilter createdDate;

    private DoubleFilter unitPrice;

    private StringFilter transactionID;

    private StringFilter locationCode;

    private StringFilter tenantCode;

    private Boolean distinct;

    public ItemCriteria() {}

    public ItemCriteria(ItemCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.itemCode = other.itemCode == null ? null : other.itemCode.copy();
        this.itemName = other.itemName == null ? null : other.itemName.copy();
        this.createdDate = other.createdDate == null ? null : other.createdDate.copy();
        this.unitPrice = other.unitPrice == null ? null : other.unitPrice.copy();
        this.transactionID = other.transactionID == null ? null : other.transactionID.copy();
        this.locationCode = other.locationCode == null ? null : other.locationCode.copy();
        this.tenantCode = other.tenantCode == null ? null : other.tenantCode.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ItemCriteria copy() {
        return new ItemCriteria(this);
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

    public StringFilter getItemCode() {
        return itemCode;
    }

    public StringFilter itemCode() {
        if (itemCode == null) {
            itemCode = new StringFilter();
        }
        return itemCode;
    }

    public void setItemCode(StringFilter itemCode) {
        this.itemCode = itemCode;
    }

    public StringFilter getItemName() {
        return itemName;
    }

    public StringFilter itemName() {
        if (itemName == null) {
            itemName = new StringFilter();
        }
        return itemName;
    }

    public void setItemName(StringFilter itemName) {
        this.itemName = itemName;
    }

    public InstantFilter getCreatedDate() {
        return createdDate;
    }

    public InstantFilter createdDate() {
        if (createdDate == null) {
            createdDate = new InstantFilter();
        }
        return createdDate;
    }

    public void setCreatedDate(InstantFilter createdDate) {
        this.createdDate = createdDate;
    }

    public DoubleFilter getUnitPrice() {
        return unitPrice;
    }

    public DoubleFilter unitPrice() {
        if (unitPrice == null) {
            unitPrice = new DoubleFilter();
        }
        return unitPrice;
    }

    public void setUnitPrice(DoubleFilter unitPrice) {
        this.unitPrice = unitPrice;
    }

    public StringFilter getTransactionID() {
        return transactionID;
    }

    public StringFilter transactionID() {
        if (transactionID == null) {
            transactionID = new StringFilter();
        }
        return transactionID;
    }

    public void setTransactionID(StringFilter transactionID) {
        this.transactionID = transactionID;
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
        final ItemCriteria that = (ItemCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(itemCode, that.itemCode) &&
            Objects.equals(itemName, that.itemName) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(unitPrice, that.unitPrice) &&
            Objects.equals(transactionID, that.transactionID) &&
            Objects.equals(locationCode, that.locationCode) &&
            Objects.equals(tenantCode, that.tenantCode) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, itemCode, itemName, createdDate, unitPrice, transactionID, locationCode, tenantCode, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ItemCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (itemCode != null ? "itemCode=" + itemCode + ", " : "") +
            (itemName != null ? "itemName=" + itemName + ", " : "") +
            (createdDate != null ? "createdDate=" + createdDate + ", " : "") +
            (unitPrice != null ? "unitPrice=" + unitPrice + ", " : "") +
            (transactionID != null ? "transactionID=" + transactionID + ", " : "") +
            (locationCode != null ? "locationCode=" + locationCode + ", " : "") +
            (tenantCode != null ? "tenantCode=" + tenantCode + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
