"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import apiClient from "@/utils/api";

interface Product {
    id: number;
    name: string;
    price: number;
    stock: number;
    description: string;
    productType: string;
}

export default function HomePage() {
    const router = useRouter();

    // =========== 로그인 관련 상태 ===========
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [emailInput, setEmailInput] = useState("");
    const [addressInput, setAddressInput] = useState("");
    // ======================================

    const [products, setProducts] = useState<Product[]>([]);
    const [cart, setCart] = useState<{ [key: number]: number }>({});
    const [status, setStatus] = useState("");

    // 로그인 여부 판별
    const checkLoginStatus = () => {
        const token = localStorage.getItem("access-token");
        if (token) {
            setIsLoggedIn(true);
        } else {
            setIsLoggedIn(false);
        }
    };

    // 로그아웃
    const handleLogout = () => {
        localStorage.removeItem("access-token");
        setIsLoggedIn(false);
        // 필요하다면 router.refresh() 또는 router.push("/") 등 추가 가능
    };

    // 상품 목록 불러오기
    const fetchProducts = async () => {
        try {
            const response = await apiClient.get("/api/v1/products");
            const productList = response.data.data;
            setProducts(productList);
            setStatus("상품 목록을 성공적으로 가져왔습니다.");
        } catch (error: any) {
            console.error("상품 목록 가져오기 실패:", error);
            setStatus("상품 목록 가져오기 실패: " + (error.response?.data?.msg || error.message));
        }
    };

    // 장바구니에 상품 추가
    const addToCart = async (productId: number) => {
        try {
            const quantity = 1;
            await apiClient.post("/api/v1/cart/add", {
                productId,
                quantity,
            });
            // 로컬 장바구니 업데이트
            setCart((prev) => ({
                ...prev,
                [productId]: (prev[productId] || 0) + quantity,
            }));
            console.log("백엔드 Cart에 상품 추가 완료");
        } catch (error: any) {
            console.error("백엔드 Cart 담기 실패:", error);
            setStatus("백엔드 Cart 담기 실패: " + (error.response?.data?.msg || error.message));
        }
    };

    // 장바구니에서 상품 1개 삭제
    const removeOneFromCart = async (productId: number) => {
        try {
            await apiClient.patch("/api/v1/cart/removeOne", { productId });
            // 로컬 상태 업데이트
            setCart((prev) => {
                const currentQty = prev[productId] || 0;
                if (currentQty <= 1) {
                    const updated = { ...prev };
                    delete updated[productId];
                    return updated;
                } else {
                    return {
                        ...prev,
                        [productId]: currentQty - 1,
                    };
                }
            });
            console.log("백엔드 Cart에서 상품 1개 제거 완료");
        } catch (error: any) {
            console.error("백엔드 Cart에서 상품 제거 실패:", error);
            setStatus("백엔드 Cart에서 상품 제거 실패: " + (error.response?.data?.msg || error.message));
        }
    };

    // 로컬 장바구니 총금액
    const totalPrice = Object.entries(cart).reduce((acc, [pid, qty]) => {
        const product = products.find((p) => p.id === Number(pid));
        return product ? acc + product.price * qty : acc;
    }, 0);

    // “결제하기” -> 백엔드 주문 생성
    const handleCheckout = async () => {
        try {
            const response = await apiClient.post("/user/orders/create");
            if (response.status === 200) {
                console.log("주문 생성 성공:", response.data);
                alert("주문이 성공적으로 생성되었습니다.");
                router.push("/order"); // 주문 목록 페이지
            }
        } catch (error: any) {
            console.error("주문 생성 실패:", error);
            alert("주문 생성 실패. 다시 시도해주세요.");
            router.push("/order");
        }
    };

    useEffect(() => {
        checkLoginStatus();
        fetchProducts();
    }, []);

    return (
        <div style={styles.container}>
            {/* 우측 상단 영역: 로그인 상태 표시 */}
            <div style={styles.topRight}>
                {isLoggedIn ? (
                    <>
                        <button
                            onClick={() => router.push("/order")} // handleOrder를 직접 작성하지 않고 바로 사용
                            style={styles.addButton}
                        >
                            주문 내역
                        </button>
                        <button onClick={handleLogout} style={styles.logoutButton}>
                            로그아웃
                        </button>
                    </>
                ) : (
                    <button onClick={() => router.push("/login")} style={styles.loginButton}>
                        로그인
                    </button>
                )}
            </div>

            {/* 상품 목록 */}
            <div style={styles.leftPane}>
                <h2>상품 목록</h2>
                {status && <p style={{ color: "blue" }}>{status}</p>}
                {products.map((product) => (
                    <div key={product.id} style={styles.productItem}>
                        <div style={{ flex: 1 }}>
                            <strong>{product.name}</strong>
                            <p>가격: {product.price}원</p>
                            <p>재고: {product.stock}개</p>
                            <p>설명: {product.description}</p>
                            <p>종류: {product.productType}</p>
                        </div>
                        {isLoggedIn && (
                            <button onClick={() => addToCart(product.id)} style={styles.addButton}>
                                담기
                            </button>
                        )}
                    </div>
                ))}
            </div>

            {/* 우측 영역 */}
            {isLoggedIn ? (
                // 로그인 상태 → 장바구니
                <div style={styles.rightPane}>
                    <h2>장바구니 요약</h2>
                    <div style={styles.cartList}>
                        {Object.entries(cart).map(([pid, qty]) => {
                            const product = products.find((p) => p.id === Number(pid));
                            if (!product) return null;
                            return (
                                <div key={pid} style={styles.cartItem}>
                                    {product.name} : {qty}개 = {product.price * qty}원
                                    <button
                                        onClick={() => removeOneFromCart(product.id)}
                                        style={styles.deleteButton}
                                    >
                                        삭제
                                    </button>
                                </div>
                            );
                        })}
                    </div>
                    <p>총 금액: {totalPrice}원</p>
                    <button onClick={handleCheckout} style={styles.checkoutButton}>
                        결제하기
                    </button>
                </div>
            ) : (
                // 비로그인 상태 → 회원 정보 입력 및 안내
                <div style={styles.rightPaneSmall}>
                    <h2>회원 정보 입력</h2>
                    {/* 여기서 추가로 비로그인 시 주문 불가 안내 문구 */}
                    <p style={{ color: "red", fontWeight: "bold" }}>
                        비회원은 주문하실 수 없습니다.
                    </p>
                    <p style={{ color: "gray" }}>
                        (로그인을 하시면 장바구니 담기 기능을 이용할 수 있습니다.)
                    </p>
                </div>
            )}
        </div>
    );
}

const styles = {
    container: {
        position: "relative" as const,
        display: "flex",
        flexDirection: "row" as const,
        gap: "20px",
        padding: "20px",
        fontFamily: "Arial, sans-serif",
        color: "#333",
        backgroundColor: "#f9f9f9",
        height: "100vh",
    },
    topRight: {
        position: "absolute" as const,
        top: "10px",
        right: "20px",
        display: "flex",
        alignItems: "center",
        gap: "10px",
    },
    loginButton: {
        backgroundColor: "#0070f3",
        color: "#fff",
        border: "none",
        borderRadius: "5px",
        padding: "8px 12px",
        cursor: "pointer",
    },
    logoutButton: {
        backgroundColor: "#ff5252",
        color: "#fff",
        border: "none",
        borderRadius: "5px",
        padding: "8px 12px",
        cursor: "pointer",
    },
    leftPane: {
        flex: 1,
        backgroundColor: "#fff",
        padding: "20px",
        borderRadius: "10px",
        boxShadow: "0 2px 8px rgba(0, 0, 0, 0.1)",
        overflowY: "auto" as const,
    },
    productItem: {
        display: "flex",
        justifyContent: "space-between",
        marginBottom: "10px",
        padding: "10px",
        border: "1px solid #ddd",
        borderRadius: "5px",
        backgroundColor: "#fafafa",
    },
    addButton: {
        marginLeft: "10px",
        padding: "5px 10px",
        backgroundColor: "#0070f3",
        color: "#fff",
        border: "none",
        borderRadius: "5px",
        cursor: "pointer",
        alignSelf: "center",
        height: "40px",
    },
    rightPane: {
        flex: 1,
        backgroundColor: "#fff",
        padding: "20px",
        borderRadius: "10px",
        boxShadow: "0 2px 8px rgba(0, 0, 0, 0.1)",
        overflowY: "auto" as const,
    },
    rightPaneSmall: {
        flex: 1,
        backgroundColor: "#fff",
        padding: "20px",
        borderRadius: "10px",
        boxShadow: "0 2px 8px rgba(0, 0, 0, 0.1)",
        overflowY: "auto" as const,
        maxHeight: "400px",
    },
    cartList: {
        border: "1px solid #ddd",
        borderRadius: "5px",
        padding: "10px",
        height: "300px",
        overflowY: "auto" as const,
        marginBottom: "10px",
        backgroundColor: "#fafafa",
    },
    cartItem: {
        display: "flex",
        justifyContent: "space-between",
        marginBottom: "5px",
        border: "1px solid #ddd",
        borderRadius: "5px",
        padding: "10px",
        backgroundColor: "#fff",
    },
    deleteButton: {
        backgroundColor: "#ff5252",
        color: "#fff",
        border: "none",
        borderRadius: "5px",
        padding: "5px 10px",
        cursor: "pointer",
    },
    checkoutButton: {
        padding: "10px",
        backgroundColor: "#0070f3",
        color: "#fff",
        border: "none",
        borderRadius: "5px",
        cursor: "pointer",
    },
    formGroup: {
        marginBottom: "15px",
    },
    input: {
        width: "100%",
        padding: "8px",
        border: "1px solid #ccc",
        borderRadius: "5px",
    },
};
