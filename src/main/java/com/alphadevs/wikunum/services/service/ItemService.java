package com.alphadevs.wikunum.services.service;

import com.alphadevs.wikunum.services.domain.Item;
import com.alphadevs.wikunum.services.repository.ItemRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Item}.
 */
@Service
@Transactional
public class ItemService {

    private final Logger log = LoggerFactory.getLogger(ItemService.class);

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    /**
     * Save a item.
     *
     * @param item the entity to save.
     * @return the persisted entity.
     */
    public Item save(Item item) {
        log.debug("Request to save Item : {}", item);
        return itemRepository.save(item);
    }

    /**
     * Update a item.
     *
     * @param item the entity to save.
     * @return the persisted entity.
     */
    public Item update(Item item) {
        log.debug("Request to update Item : {}", item);
        return itemRepository.save(item);
    }

    /**
     * Partially update a item.
     *
     * @param item the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Item> partialUpdate(Item item) {
        log.debug("Request to partially update Item : {}", item);

        return itemRepository
            .findById(item.getId())
            .map(existingItem -> {
                if (item.getItemCode() != null) {
                    existingItem.setItemCode(item.getItemCode());
                }
                if (item.getItemName() != null) {
                    existingItem.setItemName(item.getItemName());
                }
                if (item.getCreatedDate() != null) {
                    existingItem.setCreatedDate(item.getCreatedDate());
                }
                if (item.getUnitPrice() != null) {
                    existingItem.setUnitPrice(item.getUnitPrice());
                }
                if (item.getTransactionID() != null) {
                    existingItem.setTransactionID(item.getTransactionID());
                }
                if (item.getLocationCode() != null) {
                    existingItem.setLocationCode(item.getLocationCode());
                }
                if (item.getTenantCode() != null) {
                    existingItem.setTenantCode(item.getTenantCode());
                }

                return existingItem;
            })
            .map(itemRepository::save);
    }

    /**
     * Get all the items.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Item> findAll(Pageable pageable) {
        log.debug("Request to get all Items");
        return itemRepository.findAll(pageable);
    }

    /**
     * Get one item by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Item> findOne(Long id) {
        log.debug("Request to get Item : {}", id);
        return itemRepository.findById(id);
    }

    /**
     * Delete the item by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Item : {}", id);
        itemRepository.deleteById(id);
    }
}
