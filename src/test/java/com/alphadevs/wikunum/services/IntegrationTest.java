package com.alphadevs.wikunum.services;

import com.alphadevs.wikunum.services.InventoryManagementApp;
import com.alphadevs.wikunum.services.config.AsyncSyncConfiguration;
import com.alphadevs.wikunum.services.config.EmbeddedKafka;
import com.alphadevs.wikunum.services.config.EmbeddedSQL;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { InventoryManagementApp.class, AsyncSyncConfiguration.class })
@EmbeddedKafka
@EmbeddedSQL
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public @interface IntegrationTest {
}
