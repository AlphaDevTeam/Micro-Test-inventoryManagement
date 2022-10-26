package com.alphadevs.wikunum.services.service;

import com.alphadevs.wikunum.services.domain.*; // for static metamodels
import com.alphadevs.wikunum.services.domain.Order;
import com.alphadevs.wikunum.services.repository.OrderRepository;
import com.alphadevs.wikunum.services.service.criteria.OrderCriteria;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Order} entities in the database.
 * The main input is a {@link OrderCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Order} or a {@link Page} of {@link Order} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OrderQueryService extends QueryService<Order> {

    private final Logger log = LoggerFactory.getLogger(OrderQueryService.class);

    private final OrderRepository orderRepository;

    public OrderQueryService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Return a {@link List} of {@link Order} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Order> findByCriteria(OrderCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Order> specification = createSpecification(criteria);
        return orderRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Order} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Order> findByCriteria(OrderCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Order> specification = createSpecification(criteria);
        return orderRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(OrderCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Order> specification = createSpecification(criteria);
        return orderRepository.count(specification);
    }

    /**
     * Function to convert {@link OrderCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Order> createSpecification(OrderCriteria criteria) {
        Specification<Order> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Order_.id));
            }
            if (criteria.getOrderID() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOrderID(), Order_.orderID));
            }
            if (criteria.getOrderNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOrderNumber(), Order_.orderNumber));
            }
            if (criteria.getCustomerCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCustomerCode(), Order_.customerCode));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), Order_.createdDate));
            }
            if (criteria.getTransactionID() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTransactionID(), Order_.transactionID));
            }
            if (criteria.getLocationCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLocationCode(), Order_.locationCode));
            }
            if (criteria.getTenantCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTenantCode(), Order_.tenantCode));
            }
        }
        return specification;
    }
}
