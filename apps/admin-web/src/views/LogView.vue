<template>
    <div class="log">
        <div class="filter-card app-card">
            <el-form :inline="true" @submit.prevent="handleSearch">
                <el-form-item label="关键词">
                    <el-input
                        v-model="keyword"
                        class="filter-input"
                        placeholder="操作人 / 动作 / 对象"
                        clearable
                        @keyup.enter="handleSearch"
                        @clear="handleSearch"
                    />
                </el-form-item>
                <el-form-item label="模块">
                    <el-select
                        v-model="module"
                        class="filter-select"
                        placeholder="全部模块"
                        clearable
                        filterable
                        allow-create
                        @change="handleSearch"
                    >
                        <el-option
                            v-for="item in moduleOptions"
                            :key="item"
                            :label="item"
                            :value="item"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleSearch">查询</el-button>
                    <el-button @click="handleReset">重置</el-button>
                </el-form-item>
            </el-form>
        </div>

        <div class="table-card app-card">
            <div class="table-card__head">
                <div>
                    <div class="table-card__title">操作日志</div>
                    <div class="page-desc">共 {{ total }} 条操作记录</div>
                </div>
            </div>

            <el-table v-loading="loading" :data="records" row-key="id">
                <el-table-column prop="adminName" label="操作人" min-width="120">
                    <template #default="{ row }">{{ row.adminName || '—' }}</template>
                </el-table-column>
                <el-table-column label="模块" min-width="120">
                    <template #default="{ row }">
                        <el-tag v-if="row.module" type="info" effect="plain" round>{{ row.module }}</el-tag>
                        <span v-else class="muted">—</span>
                    </template>
                </el-table-column>
                <el-table-column prop="action" label="动作" min-width="150">
                    <template #default="{ row }">{{ row.action || '—' }}</template>
                </el-table-column>
                <el-table-column prop="target" label="操作对象" min-width="160">
                    <template #default="{ row }">{{ row.target || '—' }}</template>
                </el-table-column>
                <el-table-column prop="ip" label="IP 地址" min-width="140">
                    <template #default="{ row }">{{ row.ip || '—' }}</template>
                </el-table-column>
                <el-table-column label="结果" width="100">
                    <template #default="{ row }">
                        <el-tag :type="isSuccess(row.result) ? 'success' : 'danger'" effect="light" round>
                            {{ isSuccess(row.result) ? '成功' : '失败' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="耗时" width="100">
                    <template #default="{ row }">{{ row.costMs !== null && row.costMs !== undefined ? `${row.costMs} ms` : '—' }}</template>
                </el-table-column>
                <el-table-column prop="createdAt" label="操作时间" min-width="170">
                    <template #default="{ row }">{{ row.createdAt || '—' }}</template>
                </el-table-column>
                <template #empty>
                    <div class="empty">暂无操作日志</div>
                </template>
            </el-table>

            <div class="table-footer">
                <el-pagination
                    v-model:current-page="page"
                    v-model:page-size="size"
                    :total="total"
                    :page-sizes="[10, 20, 50]"
                    layout="total, sizes, prev, pager, next, jumper"
                    background
                    @current-change="loadList"
                    @size-change="handleSizeChange"
                />
            </div>
        </div>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getLogs } from '../api/admin'

const loading = ref(false)
const keyword = ref('')
const module = ref('')
const page = ref(1)
const size = ref(10)
const total = ref(0)
const records = ref([])

const moduleOptions = ['登录认证', '管理员', '角色权限', '用户管理', '订单', '预约', '内容审核', '系统']

function isSuccess(result) {
    return (
        result === 1 ||
        result === '1' ||
        result === true ||
        result === 'SUCCESS' ||
        result === 'success' ||
        result === '成功'
    )
}

async function loadList() {
    loading.value = true
    try {
        const data = await getLogs({
            page: page.value,
            size: size.value,
            keyword: keyword.value,
            module: module.value
        })
        records.value = (data && data.records) || []
        total.value = (data && data.total) || 0
    } catch (error) {
        // 错误提示已由请求拦截统一处理
    } finally {
        loading.value = false
    }
}

function handleSearch() {
    page.value = 1
    loadList()
}

function handleReset() {
    keyword.value = ''
    module.value = ''
    page.value = 1
    loadList()
}

function handleSizeChange() {
    page.value = 1
    loadList()
}

onMounted(loadList)
</script>

<style scoped>
.filter-card :deep(.el-form-item) {
    margin-bottom: 16px;
}

.filter-input {
    width: 240px;
}

.filter-select {
    width: 180px;
}

.muted {
    color: var(--color-text-muted);
}

.empty {
    padding: 32px 0;
    color: var(--color-text-muted);
}
</style>
