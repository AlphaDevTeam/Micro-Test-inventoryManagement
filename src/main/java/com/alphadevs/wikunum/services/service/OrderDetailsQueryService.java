package com.alphadevs.wikunum.services.service;

import com.alphadevs.wikunum.services.domain.*; // for static metamodels
import com.alphadevs.wikunum.services.domain.OrderDetails;
import com.alphadevs.wikunum.services.repository.OrderDetailsRepository;
import com.alphadevs.wikunum.services.service.criteria.OrderDetailsCriteria;
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
 * Service for executing complex queries for {@link OrderDetails} entities in the database.
 * The main input is a {@link OrderDetailsCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link OrderDetails} or a {@link Page} of {@link OrderDetails} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OrderDetailsQueryService extends QueryService<OrderDetails> {

    private final Logger log = LoggerFactory.getLogger(OrderDetailsQueryService.class);

    private final OrderDetailsRepository orderDetailsRepository;

    public OrderDetailsQueryService(OrderDetailsRepository orderDetailsRepository) {
        this.orderDetailsRepository = orderDetailsRepository;
    }

    /**
     * Return a {@link List} of {@link OrderDetails} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<OrderDetails> findByCriteria(OrderDetailsCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<OrderDetails> specification = createSpecification(criteria);
        return orderDetailsRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link OrderDetails} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<OrderDetails> findByCriteria(OrderDetailsCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<OrderDetails> specification = createSpecification(criteria);
        return orderDetailsRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(OrderDetailsCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<OrderDetails> specification = createSpecification(criteria);
        return orderDetailsRepository.count(specification);
    }

    /**
     * Function to convert {@link OrderDetailsCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<OrderDetails> createSpecification(OrderDetailsCriteria criteria) {
        Specification<OrderDetails> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), OrderDetails_.id));
            }
            if (criteria.getNotes() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNotes(), OrderDetails_.notes));
            }
            if (criteria.getOrderedQty() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getOrderedQty(), OrderDetails_.orderedQty));
            }
            if (criteria.getOrderId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getOrderId(), root -> root.join(OrderDetails_.order, JoinType.LEFT).get(Order_.id))
                    );
            }
            if (criteria.getItemId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getItemId(), root -> root.join(OrderDetails_.item, JoinType.LEFT).get(Item_.id))
                    );
            }
        }
        return specification;
    }
}
