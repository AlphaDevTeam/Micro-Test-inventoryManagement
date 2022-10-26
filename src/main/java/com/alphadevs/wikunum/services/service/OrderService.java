package com.alphadevs.wikunum.services.service;

import com.alphadevs.wikunum.services.domain.Order;
import com.alphadevs.wikunum.services.repository.OrderRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Order}.
 */
@Service
@Transactional
public class OrderService {

    private final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Save a order.
     *
     * @param order the entity to save.
     * @return the persisted entity.
     */
    public Order save(Order order) {
        log.debug("Request to save Order : {}", order);
        return orderRepository.save(order);
    }

    /**
     * Update a order.
     *
     * @param order the entity to save.
     * @return the persisted entity.
     */
    public Order update(Order order) {
        log.debug("Request to update Order : {}", order);
        return orderRepository.save(order);
    }

    /**
     * Partially update a order.
     *
     * @param order the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Order> partialUpdate(Order order) {
        log.debug("Request to partially update Order : {}", order);

        return orderRepository
            .findById(order.getId())
            .map(existingOrder -> {
                if (order.getOrderID() != null) {
                    existingOrder.setOrderID(order.getOrderID());
                }
                if (order.getOrderNumber() != null) {
                    existingOrder.setOrderNumber(order.getOrderNumber());
                }
                if (order.getCustomerCode() != null) {
                    existingOrder.setCustomerCode(order.getCustomerCode());
                }
                if (order.getCreatedDate() != null) {
                    existingOrder.setCreatedDate(order.getCreatedDate());
                }
                if (order.getTransactionID() != null) {
                    existingOrder.setTransactionID(order.getTransactionID());
                }
                if (order.getLocationCode() != null) {
                    existingOrder.setLocationCode(order.getLocationCode());
                }
                if (order.getTenantCode() != null) {
                    existingOrder.setTenantCode(order.getTenantCode());
                }

                return existingOrder;
            })
            .map(orderRepository::save);
    }

    /**
     * Get all the orders.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Order> findAll(Pageable pageable) {
        log.debug("Request to get all Orders");
        return orderRepository.findAll(pageable);
    }

    /**
     * Get one order by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Order> findOne(Long id) {
        log.debug("Request to get Order : {}", id);
        return orderRepository.findById(id);
    }

    /**
     * Delete the order by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Order : {}", id);
        orderRepository.deleteById(id);
    }
}
