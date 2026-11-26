import { defineStore } from "pinia";

function readUser() {
    try {
        return JSON.parse(localStorage.getItem("sleep_user") || "null");
    } catch (error) {
        return null;
    }
}

export const useUserStore = defineStore("user", {
    state: () => ({
        token: localStorage.getItem("sleep_token") || "",
        user: readUser()
    }),
    getters: {
        isLogin: (state) => Boolean(state.token),
        displayName: (state) => (state.user && state.user.nickname) || "助眠用户"
    },
    actions: {
        setAuth(token, user) {
            this.token = token;
            this.user = user;
            localStorage.setItem("sleep_token", token);
            localStorage.setItem("sleep_user", JSON.stringify(user || null));
        },
        setUser(user) {
            this.user = user;
            localStorage.setItem("sleep_user", JSON.stringify(user || null));
        },
        logout() {
            this.token = "";
            this.user = null;
            localStorage.removeItem("sleep_token");
            localStorage.removeItem("sleep_user");
        }
    }
});
