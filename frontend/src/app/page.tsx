"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import apiClient from "@/utils/api";
import jwt_decode from "jwt-decode"; // 토큰 디코딩용 (npm install jwt-decode)

interface Product {
    id: number;
    name: string;
    price: number;
    stock: number;
    description: string;
    productType: string;
    sellerId: number; // ★ sellerId를 백엔드에서 내려준다고 가정
}

// 토큰 디코딩 시 사용할 인터페이스 (예: id, email)
interface MyTokenPayload {
    id: number;
    email: string;
    exp?: number;
    iat?: number;
}

export default function HomePage() {
    const router = useRouter();

    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [loggedInUserId, setLoggedInUserId] = useState<number | null>(null);

    const [products, setProducts] = useState<Product[]>([]);
    const [cart, setCart] = useState<{ [key: number]: number }>({});
    const [status, setStatus] = useState("");

    // (1) 로그인 여부 및 사용자 ID 판별
    const checkLoginStatus = () => {
        const token = localStorage.getItem("access-token");
        if (!token) {
            setIsLoggedIn(false);
            setLoggedInUserId(null);
            return;
        }
        // 토큰이 있으면 로그인 상태 true
        setIsLoggedIn(true);

        try {
            const decoded = jwt_decode<MyTokenPayload>(token);
            if (decoded && decoded.id) {
                setLoggedInUserId(decoded.id);
            } else {
                setLoggedInUserId(null);
            }
        } catch (e) {
            console.error("토큰 디코딩 실패:", e);
            setLoggedInUserId(null);
        }
    };

    // (2) 로그아웃
    const handleLogout = () => {
        localStorage.removeItem("access-token");
        localStorage.removeItem("refresh-token");
        setIsLoggedIn(false);
        setLoggedInUserId(null);
    };

    // (3) 상품 목록 불러오기
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

    // (4) 상품 삭제
    const deleteProduct = async (productId: number) => {
        if (!confirm("정말 이 상품을 삭제하시겠습니까?")) return;

        try {
            // DELETE /api/v1/products/{id}
            const response = await apiClient.delete(`/api/v1/products/${productId}`);
            alert(response.data.msg);

            // 로컬 상태에서도 제거
            setProducts((prev) => prev.filter((p) => p.id !== productId));
        } catch (error: any) {
            console.error("상품 삭제 오류:", error);
            alert("상품 삭제에 실패했습니다: " + (error.response?.data?.msg || error.message));
        }
    };

    // (5) 장바구니 담기 / 삭제
    const addToCart = async (productId: number) => {
        try {
            const quantity = 1;
            await apiClient.post("/api/v1/cart/add", { productId, quantity });
            setCart((prev) => ({
                ...prev,
                [productId]: (prev[productId] || 0) + quantity,
            }));
        } catch (error: any) {
            console.error("백엔드 Cart 담기 실패:", error);
            setStatus("백엔드 Cart 담기 실패: " + (error.response?.data?.msg || error.message));
        }
    };

    const removeOneFromCart = async (productId: number) => {
        try {
            await apiClient.patch("/api/v1/cart/removeOne", { productId });
            setCart((prev) => {
                const currentQty = prev[productId] || 0;
                if (currentQty <= 1) {
                    const updated = { ...prev };
                    delete updated[productId];
                    return updated;
                }
                return {
                    ...prev,
                    [productId]: currentQty - 1,
                };
            });
        } catch (error: any) {
            console.error("백엔드 Cart에서 상품 제거 실패:", error);
            setStatus("백엔드 Cart에서 상품 제거 실패: " + (error.response?.data?.msg || error.message));
        }
    };

    // (6) 결제하기
    const handleCheckout = async () => {
        try {
            const response = await apiClient.post("/user/orders/create");
            if (response.status === 200) {
                alert("주문이 성공적으로 생성되었습니다.");
                router.push("/order");
            }
        } catch (error: any) {
            console.error("주문 생성 실패:", error);
            alert("주문 생성 실패. 다시 시도해주세요.");
            router.push("/order");
        }
    };

    // 로컬 장바구니 총금액
    const totalPrice = Object.entries(cart).reduce((acc, [pid, qty]) => {
        const product = products.find((p) => p.id === Number(pid));
        return product ? acc + product.price * qty : acc;
    }, 0);

    useEffect(() => {
        checkLoginStatus();
        fetchProducts();
    }, []);

    return (
        <div style={styles.container}>
            {/* 우측 상단 */}
            <div style={styles.topRight}>
                {isLoggedIn ? (
                    <>
                        <button onClick={() => router.push("/create")} style={styles.addButton}>
                            상품 추가
                        </button>
                        <button onClick={() => router.push("/order")} style={styles.addButton}>
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

            {/* 왼쪽: 상품 목록 */}
            <div style={styles.leftPane}>
                <h2>상품 목록</h2>
                {status && <p style={{ color: "blue" }}>{status}</p>}
                {products.map((product) => {
                    const isOwner = product.sellerId === loggedInUserId;
                    return (
                        <div key={product.id} style={styles.productItem}>
                            <div style={{ flex: 1 }}>
                                <strong>{product.name}</strong>
                                <p>가격: {product.price}원</p>
                                <p>재고: {product.stock}개</p>
                                <p>설명: {product.description}</p>
                                <p>종류: {product.productType}</p>
                            </div>
                            {isLoggedIn && (
                                <>
                                    {isOwner ? (
                                        // 본인이 만든 상품이면 삭제 버튼
                                        <button
                                            onClick={() => deleteProduct(product.id)}
                                            style={styles.deleteButton}
                                        >
                                            삭제
                                        </button>
                                    ) : (
                                        // 다른 사람 상품이면 담기 버튼
                                        <button
                                            onClick={() => addToCart(product.id)}
                                            style={styles.addButton}
                                        >
                                            담기
                                        </button>
                                    )}
                                </>
                            )}
                        </div>
                    );
                })}
            </div>

            {/* 오른쪽: 장바구니 or 안내 */}
            {isLoggedIn ? (
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
                <div style={styles.rightPaneSmall}>
                    <h2>비로그인 사용자</h2>
                    <p style={{ color: "red", fontWeight: "bold" }}>
                        비회원은 주문하실 수 없습니다.
                    </p>
                    <p style={{ color: "gray" }}>
                        (로그인 후, 장바구니 담기 및 결제 기능 사용 가능)
                    </p>
                </div>
            )}
        </div>
    );
}

// 스타일들...
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
    addButton: {
        backgroundColor: "#0070f3",
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
    deleteButton: {
        backgroundColor: "#ff5252",
        color: "#fff",
        border: "none",
        borderRadius: "5px",
        padding: "5px 10px",
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
    checkoutButton: {
        padding: "10px",
        backgroundColor: "#0070f3",
        color: "#fff",
        border: "none",
        borderRadius: "5px",
        cursor: "pointer",
    },
};