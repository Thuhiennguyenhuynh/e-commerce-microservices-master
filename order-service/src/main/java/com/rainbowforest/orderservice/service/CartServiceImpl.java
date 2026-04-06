package com.rainbowforest.orderservice.service;

import com.rainbowforest.orderservice.domain.Item;
import com.rainbowforest.orderservice.domain.Product;
import com.rainbowforest.orderservice.feignclient.ProductClient;
import com.rainbowforest.orderservice.redis.CartRedisRepository;
import com.rainbowforest.orderservice.utilities.CartUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    
    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);

    @Autowired
    private ProductClient productClient;

    @Autowired
    private CartRedisRepository cartRedisRepository;

    @Override
    public void addItemToCart(String cartId, Long productId, Integer quantity) {
        Product product = productClient.getProductById(productId);
        Item item = new Item(quantity, product, CartUtilities.getSubTotalForItem(product, quantity));
        cartRedisRepository.addItemToCart(cartId, item);
        logger.info("Item added to cart: productId={}, quantity={}", productId, quantity);
    }

    @Override
    public List<Item> getCart(String cartId) {
        Collection<Object> cartCollection = cartRedisRepository.getCart(cartId, Item.class);
        if (cartCollection == null || cartCollection.isEmpty()) {
            return List.of();
        }
        return cartCollection.stream()
            .map(item -> (Item) item)
            .toList();
    }

    @Override
    public void changeItemQuantity(String cartId, Long productId, Integer quantity) {
        List<Item> cart = getCart(cartId);
        for(Item item : cart){
            if((item.getProduct().getId()).equals(productId)){
                cartRedisRepository.deleteItemFromCart(cartId, item);
                item.setQuantity(quantity);
                item.setSubTotal(CartUtilities.getSubTotalForItem(item.getProduct(), quantity));
                cartRedisRepository.addItemToCart(cartId, item);
                logger.info("Item quantity updated: productId={}, newQuantity={}", productId, quantity);
            }
        }
    }

    @Override
    public void deleteItemFromCart(String cartId, Long productId) {
        List<Item> cart = getCart(cartId);
        for(Item item : cart){
            if((item.getProduct().getId()).equals(productId)){
                cartRedisRepository.deleteItemFromCart(cartId, item);
                logger.info("Item removed from cart: productId={}", productId);
            }
        }
    }

    @Override
    public boolean checkIfItemIsExist(String cartId, Long productId) {
        List<Item> cart = getCart(cartId);
        return cart.stream()
            .anyMatch(item -> item.getProduct().getId().equals(productId));
    }

    @Override
    public List<Item> getAllItemsFromCart(String cartId) {
        List<Item> items = getCart(cartId);
        logger.info("Retrieved {} items from cart: {}", items.size(), cartId);
        return items;
    }

    @Override
    public void deleteCart(String cartId) {
        cartRedisRepository.deleteCart(cartId);
        logger.info("Cart deleted: {}", cartId);
    }
}
