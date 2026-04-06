package com.rainbowforest.productcatalogservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.rainbowforest.productcatalogservice.entity.Product;
import com.rainbowforest.productcatalogservice.entity.Category;
import com.rainbowforest.productcatalogservice.service.ProductService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AdminProductControllerTest {

	private static final String PRODUCT_NAME= "test";
    private static final String PRODUCT_CATEGORY = "testCategory";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

	@Test
    public void add_product_controller_should_return201_when_product_isSaved() throws Exception {
		//given
		Category category = new Category();
		category.setCategoryName(PRODUCT_CATEGORY);
		Product product = new Product();
        product.setSku("SKU123");
        product.setProductName(PRODUCT_NAME);
        product.setCategory(category);
        product.setAvailability(1);
    	ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(product);
        
        //when      
        when(productService.addProduct(any(Product.class))).thenReturn(product);

        //then
        mockMvc.perform(post("/admin/products").content(requestJson).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.productName").value(PRODUCT_NAME))
                .andExpect(jsonPath("$.category.categoryName").value(PRODUCT_CATEGORY));

    	verify(productService, times(1)).addProduct(any(Product.class));
        verifyNoMoreInteractions(productService);
    }
	
	@Test
    public void add_product_controller_should_return400_when_product_isNull() throws Exception {
		//given
		Product product = null;			
    	ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(product);

        //then
        mockMvc.perform(post("/admin/products").content(requestJson).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());	
	}

	@Test
	public void update_product_controller_should_return200_when_product_exists() throws Exception {
		Category existingCategory = new Category();
		existingCategory.setCategoryName(PRODUCT_CATEGORY);
		Product existing = new Product();
		existing.setId(1L);
		existing.setProductName(PRODUCT_NAME);
		existing.setCategory(existingCategory);
		existing.setAvailability(5);

		Category updatedCategory = new Category();
		updatedCategory.setCategoryName("updatedCategory");
		Product updatedRequest = new Product();
        updatedRequest.setSku("SKU456");
		updatedRequest.setCategory(updatedCategory);
		updatedRequest.setAvailability(10);

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = objectWriter.writeValueAsString(updatedRequest);

		when(productService.getProductById(1L)).thenReturn(existing);
		when(productService.updateProduct(1L, any(Product.class))).thenReturn(updatedRequest);

		mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/admin/products/1")
				.content(requestJson)
				.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.productName").value("updated"))
				.andExpect(jsonPath("$.category.categoryName").value("updatedCategory"));

		verify(productService, times(1)).getProductById(1L);
		verify(productService, times(1)).updateProduct(1L, any(Product.class));
	}

	@Test
	public void update_product_controller_should_return404_when_product_not_found() throws Exception {
		Product updatedRequest = new Product();
		updatedRequest.setProductName("updated");
		String requestJson = new ObjectMapper().writeValueAsString(updatedRequest);

		when(productService.getProductById(10L)).thenReturn(null);

		mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/admin/products/10")
				.content(requestJson)
				.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isNotFound());
	}
}
