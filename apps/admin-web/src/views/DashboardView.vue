<template>
    <div class="dashboard">
        <section class="stats" v-loading="loading">
            <div v-for="item in stats" :key="item.key" class="stat-card app-card">
                <div class="stat-card__top">
                    <span class="stat-card__label">{{ item.label }}</span>
                    <span class="stat-card__icon" :style="{ background: item.soft, color: item.color }">
                        <el-icon><component :is="item.icon" /></el-icon>
                    </span>
                </div>
                <div class="stat-card__value">{{ item.value }}</div>
                <div class="stat-card__foot">{{ item.foot }}</div>
            </div>
        </section>

        <section class="panel">
            <div class="panel__main app-card">
                <div class="panel__head">
                    <div>
                        <h3 class="panel__title">快捷入口</h3>
                        <p class="panel__desc">常用管理功能，一键直达</p>
                    </div>
                </div>

                <div class="shortcuts">
                    <button class="shortcut" type="button" @click="router.push('/admins')">
                        <el-icon class="shortcut__icon"><User /></el-icon>
                        <span class="shortcut__name">管理员管理</span>
                        <span class="shortcut__desc">账号、状态与角色分配</span>
                    </button>
                    <button class="shortcut" type="button" @click="router.push('/roles')">
                        <el-icon class="shortcut__icon"><Lock /></el-icon>
                        <span class="shortcut__name">角色权限</span>
                        <span class="shortcut__desc">角色维护与菜单授权</span>
                    </button>
                    <button class="shortcut" type="button" @click="router.push('/logs')">
                        <el-icon class="shortcut__icon"><Document /></el-icon>
                        <span class="shortcut__name">操作日志</span>
                        <span class="shortcut__desc">关键操作记录追溯</span>
                    </button>
                </div>
            </div>

            <div class="panel__side app-card">
                <h3 class="panel__title">待办提醒</h3>
                <p class="panel__desc">需要及时处理的事项</p>

                <div class="todo-item">
                    <span class="todo-item__dot"></span>
                    <span class="todo-item__text">待处理预约</span>
                    <span class="todo-item__num">{{ summary.pendingAppointments }}</span>
                </div>
                <div class="todo-item">
                    <span class="todo-item__dot todo-item__dot--warn"></span>
                    <span class="todo-item__text">待审核内容</span>
                    <span class="todo-item__num">{{ summary.pendingReviews }}</span>
                </div>

                <div class="panel__note">数据来自 /admin/dashboard/summary 实时汇总</div>
            </div>
        </section>
    </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Calendar, Document, DocumentChecked, Lock, User, UserFilled, Wallet } from '@element-plus/icons-vue'
import { getDashboardSummary } from '../api/admin'

const router = useRouter()
const loading = ref(false)

const summary = reactive({
    todayUsers: 0,
    totalUsers: 0,
    todayAmount: 0,
    pendingAppointments: 0,
    pendingReviews: 0
})

function formatNumber(value) {
    return Number(value || 0).toLocaleString('zh-CN')
}

function formatAmount(value) {
    return `¥${Number(value || 0).toLocaleString('zh-CN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    })}`
}

const stats = computed(() => [
    {
        key: 'todayUsers',
        label: '今日新增用户',
        value: formatNumber(summary.todayUsers),
        foot: '今日新注册用户数',
        icon: User,
        color: '#6D4AFF',
        soft: '#F0ECFF'
    },
    {
        key: 'totalUsers',
        label: '累计用户',
        value: formatNumber(summary.totalUsers),
        foot: '平台注册用户总数',
        icon: UserFilled,
        color: '#2F80ED',
        soft: '#EAF2FF'
    },
    {
        key: 'todayAmount',
        label: '今日订单额',
        value: formatAmount(summary.todayAmount),
        foot: '今日成交订单金额',
        icon: Wallet,
        color: '#F2994A',
        soft: '#FFF3E6'
    },
    {
        key: 'pendingAppointments',
        label: '待处理预约',
        value: formatNumber(summary.pendingAppointments),
        foot: '等待确认的服务预约',
        icon: Calendar,
        color: '#27AE60',
        soft: '#E9F8EF'
    },
    {
        key: 'pendingReviews',
        label: '待审核内容',
        value: formatNumber(summary.pendingReviews),
        foot: '等待审核的内容数量',
        icon: DocumentChecked,
        color: '#EB5757',
        soft: '#FDECEC'
    }
])

async function loadSummary() {
    loading.value = true
    try {
        const data = await getDashboardSummary()
        Object.assign(summary, data || {})
    } catch (error) {
        // 错误提示已由请求拦截统一处理
    } finally {
        loading.value = false
    }
}

onMounted(loadSummary)
</script>

<style scoped>
.dashboard {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.stats {
    display: grid;
    grid-template-columns: repeat(5, minmax(0, 1fr));
    gap: 16px;
}

.stat-card {
    padding: 20px;
    transition: box-shadow 0.2s, transform 0.2s;
}

.stat-card:hover {
    box-shadow: 0 10px 30px rgba(35, 22, 90, 0.08);
    transform: translateY(-2px);
}

.stat-card__top {
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.stat-card__label {
    color: var(--color-text-muted);
    font-size: 13px;
}

.stat-card__icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 34px;
    height: 34px;
    border-radius: 10px;
    font-size: 17px;
}

.stat-card__value {
    margin-top: 16px;
    color: var(--color-text);
    font-size: 26px;
    font-weight: 600;
    letter-spacing: 0.5px;
}

.stat-card__foot {
    margin-top: 8px;
    color: #B2AFC2;
    font-size: 12px;
}

.panel {
    display: grid;
    grid-template-columns: minmax(0, 2.2fr) minmax(0, 1fr);
    gap: 16px;
}

.panel__main,
.panel__side {
    padding: 24px;
}

.panel__head {
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.panel__title {
    color: var(--color-text);
    font-size: 15px;
    font-weight: 600;
}

.panel__desc {
    margin-top: 6px;
    color: var(--color-text-muted);
    font-size: 12px;
}

.shortcuts {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 14px;
    margin-top: 22px;
}

.shortcut {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: 6px;
    padding: 18px;
    border: 1px solid var(--color-line);
    border-radius: 14px;
    background: #FCFBFF;
    cursor: pointer;
    text-align: left;
    transition: border-color 0.2s, background 0.2s;
}

.shortcut:hover {
    border-color: #CFC4FF;
    background: var(--color-primary-soft);
}

.shortcut__icon {
    color: var(--color-primary);
    font-size: 20px;
}

.shortcut__name {
    color: var(--color-text);
    font-size: 14px;
    font-weight: 500;
}

.shortcut__desc {
    color: var(--color-text-muted);
    font-size: 12px;
}

.todo-item {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-top: 18px;
    padding: 14px 16px;
    border-radius: 12px;
    background: #FAF9FE;
}

.todo-item__dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: var(--color-primary);
}

.todo-item__dot--warn {
    background: #F2994A;
}

.todo-item__text {
    flex: 1;
    color: var(--color-text-secondary);
    font-size: 13px;
}

.todo-item__num {
    color: var(--color-text);
    font-size: 18px;
    font-weight: 600;
}

.panel__note {
    margin-top: 22px;
    color: #B2AFC2;
    font-size: 12px;
    line-height: 1.7;
}

@media (max-width: 1440px) {
    .stats {
        grid-template-columns: repeat(3, minmax(0, 1fr));
    }
}

@media (max-width: 1180px) {
    .panel {
        grid-template-columns: 1fr;
    }
}

@media (max-width: 860px) {
    .stats,
    .shortcuts {
        grid-template-columns: repeat(2, minmax(0, 1fr));
    }
}
</style>
