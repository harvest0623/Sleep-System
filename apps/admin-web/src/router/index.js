import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '../api/request'

const routes = [
    {
        path: '/login',
        name: 'login',
        component: () => import('../views/LoginView.vue'),
        meta: { title: '登录', public: true }
    },
    {
        path: '/forgot',
        name: 'forgot',
        component: () => import('../views/ForgotView.vue'),
        meta: { title: '忘记密码', public: true }
    },
    {
        path: '/',
        component: () => import('../layouts/AdminLayout.vue'),
        redirect: '/dashboard',
        children: [
            {
                path: 'dashboard',
                name: 'dashboard',
                component: () => import('../views/DashboardView.vue'),
                meta: { title: '工作台' }
            },
            {
                path: 'admins',
                name: 'admins',
                component: () => import('../views/AdminListView.vue'),
                meta: { title: '管理员管理' }
            },
            {
                path: 'roles',
                name: 'roles',
                component: () => import('../views/RoleView.vue'),
                meta: { title: '角色权限' }
            },
            {
                path: 'logs',
                name: 'logs',
                component: () => import('../views/LogView.vue'),
                meta: { title: '操作日志' }
            },
            {
                path: ':pathMatch(.*)*',
                name: 'placeholder',
                component: () => import('../views/PlaceholderView.vue'),
                meta: { title: '模块开发中' }
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to) => {
    const token = getToken()

    if (!to.meta.public && !token) {
        return { path: '/login', query: to.fullPath === '/' ? {} : { redirect: to.fullPath } }
    }

    if (token && to.path === '/login') {
        return { path: '/dashboard' }
    }

    return true
})

export default router
