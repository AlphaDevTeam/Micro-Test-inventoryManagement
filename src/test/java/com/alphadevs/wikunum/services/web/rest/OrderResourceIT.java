package com.alphadevs.wikunum.services.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.alphadevs.wikunum.services.IntegrationTest;
import com.alphadevs.wikunum.services.domain.Order;
import com.alphadevs.wikunum.services.repository.OrderRepository;
import com.alphadevs.wikunum.services.service.criteria.OrderCriteria;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link OrderResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OrderResourceIT {

    private static final String DEFAULT_ORDER_ID = "AAAAAAAAAA";
    private static final String UPDATED_ORDER_ID = "BBBBBBBBBB";

    private static final String DEFAULT_ORDER_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_ORDER_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_CUSTOMER_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CUSTOMER_CODE = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_TRANSACTION_ID = "AAAAAAAAAA";
    private static final String UPDATED_TRANSACTION_ID = "BBBBBBBBBB";

    private static final String DEFAULT_LOCATION_CODE = "AAAAAAAAAA";
    private static final String UPDATED_LOCATION_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_TENANT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_TENANT_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrderMockMvc;

    private Order order;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Order createEntity(EntityManager em) {
        Order order = new Order()
            .orderID(DEFAULT_ORDER_ID)
            .orderNumber(DEFAULT_ORDER_NUMBER)
            .customerCode(DEFAULT_CUSTOMER_CODE)
            .createdDate(DEFAULT_CREATED_DATE)
            .transactionID(DEFAULT_TRANSACTION_ID)
            .locationCode(DEFAULT_LOCATION_CODE)
            .tenantCode(DEFAULT_TENANT_CODE);
        return order;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Order createUpdatedEntity(EntityManager em) {
        Order order = new Order()
            .orderID(UPDATED_ORDER_ID)
            .orderNumber(UPDATED_ORDER_NUMBER)
            .customerCode(UPDATED_CUSTOMER_CODE)
            .createdDate(UPDATED_CREATED_DATE)
            .transactionID(UPDATED_TRANSACTION_ID)
            .locationCode(UPDATED_LOCATION_CODE)
            .tenantCode(UPDATED_TENANT_CODE);
        return order;
    }

    @BeforeEach
    public void initTest() {
        order = createEntity(em);
    }

    @Test
    @Transactional
    void createOrder() throws Exception {
        int databaseSizeBeforeCreate = orderRepository.findAll().size();
        // Create the Order
        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isCreated());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeCreate + 1);
        Order testOrder = orderList.get(orderList.size() - 1);
        assertThat(testOrder.getOrderID()).isEqualTo(DEFAULT_ORDER_ID);
        assertThat(testOrder.getOrderNumber()).isEqualTo(DEFAULT_ORDER_NUMBER);
        assertThat(testOrder.getCustomerCode()).isEqualTo(DEFAULT_CUSTOMER_CODE);
        assertThat(testOrder.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testOrder.getTransactionID()).isEqualTo(DEFAULT_TRANSACTION_ID);
        assertThat(testOrder.getLocationCode()).isEqualTo(DEFAULT_LOCATION_CODE);
        assertThat(testOrder.getTenantCode()).isEqualTo(DEFAULT_TENANT_CODE);
    }

    @Test
    @Transactional
    void createOrderWithExistingId() throws Exception {
        // Create the Order with an existing ID
        order.setId(1L);

        int databaseSizeBeforeCreate = orderRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkOrderIDIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderRepository.findAll().size();
        // set the field null
        order.setOrderID(null);

        // Create the Order, which fails.

        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isBadRequest());

        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOrderNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderRepository.findAll().size();
        // set the field null
        order.setOrderNumber(null);

        // Create the Order, which fails.

        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isBadRequest());

        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCustomerCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderRepository.findAll().size();
        // set the field null
        order.setCustomerCode(null);

        // Create the Order, which fails.

        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isBadRequest());

        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTransactionIDIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderRepository.findAll().size();
        // set the field null
        order.setTransactionID(null);

        // Create the Order, which fails.

        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isBadRequest());

        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLocationCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderRepository.findAll().size();
        // set the field null
        order.setLocationCode(null);

        // Create the Order, which fails.

        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isBadRequest());

        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTenantCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderRepository.findAll().size();
        // set the field null
        order.setTenantCode(null);

        // Create the Order, which fails.

        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isBadRequest());

        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllOrders() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList
        restOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(order.getId().intValue())))
            .andExpect(jsonPath("$.[*].orderID").value(hasItem(DEFAULT_ORDER_ID)))
            .andExpect(jsonPath("$.[*].orderNumber").value(hasItem(DEFAULT_ORDER_NUMBER)))
            .andExpect(jsonPath("$.[*].customerCode").value(hasItem(DEFAULT_CUSTOMER_CODE)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].transactionID").value(hasItem(DEFAULT_TRANSACTION_ID)))
            .andExpect(jsonPath("$.[*].locationCode").value(hasItem(DEFAULT_LOCATION_CODE)))
            .andExpect(jsonPath("$.[*].tenantCode").value(hasItem(DEFAULT_TENANT_CODE)));
    }

    @Test
    @Transactional
    void getOrder() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get the order
        restOrderMockMvc
            .perform(get(ENTITY_API_URL_ID, order.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(order.getId().intValue()))
            .andExpect(jsonPath("$.orderID").value(DEFAULT_ORDER_ID))
            .andExpect(jsonPath("$.orderNumber").value(DEFAULT_ORDER_NUMBER))
            .andExpect(jsonPath("$.customerCode").value(DEFAULT_CUSTOMER_CODE))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.transactionID").value(DEFAULT_TRANSACTION_ID))
            .andExpect(jsonPath("$.locationCode").value(DEFAULT_LOCATION_CODE))
            .andExpect(jsonPath("$.tenantCode").value(DEFAULT_TENANT_CODE));
    }

    @Test
    @Transactional
    void getOrdersByIdFiltering() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        Long id = order.getId();

        defaultOrderShouldBeFound("id.equals=" + id);
        defaultOrderShouldNotBeFound("id.notEquals=" + id);

        defaultOrderShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultOrderShouldNotBeFound("id.greaterThan=" + id);

        defaultOrderShouldBeFound("id.lessThanOrEqual=" + id);
        defaultOrderShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllOrdersByOrderIDIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where orderID equals to DEFAULT_ORDER_ID
        defaultOrderShouldBeFound("orderID.equals=" + DEFAULT_ORDER_ID);

        // Get all the orderList where orderID equals to UPDATED_ORDER_ID
        defaultOrderShouldNotBeFound("orderID.equals=" + UPDATED_ORDER_ID);
    }

    @Test
    @Transactional
    void getAllOrdersByOrderIDIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where orderID in DEFAULT_ORDER_ID or UPDATED_ORDER_ID
        defaultOrderShouldBeFound("orderID.in=" + DEFAULT_ORDER_ID + "," + UPDATED_ORDER_ID);

        // Get all the orderList where orderID equals to UPDATED_ORDER_ID
        defaultOrderShouldNotBeFound("orderID.in=" + UPDATED_ORDER_ID);
    }

    @Test
    @Transactional
    void getAllOrdersByOrderIDIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where orderID is not null
        defaultOrderShouldBeFound("orderID.specified=true");

        // Get all the orderList where orderID is null
        defaultOrderShouldNotBeFound("orderID.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByOrderIDContainsSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where orderID contains DEFAULT_ORDER_ID
        defaultOrderShouldBeFound("orderID.contains=" + DEFAULT_ORDER_ID);

        // Get all the orderList where orderID contains UPDATED_ORDER_ID
        defaultOrderShouldNotBeFound("orderID.contains=" + UPDATED_ORDER_ID);
    }

    @Test
    @Transactional
    void getAllOrdersByOrderIDNotContainsSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where orderID does not contain DEFAULT_ORDER_ID
        defaultOrderShouldNotBeFound("orderID.doesNotContain=" + DEFAULT_ORDER_ID);

        // Get all the orderList where orderID does not contain UPDATED_ORDER_ID
        defaultOrderShouldBeFound("orderID.doesNotContain=" + UPDATED_ORDER_ID);
    }

    @Test
    @Transactional
    void getAllOrdersByOrderNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where orderNumber equals to DEFAULT_ORDER_NUMBER
        defaultOrderShouldBeFound("orderNumber.equals=" + DEFAULT_ORDER_NUMBER);

        // Get all the orderList where orderNumber equals to UPDATED_ORDER_NUMBER
        defaultOrderShouldNotBeFound("orderNumber.equals=" + UPDATED_ORDER_NUMBER);
    }

    @Test
    @Transactional
    void getAllOrdersByOrderNumberIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where orderNumber in DEFAULT_ORDER_NUMBER or UPDATED_ORDER_NUMBER
        defaultOrderShouldBeFound("orderNumber.in=" + DEFAULT_ORDER_NUMBER + "," + UPDATED_ORDER_NUMBER);

        // Get all the orderList where orderNumber equals to UPDATED_ORDER_NUMBER
        defaultOrderShouldNotBeFound("orderNumber.in=" + UPDATED_ORDER_NUMBER);
    }

    @Test
    @Transactional
    void getAllOrdersByOrderNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where orderNumber is not null
        defaultOrderShouldBeFound("orderNumber.specified=true");

        // Get all the orderList where orderNumber is null
        defaultOrderShouldNotBeFound("orderNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByOrderNumberContainsSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where orderNumber contains DEFAULT_ORDER_NUMBER
        defaultOrderShouldBeFound("orderNumber.contains=" + DEFAULT_ORDER_NUMBER);

        // Get all the orderList where orderNumber contains UPDATED_ORDER_NUMBER
        defaultOrderShouldNotBeFound("orderNumber.contains=" + UPDATED_ORDER_NUMBER);
    }

    @Test
    @Transactional
    void getAllOrdersByOrderNumberNotContainsSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where orderNumber does not contain DEFAULT_ORDER_NUMBER
        defaultOrderShouldNotBeFound("orderNumber.doesNotContain=" + DEFAULT_ORDER_NUMBER);

        // Get all the orderList where orderNumber does not contain UPDATED_ORDER_NUMBER
        defaultOrderShouldBeFound("orderNumber.doesNotContain=" + UPDATED_ORDER_NUMBER);
    }

    @Test
    @Transactional
    void getAllOrdersByCustomerCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where customerCode equals to DEFAULT_CUSTOMER_CODE
        defaultOrderShouldBeFound("customerCode.equals=" + DEFAULT_CUSTOMER_CODE);

        // Get all the orderList where customerCode equals to UPDATED_CUSTOMER_CODE
        defaultOrderShouldNotBeFound("customerCode.equals=" + UPDATED_CUSTOMER_CODE);
    }

    @Test
    @Transactional
    void getAllOrdersByCustomerCodeIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where customerCode in DEFAULT_CUSTOMER_CODE or UPDATED_CUSTOMER_CODE
        defaultOrderShouldBeFound("customerCode.in=" + DEFAULT_CUSTOMER_CODE + "," + UPDATED_CUSTOMER_CODE);

        // Get all the orderList where customerCode equals to UPDATED_CUSTOMER_CODE
        defaultOrderShouldNotBeFound("customerCode.in=" + UPDATED_CUSTOMER_CODE);
    }

    @Test
    @Transactional
    void getAllOrdersByCustomerCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where customerCode is not null
        defaultOrderShouldBeFound("customerCode.specified=true");

        // Get all the orderList where customerCode is null
        defaultOrderShouldNotBeFound("customerCode.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByCustomerCodeContainsSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where customerCode contains DEFAULT_CUSTOMER_CODE
        defaultOrderShouldBeFound("customerCode.contains=" + DEFAULT_CUSTOMER_CODE);

        // Get all the orderList where customerCode contains UPDATED_CUSTOMER_CODE
        defaultOrderShouldNotBeFound("customerCode.contains=" + UPDATED_CUSTOMER_CODE);
    }

    @Test
    @Transactional
    void getAllOrdersByCustomerCodeNotContainsSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where customerCode does not contain DEFAULT_CUSTOMER_CODE
        defaultOrderShouldNotBeFound("customerCode.doesNotContain=" + DEFAULT_CUSTOMER_CODE);

        // Get all the orderList where customerCode does not contain UPDATED_CUSTOMER_CODE
        defaultOrderShouldBeFound("customerCode.doesNotContain=" + UPDATED_CUSTOMER_CODE);
    }

    @Test
    @Transactional
    void getAllOrdersByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where createdDate equals to DEFAULT_CREATED_DATE
        defaultOrderShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the orderList where createdDate equals to UPDATED_CREATED_DATE
        defaultOrderShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllOrdersByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultOrderShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the orderList where createdDate equals to UPDATED_CREATED_DATE
        defaultOrderShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllOrdersByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where createdDate is not null
        defaultOrderShouldBeFound("createdDate.specified=true");

        // Get all the orderList where createdDate is null
        defaultOrderShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByTransactionIDIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where transactionID equals to DEFAULT_TRANSACTION_ID
        defaultOrderShouldBeFound("transactionID.equals=" + DEFAULT_TRANSACTION_ID);

        // Get all the orderList where transactionID equals to UPDATED_TRANSACTION_ID
        defaultOrderShouldNotBeFound("transactionID.equals=" + UPDATED_TRANSACTION_ID);
    }

    @Test
    @Transactional
    void getAllOrdersByTransactionIDIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where transactionID in DEFAULT_TRANSACTION_ID or UPDATED_TRANSACTION_ID
        defaultOrderShouldBeFound("transactionID.in=" + DEFAULT_TRANSACTION_ID + "," + UPDATED_TRANSACTION_ID);

        // Get all the orderList where transactionID equals to UPDATED_TRANSACTION_ID
        defaultOrderShouldNotBeFound("transactionID.in=" + UPDATED_TRANSACTION_ID);
    }

    @Test
    @Transactional
    void getAllOrdersByTransactionIDIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where transactionID is not null
        defaultOrderShouldBeFound("transactionID.specified=true");

        // Get all the orderList where transactionID is null
        defaultOrderShouldNotBeFound("transactionID.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByTransactionIDContainsSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where transactionID contains DEFAULT_TRANSACTION_ID
        defaultOrderShouldBeFound("transactionID.contains=" + DEFAULT_TRANSACTION_ID);

        // Get all the orderList where transactionID contains UPDATED_TRANSACTION_ID
        defaultOrderShouldNotBeFound("transactionID.contains=" + UPDATED_TRANSACTION_ID);
    }

    @Test
    @Transactional
    void getAllOrdersByTransactionIDNotContainsSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where transactionID does not contain DEFAULT_TRANSACTION_ID
        defaultOrderShouldNotBeFound("transactionID.doesNotContain=" + DEFAULT_TRANSACTION_ID);

        // Get all the orderList where transactionID does not contain UPDATED_TRANSACTION_ID
        defaultOrderShouldBeFound("transactionID.doesNotContain=" + UPDATED_TRANSACTION_ID);
    }

    @Test
    @Transactional
    void getAllOrdersByLocationCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where locationCode equals to DEFAULT_LOCATION_CODE
        defaultOrderShouldBeFound("locationCode.equals=" + DEFAULT_LOCATION_CODE);

        // Get all the orderList where locationCode equals to UPDATED_LOCATION_CODE
        defaultOrderShouldNotBeFound("locationCode.equals=" + UPDATED_LOCATION_CODE);
    }

    @Test
    @Transactional
    void getAllOrdersByLocationCodeIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where locationCode in DEFAULT_LOCATION_CODE or UPDATED_LOCATION_CODE
        defaultOrderShouldBeFound("locationCode.in=" + DEFAULT_LOCATION_CODE + "," + UPDATED_LOCATION_CODE);

        // Get all the orderList where locationCode equals to UPDATED_LOCATION_CODE
        defaultOrderShouldNotBeFound("locationCode.in=" + UPDATED_LOCATION_CODE);
    }

    @Test
    @Transactional
    void getAllOrdersByLocationCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where locationCode is not null
        defaultOrderShouldBeFound("locationCode.specified=true");

        // Get all the orderList where locationCode is null
        defaultOrderShouldNotBeFound("locationCode.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByLocationCodeContainsSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where locationCode contains DEFAULT_LOCATION_CODE
        defaultOrderShouldBeFound("locationCode.contains=" + DEFAULT_LOCATION_CODE);

        // Get all the orderList where locationCode contains UPDATED_LOCATION_CODE
        defaultOrderShouldNotBeFound("locationCode.contains=" + UPDATED_LOCATION_CODE);
    }

    @Test
    @Transactional
    void getAllOrdersByLocationCodeNotContainsSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where locationCode does not contain DEFAULT_LOCATION_CODE
        defaultOrderShouldNotBeFound("locationCode.doesNotContain=" + DEFAULT_LOCATION_CODE);

        // Get all the orderList where locationCode does not contain UPDATED_LOCATION_CODE
        defaultOrderShouldBeFound("locationCode.doesNotContain=" + UPDATED_LOCATION_CODE);
    }

    @Test
    @Transactional
    void getAllOrdersByTenantCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where tenantCode equals to DEFAULT_TENANT_CODE
        defaultOrderShouldBeFound("tenantCode.equals=" + DEFAULT_TENANT_CODE);

        // Get all the orderList where tenantCode equals to UPDATED_TENANT_CODE
        defaultOrderShouldNotBeFound("tenantCode.equals=" + UPDATED_TENANT_CODE);
    }

    @Test
    @Transactional
    void getAllOrdersByTenantCodeIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where tenantCode in DEFAULT_TENANT_CODE or UPDATED_TENANT_CODE
        defaultOrderShouldBeFound("tenantCode.in=" + DEFAULT_TENANT_CODE + "," + UPDATED_TENANT_CODE);

        // Get all the orderList where tenantCode equals to UPDATED_TENANT_CODE
        defaultOrderShouldNotBeFound("tenantCode.in=" + UPDATED_TENANT_CODE);
    }

    @Test
    @Transactional
    void getAllOrdersByTenantCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where tenantCode is not null
        defaultOrderShouldBeFound("tenantCode.specified=true");

        // Get all the orderList where tenantCode is null
        defaultOrderShouldNotBeFound("tenantCode.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByTenantCodeContainsSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where tenantCode contains DEFAULT_TENANT_CODE
        defaultOrderShouldBeFound("tenantCode.contains=" + DEFAULT_TENANT_CODE);

        // Get all the orderList where tenantCode contains UPDATED_TENANT_CODE
        defaultOrderShouldNotBeFound("tenantCode.contains=" + UPDATED_TENANT_CODE);
    }

    @Test
    @Transactional
    void getAllOrdersByTenantCodeNotContainsSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where tenantCode does not contain DEFAULT_TENANT_CODE
        defaultOrderShouldNotBeFound("tenantCode.doesNotContain=" + DEFAULT_TENANT_CODE);

        // Get all the orderList where tenantCode does not contain UPDATED_TENANT_CODE
        defaultOrderShouldBeFound("tenantCode.doesNotContain=" + UPDATED_TENANT_CODE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOrderShouldBeFound(String filter) throws Exception {
        restOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(order.getId().intValue())))
            .andExpect(jsonPath("$.[*].orderID").value(hasItem(DEFAULT_ORDER_ID)))
            .andExpect(jsonPath("$.[*].orderNumber").value(hasItem(DEFAULT_ORDER_NUMBER)))
            .andExpect(jsonPath("$.[*].customerCode").value(hasItem(DEFAULT_CUSTOMER_CODE)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].transactionID").value(hasItem(DEFAULT_TRANSACTION_ID)))
            .andExpect(jsonPath("$.[*].locationCode").value(hasItem(DEFAULT_LOCATION_CODE)))
            .andExpect(jsonPath("$.[*].tenantCode").value(hasItem(DEFAULT_TENANT_CODE)));

        // Check, that the count call also returns 1
        restOrderMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultOrderShouldNotBeFound(String filter) throws Exception {
        restOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restOrderMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingOrder() throws Exception {
        // Get the order
        restOrderMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOrder() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        int databaseSizeBeforeUpdate = orderRepository.findAll().size();

        // Update the order
        Order updatedOrder = orderRepository.findById(order.getId()).get();
        // Disconnect from session so that the updates on updatedOrder are not directly saved in db
        em.detach(updatedOrder);
        updatedOrder
            .orderID(UPDATED_ORDER_ID)
            .orderNumber(UPDATED_ORDER_NUMBER)
            .customerCode(UPDATED_CUSTOMER_CODE)
            .createdDate(UPDATED_CREATED_DATE)
            .transactionID(UPDATED_TRANSACTION_ID)
            .locationCode(UPDATED_LOCATION_CODE)
            .tenantCode(UPDATED_TENANT_CODE);

        restOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedOrder.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedOrder))
            )
            .andExpect(status().isOk());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
        Order testOrder = orderList.get(orderList.size() - 1);
        assertThat(testOrder.getOrderID()).isEqualTo(UPDATED_ORDER_ID);
        assertThat(testOrder.getOrderNumber()).isEqualTo(UPDATED_ORDER_NUMBER);
        assertThat(testOrder.getCustomerCode()).isEqualTo(UPDATED_CUSTOMER_CODE);
        assertThat(testOrder.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testOrder.getTransactionID()).isEqualTo(UPDATED_TRANSACTION_ID);
        assertThat(testOrder.getLocationCode()).isEqualTo(UPDATED_LOCATION_CODE);
        assertThat(testOrder.getTenantCode()).isEqualTo(UPDATED_TENANT_CODE);
    }

    @Test
    @Transactional
    void putNonExistingOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, order.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(order))
            )
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(order))
            )
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOrderWithPatch() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        int databaseSizeBeforeUpdate = orderRepository.findAll().size();

        // Update the order using partial update
        Order partialUpdatedOrder = new Order();
        partialUpdatedOrder.setId(order.getId());

        partialUpdatedOrder
            .orderID(UPDATED_ORDER_ID)
            .customerCode(UPDATED_CUSTOMER_CODE)
            .createdDate(UPDATED_CREATED_DATE)
            .transactionID(UPDATED_TRANSACTION_ID)
            .locationCode(UPDATED_LOCATION_CODE)
            .tenantCode(UPDATED_TENANT_CODE);

        restOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrder))
            )
            .andExpect(status().isOk());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
        Order testOrder = orderList.get(orderList.size() - 1);
        assertThat(testOrder.getOrderID()).isEqualTo(UPDATED_ORDER_ID);
        assertThat(testOrder.getOrderNumber()).isEqualTo(DEFAULT_ORDER_NUMBER);
        assertThat(testOrder.getCustomerCode()).isEqualTo(UPDATED_CUSTOMER_CODE);
        assertThat(testOrder.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testOrder.getTransactionID()).isEqualTo(UPDATED_TRANSACTION_ID);
        assertThat(testOrder.getLocationCode()).isEqualTo(UPDATED_LOCATION_CODE);
        assertThat(testOrder.getTenantCode()).isEqualTo(UPDATED_TENANT_CODE);
    }

    @Test
    @Transactional
    void fullUpdateOrderWithPatch() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        int databaseSizeBeforeUpdate = orderRepository.findAll().size();

        // Update the order using partial update
        Order partialUpdatedOrder = new Order();
        partialUpdatedOrder.setId(order.getId());

        partialUpdatedOrder
            .orderID(UPDATED_ORDER_ID)
            .orderNumber(UPDATED_ORDER_NUMBER)
            .customerCode(UPDATED_CUSTOMER_CODE)
            .createdDate(UPDATED_CREATED_DATE)
            .transactionID(UPDATED_TRANSACTION_ID)
            .locationCode(UPDATED_LOCATION_CODE)
            .tenantCode(UPDATED_TENANT_CODE);

        restOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrder))
            )
            .andExpect(status().isOk());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
        Order testOrder = orderList.get(orderList.size() - 1);
        assertThat(testOrder.getOrderID()).isEqualTo(UPDATED_ORDER_ID);
        assertThat(testOrder.getOrderNumber()).isEqualTo(UPDATED_ORDER_NUMBER);
        assertThat(testOrder.getCustomerCode()).isEqualTo(UPDATED_CUSTOMER_CODE);
        assertThat(testOrder.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testOrder.getTransactionID()).isEqualTo(UPDATED_TRANSACTION_ID);
        assertThat(testOrder.getLocationCode()).isEqualTo(UPDATED_LOCATION_CODE);
        assertThat(testOrder.getTenantCode()).isEqualTo(UPDATED_TENANT_CODE);
    }

    @Test
    @Transactional
    void patchNonExistingOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, order.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(order))
            )
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(order))
            )
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOrder() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        int databaseSizeBeforeDelete = orderRepository.findAll().size();

        // Delete the order
        restOrderMockMvc
            .perform(delete(ENTITY_API_URL_ID, order.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
