"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import apiClient from "@/utils/api";

interface Product {
    id: number;
    name: string;
    price: number;
    stock: number;
}

export default function HomePage() {
    const router = useRouter();
    const [products, setProducts] = useState<Product[]>([]); // 상품 목록 상태
    const [cart, setCart] = useState<{ [key: number]: number }>({}); // 로컬 장바구니
    const [status, setStatus] = useState(""); // 상태 메시지

    // 1. 상품 목록 불러오기
    const fetchProducts = async () => {
        try {
            const response = await apiClient.get("/api/v1/products");
            const productList = response.data.data; // RsData.data 내 상품 리스트
            setProducts(productList);
            setStatus("상품 목록을 성공적으로 가져왔습니다.");
        } catch (error: any) {
            console.error("상품 목록 가져오기 실패:", error);
            setStatus("상품 목록 가져오기 실패: " + (error.response?.data?.msg || error.message));
        }
    };

    // 2. 장바구니에 상품 추가 (프론트 로컬 + 백엔드 DB)
    const addToCart = async (id: number) => {
        const product = products.find((p) => p.id === id);
        if (!product) {
            console.error("상품을 찾을 수 없습니다.");
            return;
        }

        // (2-1) 로컬 장바구니 수량 증가
        setCart((prevCart) => ({
            ...prevCart,
            [id]: (prevCart[id] || 0) + 1,
        }));

        // (2-2) 서버 장바구니에 추가 API 호출
        try {
            await apiClient.post("/api/v1/cart/add", {
                productId: id,
                quantity: 1, // 여기서는 테스트로 1씩 담기
            });
            console.log("백엔드 Cart에 상품 추가 완료");
        } catch (error: any) {
            console.error("백엔드 Cart 담기 실패:", error);
            setStatus("백엔드 Cart 담기 실패: " + (error.response?.data?.msg || error.message));
        }
    };

    // 3. 총 금액 계산 (로컬 cart 기준)
    const totalPrice = Object.entries(cart).reduce((total, [id, count]) => {
        const product = products.find((p) => p.id === Number(id));
        return product ? total + product.price * count : total;
    }, 0);

    // 4. “결제하기” 버튼 → 주문 생성 (백엔드 /user/orders/create)
    const handleCheckout = async () => {
        try {
            const response = await apiClient.post("/user/orders/create");
            if (response.status === 200) {
                console.log("주문 생성 성공:", response.data);
                alert("주문이 성공적으로 생성되었습니다.");
                router.push("/order");
            }
        } catch (error: any) {
            console.error("주문 생성 실패:", error);
            alert("주문 생성에 실패했습니다. 다시 시도해주세요.");
            router.push("/order");
        }
    };

    // 컴포넌트 마운트 시 API 호출
    useEffect(() => {
        fetchProducts();
    }, []);

    return (
        <div
            style={{
                display: "flex",
                flexDirection: "row",
                gap: "20px",
                padding: "20px",
                fontFamily: "Arial, sans-serif",
                color: "#333",
                backgroundColor: "#f9f9f9",
                height: "100vh",
            }}
        >
            {/* 상품 목록 */}
            <div
                style={{
                    flex: 1,
                    backgroundColor: "#fff",
                    padding: "20px",
                    borderRadius: "10px",
                    boxShadow: "0 2px 8px rgba(0, 0, 0, 0.1)",
                }}
            >
                <h2 style={{ fontSize: "18px", fontWeight: "bold", marginBottom: "20px" }}>
                    상품 목록
                </h2>
                {status && <p style={{ color: "#0070f3" }}>{status}</p>}
                {products.length > 0 ? (
                    products.map((product) => (
                        <div
                            key={product.id}
                            style={{
                                display: "flex",
                                justifyContent: "space-between",
                                alignItems: "center",
                                marginBottom: "15px",
                                padding: "15px",
                                border: "1px solid #ddd",
                                borderRadius: "5px",
                                backgroundColor: "#f8f8f8",
                            }}
                        >
                            <div>
                                <strong style={{ display: "block", marginBottom: "5px" }}>
                                    {product.name}
                                </strong>
                                <p style={{ margin: 0, color: "#555" }}>
                                    {product.price.toLocaleString()}원
                                </p>
                            </div>
                            <button
                                onClick={() => addToCart(product.id)}
                                style={{
                                    padding: "10px 15px",
                                    backgroundColor: "#0070f3",
                                    color: "#fff",
                                    border: "none",
                                    borderRadius: "5px",
                                    cursor: "pointer",
                                }}
                            >
                                추가
                            </button>
                        </div>
                    ))
                ) : (
                    <p>상품이 없습니다.</p>
                )}
            </div>

            {/* Summary */}
            <div
                style={{
                    flex: 1,
                    backgroundColor: "#fff",
                    padding: "20px",
                    borderRadius: "10px",
                    boxShadow: "0 2px 8px rgba(0, 0, 0, 0.1)",
                }}
            >
                <h2 style={{ fontSize: "18px", fontWeight: "bold", marginBottom: "20px" }}>
                    Summary
                </h2>
                <div
                    style={{
                        height: "400px",
                        overflowY: "auto",
                        border: "1px solid #ddd",
                        borderRadius: "5px",
                        padding: "10px",
                        marginBottom: "20px",
                    }}
                >
                    {Object.entries(cart).map(([id, count]) => {
                        const product = products.find((p) => p.id === Number(id));
                        if (!product) {
                            return null;
                        }
                        return (
                            <div
                                key={`cart-${id}`}
                                style={{
                                    display: "flex",
                                    justifyContent: "space-between",
                                    marginBottom: "5px",
                                    fontSize: "14px",
                                }}
                            >
                                <span>
                                    {product.name}{" "}
                                    <strong style={{ color: "#0070f3" }}>{count}개</strong>
                                </span>
                                <span>{(product.price * count).toLocaleString()}원</span>
                            </div>
                        );
                    })}
                </div>
                <div style={{ marginBottom: "20px" }}>
                    <p style={{ fontSize: "16px", fontWeight: "bold" }}>
                        총금액:{" "}
                        <span style={{ color: "#0070f3" }}>{totalPrice.toLocaleString()}원</span>
                    </p>
                    <button
                        onClick={handleCheckout}
                        style={{
                            width: "100%",
                            padding: "15px",
                            backgroundColor: "#0070f3",
                            color: "#fff",
                            border: "none",
                            borderRadius: "5px",
                            fontSize: "16px",
                            fontWeight: "bold",
                            cursor: "pointer",
                        }}
                    >
                        결제하기
                    </button>
                </div>
            </div>
        </div>
    );
}
