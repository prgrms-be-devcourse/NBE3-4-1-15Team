package com.nbe.NBE3_4_1_Team15.domain.order.repository;

import com.nbe.NBE3_4_1_Team15.domain.order.entity.Order;
import com.nbe.NBE3_4_1_Team15.domain.order.type.OrderType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final SpringOrderRepository dbRepository;
    private final List<Order> memRepository = new ArrayList<>();

    public void scheduledTask() {
        dbRepository.saveAll(memRepository);
        memRepository.clear();
    }

    public Order save(Order order) {
        memRepository.add(order);
        return order;
    }

    public List<Order> findAllByConsumerId(Long memberId) {
        List<Order> memOrders = memRepository.stream().filter(order -> order.getConsumer().getId().equals(memberId)).toList();
        List<Order> dbOrders = dbRepository.findAllByConsumer_Id(memberId);

        return Stream.concat(memOrders.stream(), dbOrders.stream()).toList();
    }

    // 추가적으로 Scheduled 에서 사용할 주문 조회 메서드가 있다면 추가할 수 있습니다.
    public List<Order> findAllByOrderTypeAndOrderDateBetween(OrderType orderType, LocalDateTime start, LocalDateTime end) {
        List<Order> memOrders = memRepository
                .stream()
                .filter(order -> order.getOrderType().equals(orderType))
                .filter(order -> order.getOrderDate().isAfter(start) && order.getOrderDate().isBefore(end))
                .toList();
        List<Order> dbOrders = dbRepository.findAllByOrderTypeAndOrderDateBetween(orderType, start, end);

        return Stream.concat(memOrders.stream(), dbOrders.stream()).toList();
    }

    List<Order> findAllByOrderTypeAndOrderDateAfter(OrderType orderType, LocalDateTime dateTime) {
        List<Order> memOrders = memRepository
                .stream()
                .filter(order -> order.getOrderType().equals(orderType))
                .filter(order -> order.getOrderDate().isAfter(dateTime))
                .toList();

        List<Order> dbOrders = dbRepository.findAllByOrderTypeAndOrderDateAfter(orderType, dateTime);
        return Stream.concat(memOrders.stream(), dbOrders.stream()).toList();
    }

    public List<Order> findAll() {
        List<Order> dbOrders = dbRepository.findAll();
        return Stream.concat(memRepository.stream(), dbOrders.stream()).toList();
    }

    public Optional<Order> findById(Long id) {
        Optional<Order> memOrder = memRepository.stream().filter(order -> order.getId().equals(id)).findFirst();
        if (memOrder.isPresent()) {
            return memOrder;
        }
        return dbRepository.findById(id);
    }

    public void delete(Order order) {
        boolean isRemoved = memRepository.remove(order);
        if (!isRemoved) {
            dbRepository.delete(order);
        }
    }

    public List<Order> findAllByConsumer_Id(Long memberId) {
        List<Order> memOrders = memRepository
                .stream().filter(order -> order.getConsumer().getId().equals(memberId)).toList();
        List<Order> dbOrders = dbRepository.findAllByConsumer_Id(memberId);

        return Stream.concat(memOrders.stream(), dbOrders.stream()).toList();
    }
}
