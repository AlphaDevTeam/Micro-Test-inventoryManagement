package com.alphadevs.wikunum.services.repository;

import com.alphadevs.wikunum.services.domain.Item;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Item entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {}
