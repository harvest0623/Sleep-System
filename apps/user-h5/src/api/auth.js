import { request } from "./request";

export function sendCaptcha(phone) {
    return request("/user/auth/captcha", {
        method: "POST",
        body: { phone }
    });
}

export function login(phone, code) {
    return request("/user/auth/login", {
        method: "POST",
        body: { phone, code }
    });
}

export function register(phone, code, password) {
    return request("/user/auth/register", {
        method: "POST",
        body: { phone, code, password }
    });
}

export function resetPassword(phone, code, newPassword) {
    return request("/user/auth/reset-password", {
        method: "POST",
        body: { phone, code, newPassword }
    });
}

export function fetchProfile() {
    return request("/user/profile");
}

export function updateProfile(data) {
    return request("/user/profile", {
        method: "PUT",
        body: data
    });
}

export function saveOnboard(data) {
    return request("/user/profile/onboard", {
        method: "POST",
        body: data
    });
}
