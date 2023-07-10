package com.server.pickplace;

import com.server.pickplace.config.SwaggerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringBootTest
@SpringJUnitConfig(SwaggerConfig.class)
class PickplaceApplicationTests {

	@Test
	void contextLoads() {
	}

}
