package com.shikenstore.shikenstoreapp.repository;

import com.shikenstore.shikenstoreapp.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, String> {

    List<OrderEntity> findByUserId(Long userId);

    Optional<OrderEntity> findByOrderNumber(String orderNumber);
}
