package com.alphadevs.wikunum.services.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.alphadevs.wikunum.services.domain.OrderDetails} entity. This class is used
 * in {@link com.alphadevs.wikunum.services.web.rest.OrderDetailsResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /order-details?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderDetailsCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter notes;

    private DoubleFilter orderedQty;

    private LongFilter orderId;

    private LongFilter itemId;

    private Boolean distinct;

    public OrderDetailsCriteria() {}

    public OrderDetailsCriteria(OrderDetailsCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.notes = other.notes == null ? null : other.notes.copy();
        this.orderedQty = other.orderedQty == null ? null : other.orderedQty.copy();
        this.orderId = other.orderId == null ? null : other.orderId.copy();
        this.itemId = other.itemId == null ? null : other.itemId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public OrderDetailsCriteria copy() {
        return new OrderDetailsCriteria(this);
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

    public StringFilter getNotes() {
        return notes;
    }

    public StringFilter notes() {
        if (notes == null) {
            notes = new StringFilter();
        }
        return notes;
    }

    public void setNotes(StringFilter notes) {
        this.notes = notes;
    }

    public DoubleFilter getOrderedQty() {
        return orderedQty;
    }

    public DoubleFilter orderedQty() {
        if (orderedQty == null) {
            orderedQty = new DoubleFilter();
        }
        return orderedQty;
    }

    public void setOrderedQty(DoubleFilter orderedQty) {
        this.orderedQty = orderedQty;
    }

    public LongFilter getOrderId() {
        return orderId;
    }

    public LongFilter orderId() {
        if (orderId == null) {
            orderId = new LongFilter();
        }
        return orderId;
    }

    public void setOrderId(LongFilter orderId) {
        this.orderId = orderId;
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
        final OrderDetailsCriteria that = (OrderDetailsCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(notes, that.notes) &&
            Objects.equals(orderedQty, that.orderedQty) &&
            Objects.equals(orderId, that.orderId) &&
            Objects.equals(itemId, that.itemId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, notes, orderedQty, orderId, itemId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderDetailsCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (notes != null ? "notes=" + notes + ", " : "") +
            (orderedQty != null ? "orderedQty=" + orderedQty + ", " : "") +
            (orderId != null ? "orderId=" + orderId + ", " : "") +
            (itemId != null ? "itemId=" + itemId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
