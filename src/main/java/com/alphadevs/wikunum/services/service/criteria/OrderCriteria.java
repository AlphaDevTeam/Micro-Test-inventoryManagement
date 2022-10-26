package com.alphadevs.wikunum.services.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.alphadevs.wikunum.services.domain.Order} entity. This class is used
 * in {@link com.alphadevs.wikunum.services.web.rest.OrderResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /orders?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter orderID;

    private StringFilter orderNumber;

    private StringFilter customerCode;

    private InstantFilter createdDate;

    private StringFilter transactionID;

    private StringFilter locationCode;

    private StringFilter tenantCode;

    private Boolean distinct;

    public OrderCriteria() {}

    public OrderCriteria(OrderCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.orderID = other.orderID == null ? null : other.orderID.copy();
        this.orderNumber = other.orderNumber == null ? null : other.orderNumber.copy();
        this.customerCode = other.customerCode == null ? null : other.customerCode.copy();
        this.createdDate = other.createdDate == null ? null : other.createdDate.copy();
        this.transactionID = other.transactionID == null ? null : other.transactionID.copy();
        this.locationCode = other.locationCode == null ? null : other.locationCode.copy();
        this.tenantCode = other.tenantCode == null ? null : other.tenantCode.copy();
        this.distinct = other.distinct;
    }

    @Override
    public OrderCriteria copy() {
        return new OrderCriteria(this);
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

    public StringFilter getOrderID() {
        return orderID;
    }

    public StringFilter orderID() {
        if (orderID == null) {
            orderID = new StringFilter();
        }
        return orderID;
    }

    public void setOrderID(StringFilter orderID) {
        this.orderID = orderID;
    }

    public StringFilter getOrderNumber() {
        return orderNumber;
    }

    public StringFilter orderNumber() {
        if (orderNumber == null) {
            orderNumber = new StringFilter();
        }
        return orderNumber;
    }

    public void setOrderNumber(StringFilter orderNumber) {
        this.orderNumber = orderNumber;
    }

    public StringFilter getCustomerCode() {
        return customerCode;
    }

    public StringFilter customerCode() {
        if (customerCode == null) {
            customerCode = new StringFilter();
        }
        return customerCode;
    }

    public void setCustomerCode(StringFilter customerCode) {
        this.customerCode = customerCode;
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
        final OrderCriteria that = (OrderCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(orderID, that.orderID) &&
            Objects.equals(orderNumber, that.orderNumber) &&
            Objects.equals(customerCode, that.customerCode) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(transactionID, that.transactionID) &&
            Objects.equals(locationCode, that.locationCode) &&
            Objects.equals(tenantCode, that.tenantCode) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderID, orderNumber, customerCode, createdDate, transactionID, locationCode, tenantCode, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (orderID != null ? "orderID=" + orderID + ", " : "") +
            (orderNumber != null ? "orderNumber=" + orderNumber + ", " : "") +
            (customerCode != null ? "customerCode=" + customerCode + ", " : "") +
            (createdDate != null ? "createdDate=" + createdDate + ", " : "") +
            (transactionID != null ? "transactionID=" + transactionID + ", " : "") +
            (locationCode != null ? "locationCode=" + locationCode + ", " : "") +
            (tenantCode != null ? "tenantCode=" + tenantCode + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
