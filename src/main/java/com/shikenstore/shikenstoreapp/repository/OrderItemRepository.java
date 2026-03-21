package com.shikenstore.shikenstoreapp.repository;

import com.shikenstore.shikenstoreapp.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
