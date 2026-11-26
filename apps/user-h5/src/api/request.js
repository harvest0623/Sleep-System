const BASE_URL = "/api";

export async function request(path, options = {}) {
    const token = localStorage.getItem("sleep_token");
    const headers = {
        "Content-Type": "application/json",
        ...(options.headers || {})
    };
    if (token) {
        headers.Authorization = "Bearer " + token;
    }
    const response = await fetch(BASE_URL + path, {
        method: options.method || "GET",
        headers,
        body: options.body ? JSON.stringify(options.body) : undefined
    });
    const result = await response.json().catch(() => null);
    if (!result) {
        throw new Error("网络异常，请稍后重试");
    }
    if (result.code === 401) {
        localStorage.removeItem("sleep_token");
        localStorage.removeItem("sleep_user");
        if (window.location.pathname !== "/login") {
            window.location.href = "/login";
        }
        throw new Error(result.message || "登录已失效");
    }
    if (result.code !== 200) {
        throw new Error(result.message || "请求失败");
    }
    return result.data;
}
