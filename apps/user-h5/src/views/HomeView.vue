<template>
    <div class="home">
        <div class="home__inner">
            <h1 class="home__title">晚安，好梦开始的地方</h1>

            <div class="overview">
                <div class="overview__label">睡眠概览</div>
                <div class="overview__value">数据接入中</div>
            </div>

            <button class="login-btn" type="button" @click="goLogin">开始使用</button>
        </div>
    </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

function goLogin() {
    router.push('/login')
}
</script>

<style scoped>
.home {
    min-height: 100vh;
    background: var(--color-bg);
    display: flex;
    justify-content: center;
}

.home__inner {
    width: 100%;
    max-width: 375px;
    min-height: 100vh;
    padding: 48px 24px 32px;
    display: flex;
    flex-direction: column;
}

.home__title {
    margin: 0 0 32px;
    font-size: 26px;
    line-height: 1.4;
    font-weight: 600;
    color: var(--color-text);
}

.overview {
    flex: 1;
    border-radius: var(--radius-card);
    background: var(--color-card);
    border: 1px solid var(--color-line);
    padding: 32px 24px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    gap: 12px;
}

.overview__label {
    font-size: 14px;
    color: var(--color-text-muted);
    letter-spacing: 1px;
}

.overview__value {
    font-size: 22px;
    font-weight: 600;
    color: var(--color-text);
}

.login-btn {
    margin-top: 32px;
    height: 48px;
    border: none;
    border-radius: var(--radius-btn);
    background: var(--color-primary);
    color: var(--color-text);
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
}

.login-btn:active {
    background: var(--color-primary-deep);
}
</style>