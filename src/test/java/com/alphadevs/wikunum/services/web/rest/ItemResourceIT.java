package com.alphadevs.wikunum.services.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.alphadevs.wikunum.services.IntegrationTest;
import com.alphadevs.wikunum.services.domain.Item;
import com.alphadevs.wikunum.services.repository.ItemRepository;
import com.alphadevs.wikunum.services.service.criteria.ItemCriteria;
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
 * Integration tests for the {@link ItemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ItemResourceIT {

    private static final String DEFAULT_ITEM_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ITEM_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_ITEM_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ITEM_NAME = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Double DEFAULT_UNIT_PRICE = 1D;
    private static final Double UPDATED_UNIT_PRICE = 2D;
    private static final Double SMALLER_UNIT_PRICE = 1D - 1D;

    private static final String DEFAULT_TRANSACTION_ID = "AAAAAAAAAA";
    private static final String UPDATED_TRANSACTION_ID = "BBBBBBBBBB";

    private static final String DEFAULT_LOCATION_CODE = "AAAAAAAAAA";
    private static final String UPDATED_LOCATION_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_TENANT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_TENANT_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restItemMockMvc;

    private Item item;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Item createEntity(EntityManager em) {
        Item item = new Item()
            .itemCode(DEFAULT_ITEM_CODE)
            .itemName(DEFAULT_ITEM_NAME)
            .createdDate(DEFAULT_CREATED_DATE)
            .unitPrice(DEFAULT_UNIT_PRICE)
            .transactionID(DEFAULT_TRANSACTION_ID)
            .locationCode(DEFAULT_LOCATION_CODE)
            .tenantCode(DEFAULT_TENANT_CODE);
        return item;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Item createUpdatedEntity(EntityManager em) {
        Item item = new Item()
            .itemCode(UPDATED_ITEM_CODE)
            .itemName(UPDATED_ITEM_NAME)
            .createdDate(UPDATED_CREATED_DATE)
            .unitPrice(UPDATED_UNIT_PRICE)
            .transactionID(UPDATED_TRANSACTION_ID)
            .locationCode(UPDATED_LOCATION_CODE)
            .tenantCode(UPDATED_TENANT_CODE);
        return item;
    }

    @BeforeEach
    public void initTest() {
        item = createEntity(em);
    }

    @Test
    @Transactional
    void createItem() throws Exception {
        int databaseSizeBeforeCreate = itemRepository.findAll().size();
        // Create the Item
        restItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(item)))
            .andExpect(status().isCreated());

        // Validate the Item in the database
        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList).hasSize(databaseSizeBeforeCreate + 1);
        Item testItem = itemList.get(itemList.size() - 1);
        assertThat(testItem.getItemCode()).isEqualTo(DEFAULT_ITEM_CODE);
        assertThat(testItem.getItemName()).isEqualTo(DEFAULT_ITEM_NAME);
        assertThat(testItem.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testItem.getUnitPrice()).isEqualTo(DEFAULT_UNIT_PRICE);
        assertThat(testItem.getTransactionID()).isEqualTo(DEFAULT_TRANSACTION_ID);
        assertThat(testItem.getLocationCode()).isEqualTo(DEFAULT_LOCATION_CODE);
        assertThat(testItem.getTenantCode()).isEqualTo(DEFAULT_TENANT_CODE);
    }

    @Test
    @Transactional
    void createItemWithExistingId() throws Exception {
        // Create the Item with an existing ID
        item.setId(1L);

        int databaseSizeBeforeCreate = itemRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(item)))
            .andExpect(status().isBadRequest());

        // Validate the Item in the database
        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkItemCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = itemRepository.findAll().size();
        // set the field null
        item.setItemCode(null);

        // Create the Item, which fails.

        restItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(item)))
            .andExpect(status().isBadRequest());

        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkItemNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = itemRepository.findAll().size();
        // set the field null
        item.setItemName(null);

        // Create the Item, which fails.

        restItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(item)))
            .andExpect(status().isBadRequest());

        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTransactionIDIsRequired() throws Exception {
        int databaseSizeBeforeTest = itemRepository.findAll().size();
        // set the field null
        item.setTransactionID(null);

        // Create the Item, which fails.

        restItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(item)))
            .andExpect(status().isBadRequest());

        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLocationCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = itemRepository.findAll().size();
        // set the field null
        item.setLocationCode(null);

        // Create the Item, which fails.

        restItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(item)))
            .andExpect(status().isBadRequest());

        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTenantCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = itemRepository.findAll().size();
        // set the field null
        item.setTenantCode(null);

        // Create the Item, which fails.

        restItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(item)))
            .andExpect(status().isBadRequest());

        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllItems() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList
        restItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(item.getId().intValue())))
            .andExpect(jsonPath("$.[*].itemCode").value(hasItem(DEFAULT_ITEM_CODE)))
            .andExpect(jsonPath("$.[*].itemName").value(hasItem(DEFAULT_ITEM_NAME)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(DEFAULT_UNIT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].transactionID").value(hasItem(DEFAULT_TRANSACTION_ID)))
            .andExpect(jsonPath("$.[*].locationCode").value(hasItem(DEFAULT_LOCATION_CODE)))
            .andExpect(jsonPath("$.[*].tenantCode").value(hasItem(DEFAULT_TENANT_CODE)));
    }

    @Test
    @Transactional
    void getItem() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get the item
        restItemMockMvc
            .perform(get(ENTITY_API_URL_ID, item.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(item.getId().intValue()))
            .andExpect(jsonPath("$.itemCode").value(DEFAULT_ITEM_CODE))
            .andExpect(jsonPath("$.itemName").value(DEFAULT_ITEM_NAME))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.unitPrice").value(DEFAULT_UNIT_PRICE.doubleValue()))
            .andExpect(jsonPath("$.transactionID").value(DEFAULT_TRANSACTION_ID))
            .andExpect(jsonPath("$.locationCode").value(DEFAULT_LOCATION_CODE))
            .andExpect(jsonPath("$.tenantCode").value(DEFAULT_TENANT_CODE));
    }

    @Test
    @Transactional
    void getItemsByIdFiltering() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        Long id = item.getId();

        defaultItemShouldBeFound("id.equals=" + id);
        defaultItemShouldNotBeFound("id.notEquals=" + id);

        defaultItemShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultItemShouldNotBeFound("id.greaterThan=" + id);

        defaultItemShouldBeFound("id.lessThanOrEqual=" + id);
        defaultItemShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllItemsByItemCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where itemCode equals to DEFAULT_ITEM_CODE
        defaultItemShouldBeFound("itemCode.equals=" + DEFAULT_ITEM_CODE);

        // Get all the itemList where itemCode equals to UPDATED_ITEM_CODE
        defaultItemShouldNotBeFound("itemCode.equals=" + UPDATED_ITEM_CODE);
    }

    @Test
    @Transactional
    void getAllItemsByItemCodeIsInShouldWork() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where itemCode in DEFAULT_ITEM_CODE or UPDATED_ITEM_CODE
        defaultItemShouldBeFound("itemCode.in=" + DEFAULT_ITEM_CODE + "," + UPDATED_ITEM_CODE);

        // Get all the itemList where itemCode equals to UPDATED_ITEM_CODE
        defaultItemShouldNotBeFound("itemCode.in=" + UPDATED_ITEM_CODE);
    }

    @Test
    @Transactional
    void getAllItemsByItemCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where itemCode is not null
        defaultItemShouldBeFound("itemCode.specified=true");

        // Get all the itemList where itemCode is null
        defaultItemShouldNotBeFound("itemCode.specified=false");
    }

    @Test
    @Transactional
    void getAllItemsByItemCodeContainsSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where itemCode contains DEFAULT_ITEM_CODE
        defaultItemShouldBeFound("itemCode.contains=" + DEFAULT_ITEM_CODE);

        // Get all the itemList where itemCode contains UPDATED_ITEM_CODE
        defaultItemShouldNotBeFound("itemCode.contains=" + UPDATED_ITEM_CODE);
    }

    @Test
    @Transactional
    void getAllItemsByItemCodeNotContainsSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where itemCode does not contain DEFAULT_ITEM_CODE
        defaultItemShouldNotBeFound("itemCode.doesNotContain=" + DEFAULT_ITEM_CODE);

        // Get all the itemList where itemCode does not contain UPDATED_ITEM_CODE
        defaultItemShouldBeFound("itemCode.doesNotContain=" + UPDATED_ITEM_CODE);
    }

    @Test
    @Transactional
    void getAllItemsByItemNameIsEqualToSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where itemName equals to DEFAULT_ITEM_NAME
        defaultItemShouldBeFound("itemName.equals=" + DEFAULT_ITEM_NAME);

        // Get all the itemList where itemName equals to UPDATED_ITEM_NAME
        defaultItemShouldNotBeFound("itemName.equals=" + UPDATED_ITEM_NAME);
    }

    @Test
    @Transactional
    void getAllItemsByItemNameIsInShouldWork() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where itemName in DEFAULT_ITEM_NAME or UPDATED_ITEM_NAME
        defaultItemShouldBeFound("itemName.in=" + DEFAULT_ITEM_NAME + "," + UPDATED_ITEM_NAME);

        // Get all the itemList where itemName equals to UPDATED_ITEM_NAME
        defaultItemShouldNotBeFound("itemName.in=" + UPDATED_ITEM_NAME);
    }

    @Test
    @Transactional
    void getAllItemsByItemNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where itemName is not null
        defaultItemShouldBeFound("itemName.specified=true");

        // Get all the itemList where itemName is null
        defaultItemShouldNotBeFound("itemName.specified=false");
    }

    @Test
    @Transactional
    void getAllItemsByItemNameContainsSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where itemName contains DEFAULT_ITEM_NAME
        defaultItemShouldBeFound("itemName.contains=" + DEFAULT_ITEM_NAME);

        // Get all the itemList where itemName contains UPDATED_ITEM_NAME
        defaultItemShouldNotBeFound("itemName.contains=" + UPDATED_ITEM_NAME);
    }

    @Test
    @Transactional
    void getAllItemsByItemNameNotContainsSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where itemName does not contain DEFAULT_ITEM_NAME
        defaultItemShouldNotBeFound("itemName.doesNotContain=" + DEFAULT_ITEM_NAME);

        // Get all the itemList where itemName does not contain UPDATED_ITEM_NAME
        defaultItemShouldBeFound("itemName.doesNotContain=" + UPDATED_ITEM_NAME);
    }

    @Test
    @Transactional
    void getAllItemsByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where createdDate equals to DEFAULT_CREATED_DATE
        defaultItemShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the itemList where createdDate equals to UPDATED_CREATED_DATE
        defaultItemShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllItemsByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultItemShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the itemList where createdDate equals to UPDATED_CREATED_DATE
        defaultItemShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllItemsByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where createdDate is not null
        defaultItemShouldBeFound("createdDate.specified=true");

        // Get all the itemList where createdDate is null
        defaultItemShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllItemsByUnitPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where unitPrice equals to DEFAULT_UNIT_PRICE
        defaultItemShouldBeFound("unitPrice.equals=" + DEFAULT_UNIT_PRICE);

        // Get all the itemList where unitPrice equals to UPDATED_UNIT_PRICE
        defaultItemShouldNotBeFound("unitPrice.equals=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllItemsByUnitPriceIsInShouldWork() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where unitPrice in DEFAULT_UNIT_PRICE or UPDATED_UNIT_PRICE
        defaultItemShouldBeFound("unitPrice.in=" + DEFAULT_UNIT_PRICE + "," + UPDATED_UNIT_PRICE);

        // Get all the itemList where unitPrice equals to UPDATED_UNIT_PRICE
        defaultItemShouldNotBeFound("unitPrice.in=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllItemsByUnitPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where unitPrice is not null
        defaultItemShouldBeFound("unitPrice.specified=true");

        // Get all the itemList where unitPrice is null
        defaultItemShouldNotBeFound("unitPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllItemsByUnitPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where unitPrice is greater than or equal to DEFAULT_UNIT_PRICE
        defaultItemShouldBeFound("unitPrice.greaterThanOrEqual=" + DEFAULT_UNIT_PRICE);

        // Get all the itemList where unitPrice is greater than or equal to UPDATED_UNIT_PRICE
        defaultItemShouldNotBeFound("unitPrice.greaterThanOrEqual=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllItemsByUnitPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where unitPrice is less than or equal to DEFAULT_UNIT_PRICE
        defaultItemShouldBeFound("unitPrice.lessThanOrEqual=" + DEFAULT_UNIT_PRICE);

        // Get all the itemList where unitPrice is less than or equal to SMALLER_UNIT_PRICE
        defaultItemShouldNotBeFound("unitPrice.lessThanOrEqual=" + SMALLER_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllItemsByUnitPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where unitPrice is less than DEFAULT_UNIT_PRICE
        defaultItemShouldNotBeFound("unitPrice.lessThan=" + DEFAULT_UNIT_PRICE);

        // Get all the itemList where unitPrice is less than UPDATED_UNIT_PRICE
        defaultItemShouldBeFound("unitPrice.lessThan=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllItemsByUnitPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where unitPrice is greater than DEFAULT_UNIT_PRICE
        defaultItemShouldNotBeFound("unitPrice.greaterThan=" + DEFAULT_UNIT_PRICE);

        // Get all the itemList where unitPrice is greater than SMALLER_UNIT_PRICE
        defaultItemShouldBeFound("unitPrice.greaterThan=" + SMALLER_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllItemsByTransactionIDIsEqualToSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where transactionID equals to DEFAULT_TRANSACTION_ID
        defaultItemShouldBeFound("transactionID.equals=" + DEFAULT_TRANSACTION_ID);

        // Get all the itemList where transactionID equals to UPDATED_TRANSACTION_ID
        defaultItemShouldNotBeFound("transactionID.equals=" + UPDATED_TRANSACTION_ID);
    }

    @Test
    @Transactional
    void getAllItemsByTransactionIDIsInShouldWork() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where transactionID in DEFAULT_TRANSACTION_ID or UPDATED_TRANSACTION_ID
        defaultItemShouldBeFound("transactionID.in=" + DEFAULT_TRANSACTION_ID + "," + UPDATED_TRANSACTION_ID);

        // Get all the itemList where transactionID equals to UPDATED_TRANSACTION_ID
        defaultItemShouldNotBeFound("transactionID.in=" + UPDATED_TRANSACTION_ID);
    }

    @Test
    @Transactional
    void getAllItemsByTransactionIDIsNullOrNotNull() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where transactionID is not null
        defaultItemShouldBeFound("transactionID.specified=true");

        // Get all the itemList where transactionID is null
        defaultItemShouldNotBeFound("transactionID.specified=false");
    }

    @Test
    @Transactional
    void getAllItemsByTransactionIDContainsSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where transactionID contains DEFAULT_TRANSACTION_ID
        defaultItemShouldBeFound("transactionID.contains=" + DEFAULT_TRANSACTION_ID);

        // Get all the itemList where transactionID contains UPDATED_TRANSACTION_ID
        defaultItemShouldNotBeFound("transactionID.contains=" + UPDATED_TRANSACTION_ID);
    }

    @Test
    @Transactional
    void getAllItemsByTransactionIDNotContainsSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where transactionID does not contain DEFAULT_TRANSACTION_ID
        defaultItemShouldNotBeFound("transactionID.doesNotContain=" + DEFAULT_TRANSACTION_ID);

        // Get all the itemList where transactionID does not contain UPDATED_TRANSACTION_ID
        defaultItemShouldBeFound("transactionID.doesNotContain=" + UPDATED_TRANSACTION_ID);
    }

    @Test
    @Transactional
    void getAllItemsByLocationCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where locationCode equals to DEFAULT_LOCATION_CODE
        defaultItemShouldBeFound("locationCode.equals=" + DEFAULT_LOCATION_CODE);

        // Get all the itemList where locationCode equals to UPDATED_LOCATION_CODE
        defaultItemShouldNotBeFound("locationCode.equals=" + UPDATED_LOCATION_CODE);
    }

    @Test
    @Transactional
    void getAllItemsByLocationCodeIsInShouldWork() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where locationCode in DEFAULT_LOCATION_CODE or UPDATED_LOCATION_CODE
        defaultItemShouldBeFound("locationCode.in=" + DEFAULT_LOCATION_CODE + "," + UPDATED_LOCATION_CODE);

        // Get all the itemList where locationCode equals to UPDATED_LOCATION_CODE
        defaultItemShouldNotBeFound("locationCode.in=" + UPDATED_LOCATION_CODE);
    }

    @Test
    @Transactional
    void getAllItemsByLocationCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where locationCode is not null
        defaultItemShouldBeFound("locationCode.specified=true");

        // Get all the itemList where locationCode is null
        defaultItemShouldNotBeFound("locationCode.specified=false");
    }

    @Test
    @Transactional
    void getAllItemsByLocationCodeContainsSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where locationCode contains DEFAULT_LOCATION_CODE
        defaultItemShouldBeFound("locationCode.contains=" + DEFAULT_LOCATION_CODE);

        // Get all the itemList where locationCode contains UPDATED_LOCATION_CODE
        defaultItemShouldNotBeFound("locationCode.contains=" + UPDATED_LOCATION_CODE);
    }

    @Test
    @Transactional
    void getAllItemsByLocationCodeNotContainsSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where locationCode does not contain DEFAULT_LOCATION_CODE
        defaultItemShouldNotBeFound("locationCode.doesNotContain=" + DEFAULT_LOCATION_CODE);

        // Get all the itemList where locationCode does not contain UPDATED_LOCATION_CODE
        defaultItemShouldBeFound("locationCode.doesNotContain=" + UPDATED_LOCATION_CODE);
    }

    @Test
    @Transactional
    void getAllItemsByTenantCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where tenantCode equals to DEFAULT_TENANT_CODE
        defaultItemShouldBeFound("tenantCode.equals=" + DEFAULT_TENANT_CODE);

        // Get all the itemList where tenantCode equals to UPDATED_TENANT_CODE
        defaultItemShouldNotBeFound("tenantCode.equals=" + UPDATED_TENANT_CODE);
    }

    @Test
    @Transactional
    void getAllItemsByTenantCodeIsInShouldWork() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where tenantCode in DEFAULT_TENANT_CODE or UPDATED_TENANT_CODE
        defaultItemShouldBeFound("tenantCode.in=" + DEFAULT_TENANT_CODE + "," + UPDATED_TENANT_CODE);

        // Get all the itemList where tenantCode equals to UPDATED_TENANT_CODE
        defaultItemShouldNotBeFound("tenantCode.in=" + UPDATED_TENANT_CODE);
    }

    @Test
    @Transactional
    void getAllItemsByTenantCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where tenantCode is not null
        defaultItemShouldBeFound("tenantCode.specified=true");

        // Get all the itemList where tenantCode is null
        defaultItemShouldNotBeFound("tenantCode.specified=false");
    }

    @Test
    @Transactional
    void getAllItemsByTenantCodeContainsSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where tenantCode contains DEFAULT_TENANT_CODE
        defaultItemShouldBeFound("tenantCode.contains=" + DEFAULT_TENANT_CODE);

        // Get all the itemList where tenantCode contains UPDATED_TENANT_CODE
        defaultItemShouldNotBeFound("tenantCode.contains=" + UPDATED_TENANT_CODE);
    }

    @Test
    @Transactional
    void getAllItemsByTenantCodeNotContainsSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where tenantCode does not contain DEFAULT_TENANT_CODE
        defaultItemShouldNotBeFound("tenantCode.doesNotContain=" + DEFAULT_TENANT_CODE);

        // Get all the itemList where tenantCode does not contain UPDATED_TENANT_CODE
        defaultItemShouldBeFound("tenantCode.doesNotContain=" + UPDATED_TENANT_CODE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultItemShouldBeFound(String filter) throws Exception {
        restItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(item.getId().intValue())))
            .andExpect(jsonPath("$.[*].itemCode").value(hasItem(DEFAULT_ITEM_CODE)))
            .andExpect(jsonPath("$.[*].itemName").value(hasItem(DEFAULT_ITEM_NAME)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(DEFAULT_UNIT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].transactionID").value(hasItem(DEFAULT_TRANSACTION_ID)))
            .andExpect(jsonPath("$.[*].locationCode").value(hasItem(DEFAULT_LOCATION_CODE)))
            .andExpect(jsonPath("$.[*].tenantCode").value(hasItem(DEFAULT_TENANT_CODE)));

        // Check, that the count call also returns 1
        restItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultItemShouldNotBeFound(String filter) throws Exception {
        restItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingItem() throws Exception {
        // Get the item
        restItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingItem() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        int databaseSizeBeforeUpdate = itemRepository.findAll().size();

        // Update the item
        Item updatedItem = itemRepository.findById(item.getId()).get();
        // Disconnect from session so that the updates on updatedItem are not directly saved in db
        em.detach(updatedItem);
        updatedItem
            .itemCode(UPDATED_ITEM_CODE)
            .itemName(UPDATED_ITEM_NAME)
            .createdDate(UPDATED_CREATED_DATE)
            .unitPrice(UPDATED_UNIT_PRICE)
            .transactionID(UPDATED_TRANSACTION_ID)
            .locationCode(UPDATED_LOCATION_CODE)
            .tenantCode(UPDATED_TENANT_CODE);

        restItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedItem.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedItem))
            )
            .andExpect(status().isOk());

        // Validate the Item in the database
        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList).hasSize(databaseSizeBeforeUpdate);
        Item testItem = itemList.get(itemList.size() - 1);
        assertThat(testItem.getItemCode()).isEqualTo(UPDATED_ITEM_CODE);
        assertThat(testItem.getItemName()).isEqualTo(UPDATED_ITEM_NAME);
        assertThat(testItem.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testItem.getUnitPrice()).isEqualTo(UPDATED_UNIT_PRICE);
        assertThat(testItem.getTransactionID()).isEqualTo(UPDATED_TRANSACTION_ID);
        assertThat(testItem.getLocationCode()).isEqualTo(UPDATED_LOCATION_CODE);
        assertThat(testItem.getTenantCode()).isEqualTo(UPDATED_TENANT_CODE);
    }

    @Test
    @Transactional
    void putNonExistingItem() throws Exception {
        int databaseSizeBeforeUpdate = itemRepository.findAll().size();
        item.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, item.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(item))
            )
            .andExpect(status().isBadRequest());

        // Validate the Item in the database
        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchItem() throws Exception {
        int databaseSizeBeforeUpdate = itemRepository.findAll().size();
        item.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(item))
            )
            .andExpect(status().isBadRequest());

        // Validate the Item in the database
        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamItem() throws Exception {
        int databaseSizeBeforeUpdate = itemRepository.findAll().size();
        item.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restItemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(item)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Item in the database
        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateItemWithPatch() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        int databaseSizeBeforeUpdate = itemRepository.findAll().size();

        // Update the item using partial update
        Item partialUpdatedItem = new Item();
        partialUpdatedItem.setId(item.getId());

        partialUpdatedItem.unitPrice(UPDATED_UNIT_PRICE);

        restItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedItem))
            )
            .andExpect(status().isOk());

        // Validate the Item in the database
        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList).hasSize(databaseSizeBeforeUpdate);
        Item testItem = itemList.get(itemList.size() - 1);
        assertThat(testItem.getItemCode()).isEqualTo(DEFAULT_ITEM_CODE);
        assertThat(testItem.getItemName()).isEqualTo(DEFAULT_ITEM_NAME);
        assertThat(testItem.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testItem.getUnitPrice()).isEqualTo(UPDATED_UNIT_PRICE);
        assertThat(testItem.getTransactionID()).isEqualTo(DEFAULT_TRANSACTION_ID);
        assertThat(testItem.getLocationCode()).isEqualTo(DEFAULT_LOCATION_CODE);
        assertThat(testItem.getTenantCode()).isEqualTo(DEFAULT_TENANT_CODE);
    }

    @Test
    @Transactional
    void fullUpdateItemWithPatch() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        int databaseSizeBeforeUpdate = itemRepository.findAll().size();

        // Update the item using partial update
        Item partialUpdatedItem = new Item();
        partialUpdatedItem.setId(item.getId());

        partialUpdatedItem
            .itemCode(UPDATED_ITEM_CODE)
            .itemName(UPDATED_ITEM_NAME)
            .createdDate(UPDATED_CREATED_DATE)
            .unitPrice(UPDATED_UNIT_PRICE)
            .transactionID(UPDATED_TRANSACTION_ID)
            .locationCode(UPDATED_LOCATION_CODE)
            .tenantCode(UPDATED_TENANT_CODE);

        restItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedItem))
            )
            .andExpect(status().isOk());

        // Validate the Item in the database
        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList).hasSize(databaseSizeBeforeUpdate);
        Item testItem = itemList.get(itemList.size() - 1);
        assertThat(testItem.getItemCode()).isEqualTo(UPDATED_ITEM_CODE);
        assertThat(testItem.getItemName()).isEqualTo(UPDATED_ITEM_NAME);
        assertThat(testItem.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testItem.getUnitPrice()).isEqualTo(UPDATED_UNIT_PRICE);
        assertThat(testItem.getTransactionID()).isEqualTo(UPDATED_TRANSACTION_ID);
        assertThat(testItem.getLocationCode()).isEqualTo(UPDATED_LOCATION_CODE);
        assertThat(testItem.getTenantCode()).isEqualTo(UPDATED_TENANT_CODE);
    }

    @Test
    @Transactional
    void patchNonExistingItem() throws Exception {
        int databaseSizeBeforeUpdate = itemRepository.findAll().size();
        item.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, item.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(item))
            )
            .andExpect(status().isBadRequest());

        // Validate the Item in the database
        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchItem() throws Exception {
        int databaseSizeBeforeUpdate = itemRepository.findAll().size();
        item.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(item))
            )
            .andExpect(status().isBadRequest());

        // Validate the Item in the database
        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamItem() throws Exception {
        int databaseSizeBeforeUpdate = itemRepository.findAll().size();
        item.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restItemMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(item)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Item in the database
        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteItem() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        int databaseSizeBeforeDelete = itemRepository.findAll().size();

        // Delete the item
        restItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, item.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
