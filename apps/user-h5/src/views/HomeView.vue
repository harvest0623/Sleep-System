<template>
    <div class="page">
        <div class="page-inner">
            <header class="home-head">
                <div>
                    <p class="greet">{{ greeting }}</p>
                    <h1>{{ userStore.displayName }}</h1>
                </div>
                <div class="avatar">{{ initial }}</div>
            </header>

            <section class="hero-card">
                <div class="hero-top">
                    <span class="pill">今晚建议</span>
                    <span class="hero-time">22:30 入睡</span>
                </div>
                <h2>睡前 10 分钟，试试 4-7-8 呼吸</h2>
                <p>吸气 4 秒，屏息 7 秒，呼气 8 秒，重复 4 轮，有助于降低心率。</p>
                <div class="wave">
                    <span v-for="index in 28" :key="index" :style="{ height: waveHeight(index) }"></span>
                </div>
            </section>

            <section class="stats">
                <div class="stat-card">
                    <span class="stat-label">昨夜睡眠</span>
                    <strong>--</strong>
                    <span class="stat-unit">小时</span>
                    <p>记录功能将在下个版本开放</p>
                </div>
                <div class="stat-card">
                    <span class="stat-label">连续打卡</span>
                    <strong>0</strong>
                    <span class="stat-unit">天</span>
                    <p>完成首次记录即可开始</p>
                </div>
            </section>

            <section class="quick">
                <button class="quick-item" @click="go('/onboard')">
                    <span class="dot"></span>
                    <span>完善睡眠档案</span>
                </button>
                <button class="quick-item" @click="handleLogout">
                    <span class="dot danger"></span>
                    <span>退出登录</span>
                </button>
            </section>
        </div>
    </div>
</template>

<script setup>
import { computed } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "../store/user";

const router = useRouter();
const userStore = useUserStore();

const greeting = computed(() => {
    const hour = new Date().getHours();
    if (hour < 6) {
        return "夜深了，早点休息";
    }
    if (hour < 11) {
        return "早上好，昨晚睡得如何";
    }
    if (hour < 18) {
        return "下午好，今天也要好好休息";
    }
    return "晚上好，准备进入睡眠模式";
});

const initial = computed(() => {
    const name = userStore.displayName || "助";
    return name.slice(0, 1);
});

function waveHeight(index) {
    const value = Math.abs(Math.sin(index * 0.7)) * 26 + 6;
    return value + "px";
}

function go(path) {
    router.push(path);
}

function handleLogout() {
    userStore.logout();
    router.replace("/login");
}
</script>

<style scoped>
.home-head {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 26px 2px 22px;
}

.greet {
    margin: 0 0 6px;
    font-size: 12.5px;
    letter-spacing: 1px;
    color: var(--color-text-muted);
}

.home-head h1 {
    margin: 0;
    font-size: 26px;
    font-weight: 700;
}

.avatar {
    width: 48px;
    height: 48px;
    border-radius: 50%;
    background: linear-gradient(135deg, #8B6BFF, #5232D6);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 18px;
    font-weight: 600;
    box-shadow: 0 8px 20px rgba(109, 74, 255, 0.35);
}

.hero-card {
    background: linear-gradient(155deg, #2A1F5E 0%, #171233 60%, #120F26 100%);
    border: 1px solid rgba(124, 92, 255, 0.25);
    border-radius: 24px;
    padding: 22px 20px 18px;
    position: relative;
    overflow: hidden;
}

.hero-top {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 14px;
}

.pill {
    font-size: 11px;
    letter-spacing: 1px;
    padding: 4px 10px;
    border-radius: 999px;
    background: rgba(124, 92, 255, 0.22);
    color: #C9BAFF;
}

.hero-time {
    font-size: 12px;
    color: var(--color-text-muted);
}

.hero-card h2 {
    margin: 0 0 8px;
    font-size: 18px;
    line-height: 1.5;
    font-weight: 600;
}

.hero-card p {
    margin: 0;
    font-size: 12.5px;
    line-height: 1.8;
    color: var(--color-text-muted);
}

.wave {
    display: flex;
    align-items: center;
    gap: 3px;
    height: 34px;
    margin-top: 18px;
    opacity: 0.7;
}

.wave span {
    flex: 1;
    border-radius: 3px;
    background: linear-gradient(180deg, #8B6BFF, rgba(82, 50, 214, 0.2));
}

.stats {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 12px;
    margin-top: 16px;
}

.stat-card {
    background: var(--color-card);
    border: 1px solid var(--color-line);
    border-radius: 20px;
    padding: 18px 16px;
}

.stat-label {
    display: block;
    font-size: 12px;
    color: var(--color-text-muted);
}

.stat-card strong {
    font-size: 28px;
    font-weight: 700;
    margin-right: 4px;
}

.stat-unit {
    font-size: 12px;
    color: var(--color-text-muted);
}

.stat-card p {
    margin: 6px 0 0;
    font-size: 11px;
    color: var(--color-text-faint);
    line-height: 1.5;
}

.quick {
    margin-top: 20px;
    background: var(--color-card);
    border: 1px solid var(--color-line);
    border-radius: 20px;
    overflow: hidden;
}

.quick-item {
    width: 100%;
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 17px 18px;
    background: none;
    border: none;
    border-bottom: 1px solid var(--color-line);
    color: var(--color-text);
    font-size: 14px;
    cursor: pointer;
}

.quick-item:last-child {
    border-bottom: none;
}

.dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: var(--color-primary);
}

.dot.danger {
    background: var(--color-danger);
}
</style>
