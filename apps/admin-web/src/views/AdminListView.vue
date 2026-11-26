<template>
    <div class="admin-list">
        <div class="filter-card app-card">
            <el-form :inline="true" @submit.prevent="handleSearch">
                <el-form-item label="关键词">
                    <el-input
                        v-model="keyword"
                        class="filter-input"
                        placeholder="账号 / 姓名 / 手机号"
                        clearable
                        @keyup.enter="handleSearch"
                        @clear="handleSearch"
                    />
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
                    <div class="table-card__title">管理员列表</div>
                    <div class="page-desc">共 {{ total }} 个管理员账号</div>
                </div>
                <el-button type="primary" :icon="Plus" @click="openCreate">新增管理员</el-button>
            </div>

            <el-table v-loading="loading" :data="records" row-key="id">
                <el-table-column prop="username" label="账号" min-width="130" />
                <el-table-column prop="realName" label="姓名" min-width="110" />
                <el-table-column prop="phone" label="手机号" min-width="140">
                    <template #default="{ row }">{{ row.phone || '—' }}</template>
                </el-table-column>
                <el-table-column label="角色" min-width="180">
                    <template #default="{ row }">
                        <template v-if="roleList(row).length">
                            <el-tag
                                v-for="name in roleList(row)"
                                :key="name"
                                class="role-tag"
                                type="primary"
                                effect="light"
                                round
                            >
                                {{ name }}
                            </el-tag>
                        </template>
                        <span v-else class="muted">未分配</span>
                    </template>
                </el-table-column>
                <el-table-column label="状态" width="120">
                    <template #default="{ row }">
                        <el-switch
                            :model-value="row.status"
                            :active-value="1"
                            :inactive-value="0"
                            inline-prompt
                            active-text="启用"
                            inactive-text="停用"
                            @change="(value) => handleStatusChange(row, value)"
                        />
                    </template>
                </el-table-column>
                <el-table-column prop="lastLoginTime" label="最后登录" min-width="170">
                    <template #default="{ row }">{{ row.lastLoginTime || '—' }}</template>
                </el-table-column>
                <el-table-column label="操作" width="150" fixed="right">
                    <template #default="{ row }">
                        <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
                        <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
                    </template>
                </el-table-column>
                <template #empty>
                    <div class="empty">暂无管理员数据</div>
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

        <el-drawer
            v-model="drawerVisible"
            :title="form.id ? '编辑管理员' : '新增管理员'"
            size="460px"
            destroy-on-close
            @closed="handleDrawerClosed"
        >
            <el-form
                ref="formRef"
                :model="form"
                :rules="rules"
                label-position="top"
                class="admin-form"
            >
                <el-form-item label="账号" prop="username">
                    <el-input v-model="form.username" placeholder="请输入登录账号" maxlength="30" />
                </el-form-item>

                <el-form-item label="姓名" prop="realName">
                    <el-input v-model="form.realName" placeholder="请输入真实姓名" maxlength="20" />
                </el-form-item>

                <el-form-item label="手机号" prop="phone">
                    <el-input v-model="form.phone" placeholder="请输入 11 位手机号" maxlength="11" />
                </el-form-item>

                <el-form-item label="初始密码" prop="password">
                    <el-input
                        v-model="form.password"
                        type="password"
                        :placeholder="form.id ? '留空表示不修改密码' : '请输入初始密码'"
                        show-password
                    />
                </el-form-item>

                <el-form-item label="状态" prop="status">
                    <el-radio-group v-model="form.status">
                        <el-radio :value="1">启用</el-radio>
                        <el-radio :value="0">停用</el-radio>
                    </el-radio-group>
                </el-form-item>

                <el-form-item label="角色" prop="roleIds">
                    <el-select
                        v-model="form.roleIds"
                        multiple
                        collapse-tags
                        collapse-tags-tooltip
                        placeholder="请选择角色"
                        class="admin-form__select"
                    >
                        <el-option
                            v-for="role in roleOptions"
                            :key="role.id"
                            :label="role.roleName"
                            :value="role.id"
                        />
                    </el-select>
                </el-form-item>
            </el-form>

            <template #footer>
                <el-button @click="drawerVisible = false">取消</el-button>
                <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
            </template>
        </el-drawer>
    </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {
    deleteAdmin,
    getAdmins,
    getRoles,
    saveAdmin,
    updateAdminStatus
} from '../api/admin'

const loading = ref(false)
const saving = ref(false)
const keyword = ref('')
const page = ref(1)
const size = ref(10)
const total = ref(0)
const records = ref([])
const roleOptions = ref([])

const drawerVisible = ref(false)
const formRef = ref(null)
const form = reactive({
    id: null,
    username: '',
    password: '',
    realName: '',
    phone: '',
    status: 1,
    roleIds: []
})

const rules = {
    username: [
        { required: true, message: '请输入登录账号', trigger: 'blur' },
        { min: 3, max: 30, message: '账号长度需为 3-30 位', trigger: 'blur' }
    ],
    realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
    phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }
    ],
    password: [
        {
            validator: (rule, value, callback) => {
                if (!form.id && !value) {
                    callback(new Error('请输入初始密码'))
                    return
                }
                if (value && value.length < 6) {
                    callback(new Error('密码长度不能少于 6 位'))
                    return
                }
                callback()
            },
            trigger: 'blur'
        }
    ]
}

function roleList(row) {
    if (Array.isArray(row.roleNames)) {
        return row.roleNames.filter(Boolean)
    }
    if (typeof row.roleNames === 'string' && row.roleNames) {
        return row.roleNames.split(',').filter(Boolean)
    }
    return []
}

async function loadList() {
    loading.value = true
    try {
        const data = await getAdmins({ page: page.value, size: size.value, keyword: keyword.value })
        records.value = (data && data.records) || []
        total.value = (data && data.total) || 0
    } catch (error) {
        // 错误提示已由请求拦截统一处理
    } finally {
        loading.value = false
    }
}

async function loadRoles() {
    try {
        const data = await getRoles()
        roleOptions.value = Array.isArray(data) ? data : []
    } catch (error) {
        roleOptions.value = []
    }
}

function handleSearch() {
    page.value = 1
    loadList()
}

function handleReset() {
    keyword.value = ''
    page.value = 1
    loadList()
}

function handleSizeChange() {
    page.value = 1
    loadList()
}

function resetForm() {
    form.id = null
    form.username = ''
    form.password = ''
    form.realName = ''
    form.phone = ''
    form.status = 1
    form.roleIds = []
}

function openCreate() {
    resetForm()
    drawerVisible.value = true
}

function openEdit(row) {
    resetForm()
    form.id = row.id
    form.username = row.username || ''
    form.realName = row.realName || ''
    form.phone = row.phone || ''
    form.status = row.status === 0 ? 0 : 1
    form.roleIds = Array.isArray(row.roleIds) ? [...row.roleIds] : []
    drawerVisible.value = true
}

function handleDrawerClosed() {
    resetForm()
    if (formRef.value) {
        formRef.value.clearValidate()
    }
}

async function handleSave() {
    if (!formRef.value || saving.value) {
        return
    }
    try {
        await formRef.value.validate()
    } catch (error) {
        return
    }

    const payload = {
        username: form.username,
        realName: form.realName,
        phone: form.phone,
        status: form.status,
        roleIds: form.roleIds
    }
    if (form.id) {
        payload.id = form.id
    }
    if (form.password) {
        payload.password = form.password
    }

    saving.value = true
    try {
        await saveAdmin(payload)
        ElMessage.success(form.id ? '管理员信息已更新' : '管理员创建成功')
        drawerVisible.value = false
        loadList()
    } catch (error) {
        // 错误提示已由请求拦截统一处理
    } finally {
        saving.value = false
    }
}

async function handleStatusChange(row, value) {
    try {
        await updateAdminStatus(row.id, value)
        row.status = value
        ElMessage.success(value === 1 ? '账号已启用' : '账号已停用')
    } catch (error) {
        loadList()
    }
}

async function handleDelete(row) {
    try {
        await ElMessageBox.confirm(
            `确定删除管理员「${row.realName || row.username}」吗？删除后不可恢复。`,
            '删除确认',
            { type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消' }
        )
    } catch (error) {
        return
    }

    try {
        await deleteAdmin(row.id)
        ElMessage.success('管理员已删除')
        if (records.value.length === 1 && page.value > 1) {
            page.value -= 1
        }
        loadList()
    } catch (error) {
        // 错误提示已由请求拦截统一处理
    }
}

onMounted(() => {
    loadList()
    loadRoles()
})
</script>

<style scoped>
.filter-card :deep(.el-form-item) {
    margin-bottom: 16px;
}

.filter-input {
    width: 240px;
}

.role-tag {
    margin-right: 6px;
}

.muted {
    color: var(--color-text-muted);
}

.empty {
    padding: 32px 0;
    color: var(--color-text-muted);
}

.admin-form__select {
    width: 100%;
}

.table-card :deep(.el-table) {
    border-radius: 12px;
    overflow: hidden;
}
</style>
