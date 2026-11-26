<template>
    <div class="layout">
        <aside class="sidebar">
            <div class="brand">
                <span class="brand__mark">眠</span>
                <div class="brand__text">
                    <div class="brand__name">助眠服务</div>
                    <div class="brand__sub">管理后台</div>
                </div>
            </div>

            <el-scrollbar class="sidebar__scroll">
                <el-menu
                    class="sidebar__menu"
                    :default-active="activeMenu"
                    :default-openeds="openedMenus"
                    background-color="transparent"
                    text-color="#A9A5C0"
                    active-text-color="#FFFFFF"
                    router
                >
                    <template v-for="menu in menus" :key="menu.id">
                        <el-sub-menu
                            v-if="menu.children && menu.children.length"
                            :index="`sub-${menu.id}`"
                        >
                            <template #title>
                                <el-icon><component :is="iconOf(menu)" /></el-icon>
                                <span>{{ menu.name }}</span>
                            </template>
                            <el-menu-item
                                v-for="child in menu.children"
                                :key="child.id"
                                :index="pathOf(child)"
                            >
                                {{ child.name }}
                            </el-menu-item>
                        </el-sub-menu>

                        <el-menu-item v-else :index="pathOf(menu)">
                            <el-icon><component :is="iconOf(menu)" /></el-icon>
                            <span>{{ menu.name }}</span>
                        </el-menu-item>
                    </template>
                </el-menu>
            </el-scrollbar>

            <div class="sidebar__foot">Sleep System · v0.1.0</div>
        </aside>

        <div class="main">
            <header class="topbar">
                <div class="topbar__left">
                    <div class="topbar__title">{{ pageTitle }}</div>
                    <div class="topbar__crumb">首页 / {{ pageTitle }}</div>
                </div>

                <div class="topbar__right">
                    <el-dropdown trigger="click" @command="handleCommand">
                        <span class="admin-trigger">
                            <span class="admin-avatar">{{ avatarText }}</span>
                            <span class="admin-name">{{ adminName }}</span>
                            <el-icon class="admin-arrow"><ArrowDown /></el-icon>
                        </span>
                        <template #dropdown>
                            <el-dropdown-menu>
                                <el-dropdown-item disabled>
                                    {{ admin.username || adminName }}
                                </el-dropdown-item>
                                <el-dropdown-item command="logout" divided>
                                    退出登录
                                </el-dropdown-item>
                            </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                </div>
            </header>

            <main class="content">
                <router-view v-slot="{ Component }">
                    <transition name="fade" mode="out-in">
                        <component :is="Component" />
                    </transition>
                </router-view>
            </main>
        </div>
    </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown, Document, Grid, Lock, Odometer, User } from '@element-plus/icons-vue'
import { useAuthStore } from '../store/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const admin = computed(() => authStore.admin || {})
const menus = computed(() => authStore.menus || [])
const pageTitle = computed(() => route.meta.title || '工作台')
const activeMenu = computed(() => route.path)
const adminName = computed(() => authStore.adminName)
const avatarText = computed(() => adminName.value.slice(0, 1))
const openedMenus = computed(() =>
    menus.value
        .filter((item) => item.children && item.children.length)
        .map((item) => `sub-${item.id}`)
)

const iconGroups = [
    { keywords: ['工作台', '首页', '概览', '仪表'], icon: Odometer },
    { keywords: ['管理员', '账号', '用户'], icon: User },
    { keywords: ['角色', '权限'], icon: Lock },
    { keywords: ['日志', '记录'], icon: Document }
]

function iconOf(menu) {
    const name = menu.name || ''
    const group = iconGroups.find((item) => item.keywords.some((word) => name.includes(word)))
    return group ? group.icon : Grid
}

function pathOf(menu) {
    return menu.path || `/module/${menu.id}`
}

async function handleCommand(command) {
    if (command !== 'logout') {
        return
    }
    try {
        await ElMessageBox.confirm('确定要退出当前账号吗？', '退出登录', {
            type: 'warning',
            confirmButtonText: '退出登录',
            cancelButtonText: '取消'
        })
    } catch (error) {
        return
    }
    authStore.logout()
    ElMessage.success('已退出登录')
    router.replace('/login')
}

onMounted(async () => {
    if (authStore.token && menus.value.length === 0) {
        try {
            await authStore.loadInfo()
        } catch (error) {
            // 错误提示已由请求拦截统一处理
        }
    }
})
</script>

<style scoped>
.layout {
    display: flex;
    min-height: 100vh;
    background: var(--color-bg);
}

.sidebar {
    position: sticky;
    top: 0;
    display: flex;
    flex-direction: column;
    width: 240px;
    flex: 0 0 240px;
    height: 100vh;
    background: var(--color-dark);
    overflow: hidden;
}

.brand {
    display: flex;
    align-items: center;
    gap: 12px;
    height: 72px;
    padding: 0 20px;
}

.brand__mark {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 38px;
    height: 38px;
    border-radius: 12px;
    background: var(--color-primary);
    color: #FFFFFF;
    font-size: 16px;
    font-weight: 600;
}

.brand__name {
    color: #FFFFFF;
    font-size: 15px;
    font-weight: 600;
    letter-spacing: 0.5px;
}

.brand__sub {
    margin-top: 2px;
    color: #8C88AB;
    font-size: 12px;
}

.sidebar__scroll {
    flex: 1;
    min-height: 0;
    padding-top: 8px;
}

.sidebar__menu {
    border-right: none;
}

.sidebar__menu :deep(.el-menu-item),
.sidebar__menu :deep(.el-sub-menu__title) {
    height: 46px;
    line-height: 46px;
    margin: 4px 12px;
    border-radius: 10px;
    color: #A9A5C0;
    font-size: 14px;
}

.sidebar__menu :deep(.el-sub-menu .el-menu-item) {
    min-width: auto;
    padding-left: 48px !important;
}

.sidebar__menu :deep(.el-menu-item:hover),
.sidebar__menu :deep(.el-sub-menu__title:hover) {
    background: rgba(255, 255, 255, 0.06);
    color: #FFFFFF;
}

.sidebar__menu :deep(.el-menu-item.is-active) {
    background: var(--color-primary);
    color: #FFFFFF;
    font-weight: 500;
}

.sidebar__menu :deep(.el-sub-menu.is-active > .el-sub-menu__title) {
    color: #FFFFFF;
}

.sidebar__menu :deep(.el-sub-menu .el-menu) {
    background: transparent;
}

.sidebar__foot {
    padding: 16px 20px 20px;
    color: #6B6788;
    font-size: 12px;
}

.main {
    display: flex;
    flex-direction: column;
    flex: 1;
    min-width: 0;
}

.topbar {
    position: sticky;
    top: 0;
    z-index: 10;
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 64px;
    padding: 0 24px;
    background: var(--color-card);
    border-bottom: 1px solid var(--color-line);
}

.topbar__title {
    color: var(--color-text);
    font-size: 18px;
    font-weight: 600;
}

.topbar__crumb {
    margin-top: 2px;
    color: var(--color-text-muted);
    font-size: 12px;
}

.admin-trigger {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 6px 10px;
    border-radius: 10px;
    cursor: pointer;
    transition: background 0.2s;
}

.admin-trigger:hover {
    background: var(--color-primary-soft);
}

.admin-avatar {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 32px;
    height: 32px;
    border-radius: 50%;
    background: var(--color-primary-soft);
    color: var(--color-primary);
    font-size: 14px;
    font-weight: 600;
}

.admin-name {
    color: var(--color-text);
    font-size: 14px;
}

.admin-arrow {
    color: var(--color-text-muted);
    font-size: 12px;
}

.content {
    flex: 1;
    padding: 24px;
}

.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.18s ease;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
}
</style>
