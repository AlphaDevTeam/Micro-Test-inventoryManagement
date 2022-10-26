package com.alphadevs.wikunum.services.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A OrderDetails.
 */
@Entity
@Table(name = "order_details")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "notes")
    private String notes;

    @NotNull
    @Column(name = "ordered_qty", nullable = false)
    private Double orderedQty;

    @ManyToOne
    private Order order;

    @ManyToOne
    private Item item;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public OrderDetails id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNotes() {
        return this.notes;
    }

    public OrderDetails notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Double getOrderedQty() {
        return this.orderedQty;
    }

    public OrderDetails orderedQty(Double orderedQty) {
        this.setOrderedQty(orderedQty);
        return this;
    }

    public void setOrderedQty(Double orderedQty) {
        this.orderedQty = orderedQty;
    }

    public Order getOrder() {
        return this.order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public OrderDetails order(Order order) {
        this.setOrder(order);
        return this;
    }

    public Item getItem() {
        return this.item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public OrderDetails item(Item item) {
        this.setItem(item);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderDetails)) {
            return false;
        }
        return id != null && id.equals(((OrderDetails) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderDetails{" +
            "id=" + getId() +
            ", notes='" + getNotes() + "'" +
            ", orderedQty=" + getOrderedQty() +
            "}";
    }
}
