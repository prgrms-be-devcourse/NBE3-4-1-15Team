package com.nbe.NBE3_4_1_Team15.domain.order.type;

public enum OrderType {
    ORDERED,        // 주문완료
    PAID,           // 결제완료
    DELIVERY,       // 배송중
    CANCELED        // 취소됨
};