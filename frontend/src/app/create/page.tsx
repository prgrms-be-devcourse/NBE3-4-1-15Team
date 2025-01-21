"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import apiClient from "@/utils/api";

interface Product {
    id: number;
    name: string;
    price: number;
    stock: number;
}

export default function ProductManagementPage() {
    const router = useRouter();
    const [products, setProducts] = useState<Product[]>([]);
    const [name, setName] = useState(""); // 제품명
    const [price, setPrice] = useState(0); // 가격
    const [description, setDescription] = useState(""); // 설명
    const [productType, setProductType] = useState("CAFFEINE"); // 제품 타입
    const [stock, setStock] = useState(0); // 재고
    const [status, setStatus] = useState(""); // 상태 메시지

    // 상품 등록 요청
    const handleSubmit = async () => {
        try {
            const response = await apiClient.post("/api/v1/products", {
                name,
                price,
                description,
                productType,
                stock,
            });

            // 상태 메시지 업데이트
            setStatus("제품 등록 성공: " + response.data.msg);

            // 등록된 상품을 목록에 추가 (아이디는 response.data.id 에서 받아온다고 가정)
            const newProduct = {
                id: response.data.id,
                name,
                price,
                stock,
            };
            setProducts((prevProducts) => [...prevProducts, newProduct]);

            // 폼 초기화
            resetForm();

            // alert 메시지를 띄운 후 확인을 누르면 홈("/")으로 이동
            alert("제품이 등록되었습니다.");
            router.push("/");
        } catch (error: any) {
            console.error("오류 발생:", error);
            setStatus("오류 발생: " + (error.response?.data?.msg || error.message));
        }
    };

    // 폼 초기화 함수
    const resetForm = () => {
        setName("");
        setPrice(0);
        setDescription("");
        setProductType("CAFFEINE");
        setStock(0);
    };

    return (
        <div style={styles.container}>
            {/* 상품 등록 섹션 */}
            <div style={styles.formSection}>
                <h2 style={styles.title}>제품 등록</h2>
                <form
                    style={styles.form}
                    onSubmit={(e) => {
                        e.preventDefault();
                        handleSubmit();
                    }}
                >
                    <label style={styles.label}>
                        제품명
                        <input
                            type="text"
                            placeholder="제품명을 입력하세요"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            style={styles.input}
                        />
                    </label>
                    <label style={styles.label}>
                        가격
                        <input
                            type="number"
                            placeholder="가격을 입력하세요"
                            value={price}
                            onChange={(e) => setPrice(Number(e.target.value))}
                            style={styles.input}
                        />
                    </label>
                    <label style={styles.label}>
                        설명
                        <textarea
                            placeholder="제품 설명을 입력하세요"
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                            style={styles.textarea}
                        />
                    </label>
                    <label style={styles.label}>
                        제품 타입
                        <select
                            value={productType}
                            onChange={(e) => setProductType(e.target.value)}
                            style={styles.select}
                        >
                            <option value="CAFFEINE">CAFFEINE</option>
                            <option value="DE_CAFFEINE">DE_CAFFEINE</option>
                        </select>
                    </label>
                    <label style={styles.label}>
                        재고 수량
                        <input
                            type="number"
                            placeholder="재고 수량을 입력하세요"
                            value={stock}
                            onChange={(e) => setStock(Number(e.target.value))}
                            style={styles.input}
                        />
                    </label>
                    <button type="submit" style={styles.button}>
                        등록
                    </button>
                </form>
                {status && <p style={styles.status}>{status}</p>}
            </div>

            {/* 상품 목록 섹션 */}
            <div style={styles.productSection}>
                <h2 style={styles.title}>상품 목록</h2>
                {products.length > 0 ? (
                    <div style={styles.productList}>
                        {products.map((product) => (
                            <div key={product.id} style={styles.productItem}>
                                <div>
                                    <strong>{product.name}</strong>
                                    <p>{product.price.toLocaleString()}원</p>
                                    <p>재고: {product.stock}개</p>
                                </div>
                            </div>
                        ))}
                    </div>
                ) : (
                    <p>등록된 상품이 없습니다.</p>
                )}
            </div>
        </div>
    );
}

const styles = {
    container: {
        display: "flex",
        flexDirection: "row" as const,
        gap: "20px",
        padding: "20px",
        fontFamily: "Arial, sans-serif",
        backgroundColor: "#f9f9f9",
        height: "100vh",
    },
    formSection: {
        flex: 1,
        backgroundColor: "#fff",
        padding: "20px",
        borderRadius: "10px",
        boxShadow: "0 4px 8px rgba(0, 0, 0, 0.1)",
    },
    productSection: {
        flex: 1,
        backgroundColor: "#fff",
        padding: "20px",
        borderRadius: "10px",
        boxShadow: "0 4px 8px rgba(0, 0, 0, 0.1)",
    },
    title: {
        fontSize: "24px",
        fontWeight: "bold",
        marginBottom: "20px",
    },
    form: {
        display: "flex",
        flexDirection: "column",
        gap: "15px",
    },
    label: {
        display: "flex",
        flexDirection: "column",
        fontSize: "16px",
        color: "#555",
    },
    input: {
        width: "100%",
        padding: "10px",
        marginTop: "5px",
        border: "1px solid #ddd",
        borderRadius: "5px",
        fontSize: "14px",
    },
    textarea: {
        width: "100%",
        padding: "10px",
        marginTop: "5px",
        border: "1px solid #ddd",
        borderRadius: "5px",
        fontSize: "14px",
        resize: "none" as const,
        height: "80px",
    },
    select: {
        width: "100%",
        padding: "10px",
        marginTop: "5px",
        border: "1px solid #ddd",
        borderRadius: "5px",
        fontSize: "14px",
    },
    button: {
        padding: "15px",
        backgroundColor: "#0070f3",
        color: "#fff",
        border: "none",
        borderRadius: "5px",
        fontSize: "16px",
        fontWeight: "bold" as const,
        cursor: "pointer",
    },
    status: {
        marginTop: "20px",
        fontSize: "14px",
        color: "#0070f3",
    },
    productList: {
        display: "flex",
        flexDirection: "column" as const,
        gap: "10px",
    },
    productItem: {
        padding: "15px",
        border: "1px solid #ddd",
        borderRadius: "5px",
        backgroundColor: "#f8f8f8",
    },
};