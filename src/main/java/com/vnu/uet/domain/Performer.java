package com.vnu.uet.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Performer.
 */
@Entity
@Table(name = "performer")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Performer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(max = 100)
    @Column(name = "user_id", length = 100)
    private String userId;

    @Column(name = "order_execution")
    private Long orderExecution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "relateNodes", "performers", "mapForms", "flow" }, allowSetters = true)
    private Node node;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Performer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return this.userId;
    }

    public Performer userId(String userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getOrderExecution() {
        return this.orderExecution;
    }

    public Performer orderExecution(Long orderExecution) {
        this.setOrderExecution(orderExecution);
        return this;
    }

    public void setOrderExecution(Long orderExecution) {
        this.orderExecution = orderExecution;
    }

    public Node getNode() {
        return this.node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public Performer node(Node node) {
        this.setNode(node);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Performer)) {
            return false;
        }
        return getId() != null && getId().equals(((Performer) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Performer{" +
            "id=" + getId() +
            ", userId='" + getUserId() + "'" +
            ", orderExecution=" + getOrderExecution() +
            "}";
    }
}
