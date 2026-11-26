import { createRouter, createWebHistory } from "vue-router";

const routes = [
    {
        path: "/",
        redirect: "/splash"
    },
    {
        path: "/splash",
        name: "splash",
        component: () => import("../views/SplashView.vue")
    },
    {
        path: "/login",
        name: "login",
        component: () => import("../views/LoginView.vue")
    },
    {
        path: "/register",
        name: "register",
        component: () => import("../views/RegisterView.vue")
    },
    {
        path: "/forgot",
        name: "forgot",
        component: () => import("../views/ForgotView.vue")
    },
    {
        path: "/privacy",
        name: "privacy",
        component: () => import("../views/PrivacyView.vue")
    },
    {
        path: "/onboard",
        name: "onboard",
        meta: { auth: true },
        component: () => import("../views/OnboardView.vue")
    },
    {
        path: "/home",
        name: "home",
        meta: { auth: true },
        component: () => import("../views/HomeView.vue")
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

router.beforeEach((to) => {
    const token = localStorage.getItem("sleep_token");
    if (to.meta.auth && !token) {
        return { path: "/login" };
    }
    if (token && (to.path === "/login" || to.path === "/register")) {
        return { path: "/home" };
    }
    return true;
});

export default router;
