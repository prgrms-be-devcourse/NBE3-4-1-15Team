package com.nbe.NBE3_4_1_Team15.Order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter @Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @OneToMany
    private Long cart_id;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private Integer totalPrice;

    private Date orderDate;

    private Date createAt;

    private Date updateAt;

    public enum OrderStatus {
        BEFORE_ORDER, COMPLETE_ORDER, APPROVED_ORDER, DELIVERING, COMPLETE_DELIVERY
    };
}
