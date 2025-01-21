"use client";

import { useState } from "react";
import apiClient from "@/utils/api";
import { useRouter } from "next/navigation";

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
        } catch (error) {
            if (error.response) {
                setStatus(`오류 발생: ${error.response.data.msg || error.message}`);
            } else {
                setStatus("오류 발생: " + error.message);
            }
        }
    };

    return (
        <div>
            <h1>로그인</h1>
            <input
                type="email"
                placeholder="이메일"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
            />
            <input
                type="password"
                placeholder="비밀번호"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
            />
            <button onClick={handleLogin}>로그인</button>
            <p>{status}</p>
        </div>
    );
}
