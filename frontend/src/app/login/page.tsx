"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import apiClient from "@/utils/api";

export default function LoginPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [status, setStatus] = useState("");
    const router = useRouter();

    const handleLogin = async () => {
        try {
            const response = await apiClient.post("/api/v1/members/login", {
                email,
                password,
            });
            const { accessToken, refreshToken } = response.data.data;

            // 토큰 저장
            localStorage.setItem("access-token", accessToken);
            localStorage.setItem("refresh-token", refreshToken);

            setStatus("로그인 성공");
            router.push("/");
        } catch (error: any) {
            if (error.response) {
                setStatus(`오류 발생: ${error.response.data.msg || error.message}`);
            } else {
                setStatus("오류 발생: " + error.message);
            }
        }
    };

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
                justifyContent: "center",
                alignItems: "center",
            }}
        >
            {/* 로그인 카드 */}
            <div
                style={{
                    width: "400px",
                    backgroundColor: "#fff",
                    padding: "30px",
                    borderRadius: "10px",
                    boxShadow: "0 2px 8px rgba(0, 0, 0, 0.1)",
                }}
            >
                <h1 style={{ marginBottom: "20px", fontSize: "24px", textAlign: "center" }}>
                    로그인
                </h1>
                {status && (
                    <p style={{ color: "#0070f3", marginBottom: "20px", textAlign: "center" }}>
                        {status}
                    </p>
                )}
                <div style={{ marginBottom: "15px" }}>
                    <label
                        htmlFor="email"
                        style={{ display: "block", marginBottom: "5px", fontWeight: "bold" }}
                    >
                        이메일
                    </label>
                    <input
                        id="email"
                        type="email"
                        placeholder="이메일"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        style={{
                            width: "100%",
                            padding: "10px",
                            border: "1px solid #ddd",
                            borderRadius: "5px",
                        }}
                    />
                </div>
                <div style={{ marginBottom: "20px" }}>
                    <label
                        htmlFor="password"
                        style={{ display: "block", marginBottom: "5px", fontWeight: "bold" }}
                    >
                        비밀번호
                    </label>
                    <input
                        id="password"
                        type="password"
                        placeholder="비밀번호"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        style={{
                            width: "100%",
                            padding: "10px",
                            border: "1px solid #ddd",
                            borderRadius: "5px",
                        }}
                    />
                </div>
                <button
                    onClick={handleLogin}
                    style={{
                        width: "100%",
                        padding: "12px 0",
                        backgroundColor: "#0070f3",
                        color: "#fff",
                        border: "none",
                        borderRadius: "5px",
                        fontSize: "16px",
                        cursor: "pointer",
                    }}
                >
                    로그인
                </button>
            </div>
        </div>
    );
}
