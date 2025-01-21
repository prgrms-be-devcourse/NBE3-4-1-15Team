"use client";

import { useState, useEffect } from "react";
import apiClient from "@/utils/api";

export default function OrdersPage() {
    const [orders, setOrders] = useState([]); // 주문 목록 상태
    const [status, setStatus] = useState(""); // 상태 메시지

    // 주문 목록 가져오기 함수
    const fetchOrders = async () => {
        try {
            const response = await apiClient.get("/user/orders/mem"); // API 호출
            console.log("주문 내역:", response.data);
            setOrders(response.data); // 주문 목록 상태 업데이트
            setStatus("주문 목록 가져오기 성공");
        } catch (error) {
            console.error("주문 내역 조회 실패:", error);
            setStatus("주문 목록 가져오기 실패: " + (error.response?.data?.msg || error.message));
        }
    };

    // 초기 렌더링 시 주문 목록 가져오기
    useEffect(() => {
        fetchOrders();
    }, []); // 컴포넌트 마운트 시 1회 실행

    return (
        <div style={styles.container}>
            {/* 주문 목록 조회 섹션 */}
            <div style={styles.section}>
                <h1 style={styles.title}>주문 페이지</h1>
                <button style={styles.button} onClick={fetchOrders}>
                    주문 목록 가져오기
                </button>
                <p style={styles.status}>{status}</p>
                <div style={styles.orders}>
                    {orders.length > 0 ? (
                        orders.map((order, index) => (
                            <div key={index} style={styles.listItem}>
                                {JSON.stringify(order, null, 2)}
                            </div>
                        ))
                    ) : (
                        <p>주문이 없습니다.</p>
                    )}
                </div>
            </div>
        </div>
    );
}

const styles = {
    container: {
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        flexDirection: "column",
        padding: "20px",
        fontFamily: "Arial, sans-serif",
        backgroundColor: "#f4f4f4",
        minHeight: "100vh",
    },
    section: {
        width: "80%",
        padding: "20px",
        backgroundColor: "#fff",
        borderRadius: "10px",
        boxShadow: "0 4px 8px rgba(0, 0, 0, 0.1)",
    },
    title: {
        fontSize: "1.5rem",
        marginBottom: "20px",
        color: "#333",
    },
    button: {
        width: "100%",
        padding: "10px",
        marginBottom: "20px",
        backgroundColor: "#007bff",
        color: "white",
        border: "none",
        borderRadius: "5px",
        cursor: "pointer",
        fontSize: "1rem",
    },
    status: {
        fontSize: "1rem",
        color: "#007bff",
        marginBottom: "20px",
    },
    orders: {
        maxHeight: "400px", // 스크롤 가능한 최대 높이
        overflowY: "auto", // 세로 스크롤 활성화
        padding: "10px",
        backgroundColor: "#f9f9f9",
        borderRadius: "5px",
    },
    listItem: {
        padding: "10px",
        marginBottom: "5px",
        backgroundColor: "#fff",
        border: "1px solid #ddd",
        borderRadius: "5px",
        color: "#333",
        wordWrap: "break-word", // 긴 텍스트 줄바꿈 처리
    },
};
