<template>
    <div class="placeholder">
        <div class="placeholder__card app-card">
            <div class="placeholder__icon">
                <el-icon><Tools /></el-icon>
            </div>
            <h2 class="placeholder__title">该模块正在开发中</h2>
            <p class="placeholder__desc">
                您访问的页面暂未开放，敬请期待后续版本。
            </p>
            <p class="placeholder__path">{{ route.fullPath }}</p>
            <el-button type="primary" @click="router.push('/dashboard')">返回工作台</el-button>
        </div>
    </div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { Tools } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
</script>

<style scoped>
.placeholder {
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: calc(100vh - 160px);
}

.placeholder__card {
    width: 100%;
    max-width: 460px;
    padding: 48px 32px;
    text-align: center;
}

.placeholder__icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 64px;
    height: 64px;
    margin: 0 auto 20px;
    border-radius: 20px;
    background: var(--color-primary-soft);
    color: var(--color-primary);
    font-size: 28px;
}

.placeholder__title {
    color: var(--color-text);
    font-size: 18px;
    font-weight: 600;
}

.placeholder__desc {
    margin-top: 10px;
    color: var(--color-text-muted);
    font-size: 13px;
    line-height: 1.8;
}

.placeholder__path {
    margin: 16px 0 24px;
    padding: 6px 12px;
    display: inline-block;
    border-radius: 8px;
    background: #FAF9FE;
    color: #A9A5C0;
    font-size: 12px;
    word-break: break-all;
}
</style>
