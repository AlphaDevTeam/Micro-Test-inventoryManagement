package com.alphadevs.wikunum.services.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.alphadevs.wikunum.services.IntegrationTest;
import com.alphadevs.wikunum.services.domain.Item;
import com.alphadevs.wikunum.services.domain.Order;
import com.alphadevs.wikunum.services.domain.OrderDetails;
import com.alphadevs.wikunum.services.repository.OrderDetailsRepository;
import com.alphadevs.wikunum.services.service.OrderDetailsService;
import com.alphadevs.wikunum.services.service.criteria.OrderDetailsCriteria;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link OrderDetailsResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class OrderDetailsResourceIT {

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final Double DEFAULT_ORDERED_QTY = 1D;
    private static final Double UPDATED_ORDERED_QTY = 2D;
    private static final Double SMALLER_ORDERED_QTY = 1D - 1D;

    private static final String ENTITY_API_URL = "/api/order-details";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Mock
    private OrderDetailsRepository orderDetailsRepositoryMock;

    @Mock
    private OrderDetailsService orderDetailsServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrderDetailsMockMvc;

    private OrderDetails orderDetails;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderDetails createEntity(EntityManager em) {
        OrderDetails orderDetails = new OrderDetails().notes(DEFAULT_NOTES).orderedQty(DEFAULT_ORDERED_QTY);
        return orderDetails;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderDetails createUpdatedEntity(EntityManager em) {
        OrderDetails orderDetails = new OrderDetails().notes(UPDATED_NOTES).orderedQty(UPDATED_ORDERED_QTY);
        return orderDetails;
    }

    @BeforeEach
    public void initTest() {
        orderDetails = createEntity(em);
    }

    @Test
    @Transactional
    void createOrderDetails() throws Exception {
        int databaseSizeBeforeCreate = orderDetailsRepository.findAll().size();
        // Create the OrderDetails
        restOrderDetailsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderDetails)))
            .andExpect(status().isCreated());

        // Validate the OrderDetails in the database
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        assertThat(orderDetailsList).hasSize(databaseSizeBeforeCreate + 1);
        OrderDetails testOrderDetails = orderDetailsList.get(orderDetailsList.size() - 1);
        assertThat(testOrderDetails.getNotes()).isEqualTo(DEFAULT_NOTES);
        assertThat(testOrderDetails.getOrderedQty()).isEqualTo(DEFAULT_ORDERED_QTY);
    }

    @Test
    @Transactional
    void createOrderDetailsWithExistingId() throws Exception {
        // Create the OrderDetails with an existing ID
        orderDetails.setId(1L);

        int databaseSizeBeforeCreate = orderDetailsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderDetailsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderDetails)))
            .andExpect(status().isBadRequest());

        // Validate the OrderDetails in the database
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        assertThat(orderDetailsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkOrderedQtyIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderDetailsRepository.findAll().size();
        // set the field null
        orderDetails.setOrderedQty(null);

        // Create the OrderDetails, which fails.

        restOrderDetailsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderDetails)))
            .andExpect(status().isBadRequest());

        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        assertThat(orderDetailsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllOrderDetails() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        // Get all the orderDetailsList
        restOrderDetailsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderDetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].orderedQty").value(hasItem(DEFAULT_ORDERED_QTY.doubleValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOrderDetailsWithEagerRelationshipsIsEnabled() throws Exception {
        when(orderDetailsServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOrderDetailsMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(orderDetailsServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOrderDetailsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(orderDetailsServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOrderDetailsMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(orderDetailsRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getOrderDetails() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        // Get the orderDetails
        restOrderDetailsMockMvc
            .perform(get(ENTITY_API_URL_ID, orderDetails.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(orderDetails.getId().intValue()))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES))
            .andExpect(jsonPath("$.orderedQty").value(DEFAULT_ORDERED_QTY.doubleValue()));
    }

    @Test
    @Transactional
    void getOrderDetailsByIdFiltering() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        Long id = orderDetails.getId();

        defaultOrderDetailsShouldBeFound("id.equals=" + id);
        defaultOrderDetailsShouldNotBeFound("id.notEquals=" + id);

        defaultOrderDetailsShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultOrderDetailsShouldNotBeFound("id.greaterThan=" + id);

        defaultOrderDetailsShouldBeFound("id.lessThanOrEqual=" + id);
        defaultOrderDetailsShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllOrderDetailsByNotesIsEqualToSomething() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        // Get all the orderDetailsList where notes equals to DEFAULT_NOTES
        defaultOrderDetailsShouldBeFound("notes.equals=" + DEFAULT_NOTES);

        // Get all the orderDetailsList where notes equals to UPDATED_NOTES
        defaultOrderDetailsShouldNotBeFound("notes.equals=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllOrderDetailsByNotesIsInShouldWork() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        // Get all the orderDetailsList where notes in DEFAULT_NOTES or UPDATED_NOTES
        defaultOrderDetailsShouldBeFound("notes.in=" + DEFAULT_NOTES + "," + UPDATED_NOTES);

        // Get all the orderDetailsList where notes equals to UPDATED_NOTES
        defaultOrderDetailsShouldNotBeFound("notes.in=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllOrderDetailsByNotesIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        // Get all the orderDetailsList where notes is not null
        defaultOrderDetailsShouldBeFound("notes.specified=true");

        // Get all the orderDetailsList where notes is null
        defaultOrderDetailsShouldNotBeFound("notes.specified=false");
    }

    @Test
    @Transactional
    void getAllOrderDetailsByNotesContainsSomething() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        // Get all the orderDetailsList where notes contains DEFAULT_NOTES
        defaultOrderDetailsShouldBeFound("notes.contains=" + DEFAULT_NOTES);

        // Get all the orderDetailsList where notes contains UPDATED_NOTES
        defaultOrderDetailsShouldNotBeFound("notes.contains=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllOrderDetailsByNotesNotContainsSomething() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        // Get all the orderDetailsList where notes does not contain DEFAULT_NOTES
        defaultOrderDetailsShouldNotBeFound("notes.doesNotContain=" + DEFAULT_NOTES);

        // Get all the orderDetailsList where notes does not contain UPDATED_NOTES
        defaultOrderDetailsShouldBeFound("notes.doesNotContain=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllOrderDetailsByOrderedQtyIsEqualToSomething() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        // Get all the orderDetailsList where orderedQty equals to DEFAULT_ORDERED_QTY
        defaultOrderDetailsShouldBeFound("orderedQty.equals=" + DEFAULT_ORDERED_QTY);

        // Get all the orderDetailsList where orderedQty equals to UPDATED_ORDERED_QTY
        defaultOrderDetailsShouldNotBeFound("orderedQty.equals=" + UPDATED_ORDERED_QTY);
    }

    @Test
    @Transactional
    void getAllOrderDetailsByOrderedQtyIsInShouldWork() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        // Get all the orderDetailsList where orderedQty in DEFAULT_ORDERED_QTY or UPDATED_ORDERED_QTY
        defaultOrderDetailsShouldBeFound("orderedQty.in=" + DEFAULT_ORDERED_QTY + "," + UPDATED_ORDERED_QTY);

        // Get all the orderDetailsList where orderedQty equals to UPDATED_ORDERED_QTY
        defaultOrderDetailsShouldNotBeFound("orderedQty.in=" + UPDATED_ORDERED_QTY);
    }

    @Test
    @Transactional
    void getAllOrderDetailsByOrderedQtyIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        // Get all the orderDetailsList where orderedQty is not null
        defaultOrderDetailsShouldBeFound("orderedQty.specified=true");

        // Get all the orderDetailsList where orderedQty is null
        defaultOrderDetailsShouldNotBeFound("orderedQty.specified=false");
    }

    @Test
    @Transactional
    void getAllOrderDetailsByOrderedQtyIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        // Get all the orderDetailsList where orderedQty is greater than or equal to DEFAULT_ORDERED_QTY
        defaultOrderDetailsShouldBeFound("orderedQty.greaterThanOrEqual=" + DEFAULT_ORDERED_QTY);

        // Get all the orderDetailsList where orderedQty is greater than or equal to UPDATED_ORDERED_QTY
        defaultOrderDetailsShouldNotBeFound("orderedQty.greaterThanOrEqual=" + UPDATED_ORDERED_QTY);
    }

    @Test
    @Transactional
    void getAllOrderDetailsByOrderedQtyIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        // Get all the orderDetailsList where orderedQty is less than or equal to DEFAULT_ORDERED_QTY
        defaultOrderDetailsShouldBeFound("orderedQty.lessThanOrEqual=" + DEFAULT_ORDERED_QTY);

        // Get all the orderDetailsList where orderedQty is less than or equal to SMALLER_ORDERED_QTY
        defaultOrderDetailsShouldNotBeFound("orderedQty.lessThanOrEqual=" + SMALLER_ORDERED_QTY);
    }

    @Test
    @Transactional
    void getAllOrderDetailsByOrderedQtyIsLessThanSomething() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        // Get all the orderDetailsList where orderedQty is less than DEFAULT_ORDERED_QTY
        defaultOrderDetailsShouldNotBeFound("orderedQty.lessThan=" + DEFAULT_ORDERED_QTY);

        // Get all the orderDetailsList where orderedQty is less than UPDATED_ORDERED_QTY
        defaultOrderDetailsShouldBeFound("orderedQty.lessThan=" + UPDATED_ORDERED_QTY);
    }

    @Test
    @Transactional
    void getAllOrderDetailsByOrderedQtyIsGreaterThanSomething() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        // Get all the orderDetailsList where orderedQty is greater than DEFAULT_ORDERED_QTY
        defaultOrderDetailsShouldNotBeFound("orderedQty.greaterThan=" + DEFAULT_ORDERED_QTY);

        // Get all the orderDetailsList where orderedQty is greater than SMALLER_ORDERED_QTY
        defaultOrderDetailsShouldBeFound("orderedQty.greaterThan=" + SMALLER_ORDERED_QTY);
    }

    @Test
    @Transactional
    void getAllOrderDetailsByOrderIsEqualToSomething() throws Exception {
        Order order;
        if (TestUtil.findAll(em, Order.class).isEmpty()) {
            orderDetailsRepository.saveAndFlush(orderDetails);
            order = OrderResourceIT.createEntity(em);
        } else {
            order = TestUtil.findAll(em, Order.class).get(0);
        }
        em.persist(order);
        em.flush();
        orderDetails.setOrder(order);
        orderDetailsRepository.saveAndFlush(orderDetails);
        Long orderId = order.getId();

        // Get all the orderDetailsList where order equals to orderId
        defaultOrderDetailsShouldBeFound("orderId.equals=" + orderId);

        // Get all the orderDetailsList where order equals to (orderId + 1)
        defaultOrderDetailsShouldNotBeFound("orderId.equals=" + (orderId + 1));
    }

    @Test
    @Transactional
    void getAllOrderDetailsByItemIsEqualToSomething() throws Exception {
        Item item;
        if (TestUtil.findAll(em, Item.class).isEmpty()) {
            orderDetailsRepository.saveAndFlush(orderDetails);
            item = ItemResourceIT.createEntity(em);
        } else {
            item = TestUtil.findAll(em, Item.class).get(0);
        }
        em.persist(item);
        em.flush();
        orderDetails.setItem(item);
        orderDetailsRepository.saveAndFlush(orderDetails);
        Long itemId = item.getId();

        // Get all the orderDetailsList where item equals to itemId
        defaultOrderDetailsShouldBeFound("itemId.equals=" + itemId);

        // Get all the orderDetailsList where item equals to (itemId + 1)
        defaultOrderDetailsShouldNotBeFound("itemId.equals=" + (itemId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOrderDetailsShouldBeFound(String filter) throws Exception {
        restOrderDetailsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderDetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].orderedQty").value(hasItem(DEFAULT_ORDERED_QTY.doubleValue())));

        // Check, that the count call also returns 1
        restOrderDetailsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultOrderDetailsShouldNotBeFound(String filter) throws Exception {
        restOrderDetailsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restOrderDetailsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingOrderDetails() throws Exception {
        // Get the orderDetails
        restOrderDetailsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOrderDetails() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        int databaseSizeBeforeUpdate = orderDetailsRepository.findAll().size();

        // Update the orderDetails
        OrderDetails updatedOrderDetails = orderDetailsRepository.findById(orderDetails.getId()).get();
        // Disconnect from session so that the updates on updatedOrderDetails are not directly saved in db
        em.detach(updatedOrderDetails);
        updatedOrderDetails.notes(UPDATED_NOTES).orderedQty(UPDATED_ORDERED_QTY);

        restOrderDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedOrderDetails.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedOrderDetails))
            )
            .andExpect(status().isOk());

        // Validate the OrderDetails in the database
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        assertThat(orderDetailsList).hasSize(databaseSizeBeforeUpdate);
        OrderDetails testOrderDetails = orderDetailsList.get(orderDetailsList.size() - 1);
        assertThat(testOrderDetails.getNotes()).isEqualTo(UPDATED_NOTES);
        assertThat(testOrderDetails.getOrderedQty()).isEqualTo(UPDATED_ORDERED_QTY);
    }

    @Test
    @Transactional
    void putNonExistingOrderDetails() throws Exception {
        int databaseSizeBeforeUpdate = orderDetailsRepository.findAll().size();
        orderDetails.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderDetails.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderDetails in the database
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        assertThat(orderDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrderDetails() throws Exception {
        int databaseSizeBeforeUpdate = orderDetailsRepository.findAll().size();
        orderDetails.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderDetails in the database
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        assertThat(orderDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrderDetails() throws Exception {
        int databaseSizeBeforeUpdate = orderDetailsRepository.findAll().size();
        orderDetails.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderDetailsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderDetails)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderDetails in the database
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        assertThat(orderDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOrderDetailsWithPatch() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        int databaseSizeBeforeUpdate = orderDetailsRepository.findAll().size();

        // Update the orderDetails using partial update
        OrderDetails partialUpdatedOrderDetails = new OrderDetails();
        partialUpdatedOrderDetails.setId(orderDetails.getId());

        partialUpdatedOrderDetails.notes(UPDATED_NOTES);

        restOrderDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderDetails.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrderDetails))
            )
            .andExpect(status().isOk());

        // Validate the OrderDetails in the database
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        assertThat(orderDetailsList).hasSize(databaseSizeBeforeUpdate);
        OrderDetails testOrderDetails = orderDetailsList.get(orderDetailsList.size() - 1);
        assertThat(testOrderDetails.getNotes()).isEqualTo(UPDATED_NOTES);
        assertThat(testOrderDetails.getOrderedQty()).isEqualTo(DEFAULT_ORDERED_QTY);
    }

    @Test
    @Transactional
    void fullUpdateOrderDetailsWithPatch() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        int databaseSizeBeforeUpdate = orderDetailsRepository.findAll().size();

        // Update the orderDetails using partial update
        OrderDetails partialUpdatedOrderDetails = new OrderDetails();
        partialUpdatedOrderDetails.setId(orderDetails.getId());

        partialUpdatedOrderDetails.notes(UPDATED_NOTES).orderedQty(UPDATED_ORDERED_QTY);

        restOrderDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderDetails.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrderDetails))
            )
            .andExpect(status().isOk());

        // Validate the OrderDetails in the database
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        assertThat(orderDetailsList).hasSize(databaseSizeBeforeUpdate);
        OrderDetails testOrderDetails = orderDetailsList.get(orderDetailsList.size() - 1);
        assertThat(testOrderDetails.getNotes()).isEqualTo(UPDATED_NOTES);
        assertThat(testOrderDetails.getOrderedQty()).isEqualTo(UPDATED_ORDERED_QTY);
    }

    @Test
    @Transactional
    void patchNonExistingOrderDetails() throws Exception {
        int databaseSizeBeforeUpdate = orderDetailsRepository.findAll().size();
        orderDetails.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, orderDetails.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderDetails in the database
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        assertThat(orderDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrderDetails() throws Exception {
        int databaseSizeBeforeUpdate = orderDetailsRepository.findAll().size();
        orderDetails.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderDetails in the database
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        assertThat(orderDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrderDetails() throws Exception {
        int databaseSizeBeforeUpdate = orderDetailsRepository.findAll().size();
        orderDetails.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(orderDetails))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderDetails in the database
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        assertThat(orderDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOrderDetails() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        int databaseSizeBeforeDelete = orderDetailsRepository.findAll().size();

        // Delete the orderDetails
        restOrderDetailsMockMvc
            .perform(delete(ENTITY_API_URL_ID, orderDetails.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        assertThat(orderDetailsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
