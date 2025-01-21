import type { NextConfig } from "next";

const nextConfig: NextConfig = {
    // 기존 설정들...

    // 에러 오버레이 비활성화
    webpackDevMiddleware: (config: any) => {
        config.dev = {
            ...config.dev,
            overlay: false
        };
        return config;
    },

    // 에러 핸들링 (선택사항)
    onError: (err: Error) => {
        console.log('Next.js 에러:', err);
    }
};

export default nextConfig;