// api.js
import axios from "axios";

const apiClient = axios.create({
    baseURL: "http://localhost:8080",
    headers: {
        "Content-Type": "application/json",
    },
    withCredentials: true, // CORS 자격 증명 활성화
});

// 요청 인터셉터
apiClient.interceptors.request.use(
    (config) => {
        const accessToken = localStorage.getItem("access-token");
        if (accessToken) {
            config.headers.Authorization = `Bearer ${accessToken}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// 응답 인터셉터 (Refresh 로직 제거 버전)
apiClient.interceptors.response.use(
    (response) => response,
    async (error) => {
        // 401 에러가 발생하더라도, 여기서는 별도의 refresh 토큰 재발급 요청을 하지 않음
        // 필요하다면 사용자가 직접 로그인 페이지로 이동하거나, 토큰 재발급 요청을 호출하도록 처리

        if (error.response?.status === 401) {
            // 예: 자동 로그아웃 처리 (선택)
            localStorage.removeItem("access-token");
            localStorage.removeItem("refresh-token");
            window.location.href = "";
        }

        return Promise.reject(error);
    }
);

export default apiClient;
