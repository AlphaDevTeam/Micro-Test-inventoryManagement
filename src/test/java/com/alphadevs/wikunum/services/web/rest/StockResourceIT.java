package com.alphadevs.wikunum.services.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.alphadevs.wikunum.services.IntegrationTest;
import com.alphadevs.wikunum.services.domain.Item;
import com.alphadevs.wikunum.services.domain.Stock;
import com.alphadevs.wikunum.services.repository.StockRepository;
import com.alphadevs.wikunum.services.service.StockService;
import com.alphadevs.wikunum.services.service.criteria.StockCriteria;
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
 * Integration tests for the {@link StockResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class StockResourceIT {

    private static final Double DEFAULT_STOCK_QTY = 1D;
    private static final Double UPDATED_STOCK_QTY = 2D;
    private static final Double SMALLER_STOCK_QTY = 1D - 1D;

    private static final String DEFAULT_LOCATION_CODE = "AAAAAAAAAA";
    private static final String UPDATED_LOCATION_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_TENANT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_TENANT_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/stocks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StockRepository stockRepository;

    @Mock
    private StockRepository stockRepositoryMock;

    @Mock
    private StockService stockServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStockMockMvc;

    private Stock stock;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Stock createEntity(EntityManager em) {
        Stock stock = new Stock().stockQty(DEFAULT_STOCK_QTY).locationCode(DEFAULT_LOCATION_CODE).tenantCode(DEFAULT_TENANT_CODE);
        return stock;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Stock createUpdatedEntity(EntityManager em) {
        Stock stock = new Stock().stockQty(UPDATED_STOCK_QTY).locationCode(UPDATED_LOCATION_CODE).tenantCode(UPDATED_TENANT_CODE);
        return stock;
    }

    @BeforeEach
    public void initTest() {
        stock = createEntity(em);
    }

    @Test
    @Transactional
    void createStock() throws Exception {
        int databaseSizeBeforeCreate = stockRepository.findAll().size();
        // Create the Stock
        restStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stock)))
            .andExpect(status().isCreated());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeCreate + 1);
        Stock testStock = stockList.get(stockList.size() - 1);
        assertThat(testStock.getStockQty()).isEqualTo(DEFAULT_STOCK_QTY);
        assertThat(testStock.getLocationCode()).isEqualTo(DEFAULT_LOCATION_CODE);
        assertThat(testStock.getTenantCode()).isEqualTo(DEFAULT_TENANT_CODE);
    }

    @Test
    @Transactional
    void createStockWithExistingId() throws Exception {
        // Create the Stock with an existing ID
        stock.setId(1L);

        int databaseSizeBeforeCreate = stockRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stock)))
            .andExpect(status().isBadRequest());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStockQtyIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockRepository.findAll().size();
        // set the field null
        stock.setStockQty(null);

        // Create the Stock, which fails.

        restStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stock)))
            .andExpect(status().isBadRequest());

        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLocationCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockRepository.findAll().size();
        // set the field null
        stock.setLocationCode(null);

        // Create the Stock, which fails.

        restStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stock)))
            .andExpect(status().isBadRequest());

        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTenantCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockRepository.findAll().size();
        // set the field null
        stock.setTenantCode(null);

        // Create the Stock, which fails.

        restStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stock)))
            .andExpect(status().isBadRequest());

        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStocks() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList
        restStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stock.getId().intValue())))
            .andExpect(jsonPath("$.[*].stockQty").value(hasItem(DEFAULT_STOCK_QTY.doubleValue())))
            .andExpect(jsonPath("$.[*].locationCode").value(hasItem(DEFAULT_LOCATION_CODE)))
            .andExpect(jsonPath("$.[*].tenantCode").value(hasItem(DEFAULT_TENANT_CODE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStocksWithEagerRelationshipsIsEnabled() throws Exception {
        when(stockServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStockMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(stockServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStocksWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(stockServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStockMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(stockRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getStock() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get the stock
        restStockMockMvc
            .perform(get(ENTITY_API_URL_ID, stock.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stock.getId().intValue()))
            .andExpect(jsonPath("$.stockQty").value(DEFAULT_STOCK_QTY.doubleValue()))
            .andExpect(jsonPath("$.locationCode").value(DEFAULT_LOCATION_CODE))
            .andExpect(jsonPath("$.tenantCode").value(DEFAULT_TENANT_CODE));
    }

    @Test
    @Transactional
    void getStocksByIdFiltering() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        Long id = stock.getId();

        defaultStockShouldBeFound("id.equals=" + id);
        defaultStockShouldNotBeFound("id.notEquals=" + id);

        defaultStockShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultStockShouldNotBeFound("id.greaterThan=" + id);

        defaultStockShouldBeFound("id.lessThanOrEqual=" + id);
        defaultStockShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllStocksByStockQtyIsEqualToSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where stockQty equals to DEFAULT_STOCK_QTY
        defaultStockShouldBeFound("stockQty.equals=" + DEFAULT_STOCK_QTY);

        // Get all the stockList where stockQty equals to UPDATED_STOCK_QTY
        defaultStockShouldNotBeFound("stockQty.equals=" + UPDATED_STOCK_QTY);
    }

    @Test
    @Transactional
    void getAllStocksByStockQtyIsInShouldWork() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where stockQty in DEFAULT_STOCK_QTY or UPDATED_STOCK_QTY
        defaultStockShouldBeFound("stockQty.in=" + DEFAULT_STOCK_QTY + "," + UPDATED_STOCK_QTY);

        // Get all the stockList where stockQty equals to UPDATED_STOCK_QTY
        defaultStockShouldNotBeFound("stockQty.in=" + UPDATED_STOCK_QTY);
    }

    @Test
    @Transactional
    void getAllStocksByStockQtyIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where stockQty is not null
        defaultStockShouldBeFound("stockQty.specified=true");

        // Get all the stockList where stockQty is null
        defaultStockShouldNotBeFound("stockQty.specified=false");
    }

    @Test
    @Transactional
    void getAllStocksByStockQtyIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where stockQty is greater than or equal to DEFAULT_STOCK_QTY
        defaultStockShouldBeFound("stockQty.greaterThanOrEqual=" + DEFAULT_STOCK_QTY);

        // Get all the stockList where stockQty is greater than or equal to UPDATED_STOCK_QTY
        defaultStockShouldNotBeFound("stockQty.greaterThanOrEqual=" + UPDATED_STOCK_QTY);
    }

    @Test
    @Transactional
    void getAllStocksByStockQtyIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where stockQty is less than or equal to DEFAULT_STOCK_QTY
        defaultStockShouldBeFound("stockQty.lessThanOrEqual=" + DEFAULT_STOCK_QTY);

        // Get all the stockList where stockQty is less than or equal to SMALLER_STOCK_QTY
        defaultStockShouldNotBeFound("stockQty.lessThanOrEqual=" + SMALLER_STOCK_QTY);
    }

    @Test
    @Transactional
    void getAllStocksByStockQtyIsLessThanSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where stockQty is less than DEFAULT_STOCK_QTY
        defaultStockShouldNotBeFound("stockQty.lessThan=" + DEFAULT_STOCK_QTY);

        // Get all the stockList where stockQty is less than UPDATED_STOCK_QTY
        defaultStockShouldBeFound("stockQty.lessThan=" + UPDATED_STOCK_QTY);
    }

    @Test
    @Transactional
    void getAllStocksByStockQtyIsGreaterThanSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where stockQty is greater than DEFAULT_STOCK_QTY
        defaultStockShouldNotBeFound("stockQty.greaterThan=" + DEFAULT_STOCK_QTY);

        // Get all the stockList where stockQty is greater than SMALLER_STOCK_QTY
        defaultStockShouldBeFound("stockQty.greaterThan=" + SMALLER_STOCK_QTY);
    }

    @Test
    @Transactional
    void getAllStocksByLocationCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where locationCode equals to DEFAULT_LOCATION_CODE
        defaultStockShouldBeFound("locationCode.equals=" + DEFAULT_LOCATION_CODE);

        // Get all the stockList where locationCode equals to UPDATED_LOCATION_CODE
        defaultStockShouldNotBeFound("locationCode.equals=" + UPDATED_LOCATION_CODE);
    }

    @Test
    @Transactional
    void getAllStocksByLocationCodeIsInShouldWork() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where locationCode in DEFAULT_LOCATION_CODE or UPDATED_LOCATION_CODE
        defaultStockShouldBeFound("locationCode.in=" + DEFAULT_LOCATION_CODE + "," + UPDATED_LOCATION_CODE);

        // Get all the stockList where locationCode equals to UPDATED_LOCATION_CODE
        defaultStockShouldNotBeFound("locationCode.in=" + UPDATED_LOCATION_CODE);
    }

    @Test
    @Transactional
    void getAllStocksByLocationCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where locationCode is not null
        defaultStockShouldBeFound("locationCode.specified=true");

        // Get all the stockList where locationCode is null
        defaultStockShouldNotBeFound("locationCode.specified=false");
    }

    @Test
    @Transactional
    void getAllStocksByLocationCodeContainsSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where locationCode contains DEFAULT_LOCATION_CODE
        defaultStockShouldBeFound("locationCode.contains=" + DEFAULT_LOCATION_CODE);

        // Get all the stockList where locationCode contains UPDATED_LOCATION_CODE
        defaultStockShouldNotBeFound("locationCode.contains=" + UPDATED_LOCATION_CODE);
    }

    @Test
    @Transactional
    void getAllStocksByLocationCodeNotContainsSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where locationCode does not contain DEFAULT_LOCATION_CODE
        defaultStockShouldNotBeFound("locationCode.doesNotContain=" + DEFAULT_LOCATION_CODE);

        // Get all the stockList where locationCode does not contain UPDATED_LOCATION_CODE
        defaultStockShouldBeFound("locationCode.doesNotContain=" + UPDATED_LOCATION_CODE);
    }

    @Test
    @Transactional
    void getAllStocksByTenantCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where tenantCode equals to DEFAULT_TENANT_CODE
        defaultStockShouldBeFound("tenantCode.equals=" + DEFAULT_TENANT_CODE);

        // Get all the stockList where tenantCode equals to UPDATED_TENANT_CODE
        defaultStockShouldNotBeFound("tenantCode.equals=" + UPDATED_TENANT_CODE);
    }

    @Test
    @Transactional
    void getAllStocksByTenantCodeIsInShouldWork() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where tenantCode in DEFAULT_TENANT_CODE or UPDATED_TENANT_CODE
        defaultStockShouldBeFound("tenantCode.in=" + DEFAULT_TENANT_CODE + "," + UPDATED_TENANT_CODE);

        // Get all the stockList where tenantCode equals to UPDATED_TENANT_CODE
        defaultStockShouldNotBeFound("tenantCode.in=" + UPDATED_TENANT_CODE);
    }

    @Test
    @Transactional
    void getAllStocksByTenantCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where tenantCode is not null
        defaultStockShouldBeFound("tenantCode.specified=true");

        // Get all the stockList where tenantCode is null
        defaultStockShouldNotBeFound("tenantCode.specified=false");
    }

    @Test
    @Transactional
    void getAllStocksByTenantCodeContainsSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where tenantCode contains DEFAULT_TENANT_CODE
        defaultStockShouldBeFound("tenantCode.contains=" + DEFAULT_TENANT_CODE);

        // Get all the stockList where tenantCode contains UPDATED_TENANT_CODE
        defaultStockShouldNotBeFound("tenantCode.contains=" + UPDATED_TENANT_CODE);
    }

    @Test
    @Transactional
    void getAllStocksByTenantCodeNotContainsSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where tenantCode does not contain DEFAULT_TENANT_CODE
        defaultStockShouldNotBeFound("tenantCode.doesNotContain=" + DEFAULT_TENANT_CODE);

        // Get all the stockList where tenantCode does not contain UPDATED_TENANT_CODE
        defaultStockShouldBeFound("tenantCode.doesNotContain=" + UPDATED_TENANT_CODE);
    }

    @Test
    @Transactional
    void getAllStocksByItemIsEqualToSomething() throws Exception {
        Item item;
        if (TestUtil.findAll(em, Item.class).isEmpty()) {
            stockRepository.saveAndFlush(stock);
            item = ItemResourceIT.createEntity(em);
        } else {
            item = TestUtil.findAll(em, Item.class).get(0);
        }
        em.persist(item);
        em.flush();
        stock.setItem(item);
        stockRepository.saveAndFlush(stock);
        Long itemId = item.getId();

        // Get all the stockList where item equals to itemId
        defaultStockShouldBeFound("itemId.equals=" + itemId);

        // Get all the stockList where item equals to (itemId + 1)
        defaultStockShouldNotBeFound("itemId.equals=" + (itemId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStockShouldBeFound(String filter) throws Exception {
        restStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stock.getId().intValue())))
            .andExpect(jsonPath("$.[*].stockQty").value(hasItem(DEFAULT_STOCK_QTY.doubleValue())))
            .andExpect(jsonPath("$.[*].locationCode").value(hasItem(DEFAULT_LOCATION_CODE)))
            .andExpect(jsonPath("$.[*].tenantCode").value(hasItem(DEFAULT_TENANT_CODE)));

        // Check, that the count call also returns 1
        restStockMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStockShouldNotBeFound(String filter) throws Exception {
        restStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStockMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingStock() throws Exception {
        // Get the stock
        restStockMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStock() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        int databaseSizeBeforeUpdate = stockRepository.findAll().size();

        // Update the stock
        Stock updatedStock = stockRepository.findById(stock.getId()).get();
        // Disconnect from session so that the updates on updatedStock are not directly saved in db
        em.detach(updatedStock);
        updatedStock.stockQty(UPDATED_STOCK_QTY).locationCode(UPDATED_LOCATION_CODE).tenantCode(UPDATED_TENANT_CODE);

        restStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedStock.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedStock))
            )
            .andExpect(status().isOk());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
        Stock testStock = stockList.get(stockList.size() - 1);
        assertThat(testStock.getStockQty()).isEqualTo(UPDATED_STOCK_QTY);
        assertThat(testStock.getLocationCode()).isEqualTo(UPDATED_LOCATION_CODE);
        assertThat(testStock.getTenantCode()).isEqualTo(UPDATED_TENANT_CODE);
    }

    @Test
    @Transactional
    void putNonExistingStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().size();
        stock.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stock.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stock))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().size();
        stock.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stock))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().size();
        stock.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stock)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStockWithPatch() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        int databaseSizeBeforeUpdate = stockRepository.findAll().size();

        // Update the stock using partial update
        Stock partialUpdatedStock = new Stock();
        partialUpdatedStock.setId(stock.getId());

        partialUpdatedStock.stockQty(UPDATED_STOCK_QTY).locationCode(UPDATED_LOCATION_CODE);

        restStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStock))
            )
            .andExpect(status().isOk());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
        Stock testStock = stockList.get(stockList.size() - 1);
        assertThat(testStock.getStockQty()).isEqualTo(UPDATED_STOCK_QTY);
        assertThat(testStock.getLocationCode()).isEqualTo(UPDATED_LOCATION_CODE);
        assertThat(testStock.getTenantCode()).isEqualTo(DEFAULT_TENANT_CODE);
    }

    @Test
    @Transactional
    void fullUpdateStockWithPatch() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        int databaseSizeBeforeUpdate = stockRepository.findAll().size();

        // Update the stock using partial update
        Stock partialUpdatedStock = new Stock();
        partialUpdatedStock.setId(stock.getId());

        partialUpdatedStock.stockQty(UPDATED_STOCK_QTY).locationCode(UPDATED_LOCATION_CODE).tenantCode(UPDATED_TENANT_CODE);

        restStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStock))
            )
            .andExpect(status().isOk());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
        Stock testStock = stockList.get(stockList.size() - 1);
        assertThat(testStock.getStockQty()).isEqualTo(UPDATED_STOCK_QTY);
        assertThat(testStock.getLocationCode()).isEqualTo(UPDATED_LOCATION_CODE);
        assertThat(testStock.getTenantCode()).isEqualTo(UPDATED_TENANT_CODE);
    }

    @Test
    @Transactional
    void patchNonExistingStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().size();
        stock.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stock.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stock))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().size();
        stock.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stock))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().size();
        stock.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(stock)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStock() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        int databaseSizeBeforeDelete = stockRepository.findAll().size();

        // Delete the stock
        restStockMockMvc
            .perform(delete(ENTITY_API_URL_ID, stock.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
