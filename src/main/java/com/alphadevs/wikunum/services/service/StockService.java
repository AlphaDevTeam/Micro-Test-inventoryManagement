package com.alphadevs.wikunum.services.service;

import com.alphadevs.wikunum.services.domain.Stock;
import com.alphadevs.wikunum.services.repository.StockRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Stock}.
 */
@Service
@Transactional
public class StockService {

    private final Logger log = LoggerFactory.getLogger(StockService.class);

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    /**
     * Save a stock.
     *
     * @param stock the entity to save.
     * @return the persisted entity.
     */
    public Stock save(Stock stock) {
        log.debug("Request to save Stock : {}", stock);
        return stockRepository.save(stock);
    }

    /**
     * Update a stock.
     *
     * @param stock the entity to save.
     * @return the persisted entity.
     */
    public Stock update(Stock stock) {
        log.debug("Request to update Stock : {}", stock);
        return stockRepository.save(stock);
    }

    /**
     * Partially update a stock.
     *
     * @param stock the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Stock> partialUpdate(Stock stock) {
        log.debug("Request to partially update Stock : {}", stock);

        return stockRepository
            .findById(stock.getId())
            .map(existingStock -> {
                if (stock.getStockQty() != null) {
                    existingStock.setStockQty(stock.getStockQty());
                }
                if (stock.getLocationCode() != null) {
                    existingStock.setLocationCode(stock.getLocationCode());
                }
                if (stock.getTenantCode() != null) {
                    existingStock.setTenantCode(stock.getTenantCode());
                }

                return existingStock;
            })
            .map(stockRepository::save);
    }

    /**
     * Get all the stocks.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Stock> findAll(Pageable pageable) {
        log.debug("Request to get all Stocks");
        return stockRepository.findAll(pageable);
    }

    /**
     * Get all the stocks with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<Stock> findAllWithEagerRelationships(Pageable pageable) {
        return stockRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one stock by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Stock> findOne(Long id) {
        log.debug("Request to get Stock : {}", id);
        return stockRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the stock by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Stock : {}", id);
        stockRepository.deleteById(id);
    }
}
