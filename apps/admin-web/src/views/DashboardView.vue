<template>
    <div class="dashboard">
        <h1 class="dashboard__title">工作台</h1>

        <el-row :gutter="20">
            <el-col v-for="item in stats" :key="item.label" :span="6">
                <el-card shadow="never" class="stat-card">
                    <div class="stat-card__label">{{ item.label }}</div>
                    <div class="stat-card__value">{{ item.value }}</div>
                </el-card>
            </el-col>
        </el-row>

        <el-card shadow="never" class="content-card">
            <div class="content-card__placeholder">数据接入中</div>
        </el-card>
    </div>
</template>

<script setup>
const stats = [
    { label: '今日新增用户', value: 0 },
    { label: '今日订单额', value: 0 },
    { label: '待处理预约', value: 0 },
    { label: '待审核内容', value: 0 }
]
</script>

<style scoped>
.dashboard {
    min-height: 100vh;
    padding: 24px;
    background: var(--color-bg);
}

.dashboard__title {
    margin: 0 0 24px;
    font-size: 22px;
    font-weight: 600;
    color: var(--color-text);
}

.stat-card {
    border-radius: 12px;
    border: 1px solid var(--color-line);
}

.stat-card__label {
    font-size: 13px;
    color: var(--color-text-muted);
}

.stat-card__value {
    margin-top: 12px;
    font-size: 28px;
    font-weight: 600;
    color: var(--color-text);
}

.content-card {
    margin-top: 20px;
    border-radius: 12px;
    border: 1px solid var(--color-line);
}

.content-card__placeholder {
    height: 320px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--color-text-muted);
    font-size: 14px;
}
</style>