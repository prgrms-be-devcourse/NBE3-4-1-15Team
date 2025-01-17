"use client";

import { useState } from "react";
import apiClient from "@/utils/api";

export default function OrdersPage() {
    const [orders, setOrders] = useState([]);
    const [orderId, setOrderId] = useState("");
    const [status, setStatus] = useState("");

    // 모든 주문 조회
    const fetchAllOrders = async () => {
        try {
            const response = await apiClient.get("/orders");
            setOrders(response.data);
            setStatus("모든 주문을 성공적으로 가져왔습니다.");
        } catch (error) {
            setStatus("오류 발생: " + error.message);
        }
    };

    // 특정 주문 조회
    const fetchOrderById = async () => {
        try {
            const response = await apiClient.get(`/orders/${orderId}`);
            setOrders([response.data]);
            setStatus("특정 주문을 성공적으로 가져왔습니다.");
        } catch (error) {
            setStatus("오류 발생: " + error.message);
        }
    };

    // 특정 회원의 주문 조회
    const fetchOrdersByMember = async () => {
        try {
            const response = await apiClient.get(`/orders/mem/1`);
            setOrders(response.data);
            setStatus("특정 회원의 주문을 성공적으로 가져왔습니다.");
        } catch (error) {
            setStatus("오류 발생: " + error.message);
        }
    };

    // 주문 생성
    const createOrder = async () => {
        try {
            const response = await apiClient.post("/orders", null, {
                params: {
                    memberId: 1,
                    orderType: "ORDERED",
                    totalPrice: 50000,
                },
            });
            setStatus("주문이 성공적으로 생성되었습니다: " + response.data.id);
        } catch (error) {
            setStatus("오류 발생: " + error.message);
        }
    };

    // 주문 삭제
    const deleteOrder = async () => {
        try {
            await apiClient.delete(`/orders/${orderId}`);
            setStatus(`주문 ID ${orderId}이 성공적으로 삭제되었습니다.`);
            fetchAllOrders(); // 삭제 후 다시 주문 목록 갱신
        } catch (error) {
            setStatus("오류 발생: " + error.message);
        }
    };

    return (
        <div style={styles.container}>
            {/* 버튼 섹션 */}
            <div style={styles.buttonSection}>
                <h2 style={styles.title}>주문 관리</h2>
                <p style={styles.status}>{status}</p>
                <button style={styles.button} onClick={fetchAllOrders}>
                    모든 주문 조회
                </button>
                <div style={styles.inputGroup}>
                    <input
                        type="text"
                        placeholder="Order ID 입력"
                        value={orderId}
                        onChange={(e) => setOrderId(e.target.value)}
                        style={styles.input}
                    />
                    <button style={styles.button} onClick={fetchOrderById}>
                        특정 주문 조회
                    </button>
                </div>
                <button style={styles.button} onClick={fetchOrdersByMember}>
                    특정 회원의 주문 조회
                </button>
                <button style={styles.button} onClick={createOrder}>
                    주문 생성
                </button>
                <div style={styles.inputGroup}>
                    <input
                        type="text"
                        placeholder="Order ID 입력"
                        value={orderId}
                        onChange={(e) => setOrderId(e.target.value)}
                        style={styles.input}
                    />
                    <button style={styles.button} onClick={deleteOrder}>
                        주문 삭제
                    </button>
                </div>
            </div>

            {/* 주문 목록 섹션 */}
            <div style={styles.listSection}>
                <h2 style={styles.listTitle}>주문 목록</h2>
                <div style={styles.orders}>
                    {orders.length > 0 ? (
                        orders.slice(0, 123).map((order, index) => (
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
        justifyContent: "space-between",
        alignItems: "flex-start",
        padding: "20px",
        fontFamily: "Arial, sans-serif",
        backgroundColor: "#f4f4f4",
        minHeight: "100vh",
    },
    buttonSection: {
        width: "30%",
        display: "flex",
        flexDirection: "column",
        alignItems: "flex-start",
        padding: "20px",
        backgroundColor: "#fff",
        borderRadius: "10px",
        boxShadow: "0 4px 8px rgba(0, 0, 0, 0.1)",
    },
    button: {
        width: "100%",
        padding: "10px",
        marginBottom: "10px",
        backgroundColor: "#007bff",
        color: "white",
        border: "none",
        borderRadius: "5px",
        cursor: "pointer",
        fontSize: "1rem",
        textAlign: "center",
    },
    inputGroup: {
        display: "flex",
        alignItems: "center",
        gap: "10px",
        marginBottom: "10px",
    },
    input: {
        flex: 1,
        padding: "10px",
        border: "1px solid #ccc",
        borderRadius: "5px",
        color: "black", // 텍스트 색상을 검은색으로 설정
    },
    listSection: {
        width: "65%",
        padding: "20px",
        backgroundColor: "#fff",
        borderRadius: "10px",
        boxShadow: "0 4px 8px rgba(0, 0, 0, 0.1)",
        maxHeight: "300px",
        overflowY: "auto", // 스크롤 설정
    },
    listTitle: {
        fontSize: "1.5rem",
        marginBottom: "10px",
        color: "#333",
    },
    listItem: {
        padding: "10px",
        marginBottom: "5px",
        backgroundColor: "#f9f9f9",
        borderRadius: "5px",
        color: "black",
        fontSize: "1rem",
    },
    title: {
        fontSize: "1.5rem",
        marginBottom: "10px",
        color: "#333",
    },
    status: {
        fontSize: "1rem",
        color: "#007bff",
        marginBottom: "20px",
    },
};
