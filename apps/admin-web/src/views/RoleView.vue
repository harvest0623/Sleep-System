<template>
    <div class="role">
        <section class="role__left app-card">
            <div class="role__head">
                <div>
                    <div class="table-card__title">角色列表</div>
                    <div class="page-desc">共 {{ roles.length }} 个角色</div>
                </div>
                <el-button type="primary" :icon="Plus" size="small" @click="openCreate">新增</el-button>
            </div>

            <div v-loading="loading" class="role__list">
                <div
                    v-for="role in roles"
                    :key="role.id"
                    class="role-item"
                    :class="{ 'is-active': current && current.id === role.id }"
                    @click="selectRole(role)"
                >
                    <div class="role-item__main">
                        <div class="role-item__name">{{ role.roleName }}</div>
                        <div class="role-item__meta">
                            <span class="role-item__code">{{ role.roleCode }}</span>
                            <span>{{ dataScopeText(role.dataScope) }}</span>
                        </div>
                    </div>
                    <el-button
                        class="role-item__edit"
                        link
                        type="primary"
                        :icon="Edit"
                        @click.stop="openEdit(role)"
                    />
                </div>

                <div v-if="!loading && roles.length === 0" class="role__empty">
                    暂无角色，请先新增
                </div>
            </div>
        </section>

        <section class="role__right app-card">
            <div class="role__head">
                <div>
                    <div class="table-card__title">菜单权限</div>
                    <div class="page-desc">
                        {{ current ? `正在配置「${current.roleName}」的可见菜单` : '请先选择左侧角色' }}
                    </div>
                </div>
                <el-button
                    type="primary"
                    :loading="saving"
                    :disabled="!current"
                    @click="handleSaveMenus"
                >
                    保存权限
                </el-button>
            </div>

            <div v-loading="treeLoading" class="tree-wrap">
                <el-tree
                    ref="treeRef"
                    :data="menuTree"
                    :props="treeProps"
                    node-key="id"
                    show-checkbox
                    default-expand-all
                    :empty-text="current ? '暂无可配置菜单' : '请选择角色后配置权限'"
                />
            </div>
        </section>

        <el-dialog
            v-model="dialogVisible"
            :title="form.id ? '编辑角色' : '新增角色'"
            width="460px"
            destroy-on-close
            @closed="handleDialogClosed"
        >
            <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
                <el-form-item label="角色名称" prop="roleName">
                    <el-input v-model="form.roleName" placeholder="如：运营管理员" maxlength="20" />
                </el-form-item>

                <el-form-item label="角色编码" prop="roleCode">
                    <el-input v-model="form.roleCode" placeholder="如：OPERATOR" maxlength="30" />
                </el-form-item>

                <el-form-item label="数据范围" prop="dataScope">
                    <el-select v-model="form.dataScope" class="full-width">
                        <el-option
                            v-for="item in dataScopeOptions"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                        />
                    </el-select>
                </el-form-item>

                <el-form-item label="备注" prop="remark">
                    <el-input
                        v-model="form.remark"
                        type="textarea"
                        :rows="3"
                        placeholder="角色职责说明（选填）"
                        maxlength="100"
                        show-word-limit
                    />
                </el-form-item>
            </el-form>

            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" :loading="dialogSaving" @click="handleSaveRole">保存</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { nextTick, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Edit, Plus } from '@element-plus/icons-vue'
import { getMenuTree, getRoleMenus, getRoles, saveRole, saveRoleMenus } from '../api/admin'

const loading = ref(false)
const treeLoading = ref(false)
const saving = ref(false)
const dialogSaving = ref(false)
const roles = ref([])
const current = ref(null)
const menuTree = ref([])
const treeRef = ref(null)
const dialogVisible = ref(false)
const formRef = ref(null)

const treeProps = { label: 'name', children: 'children' }
const dataScopeOptions = [
    { value: 1, label: '全部数据' },
    { value: 2, label: '本部门数据' },
    { value: 3, label: '仅本人数据' }
]

const form = reactive({
    id: null,
    roleName: '',
    roleCode: '',
    dataScope: 1,
    remark: ''
})

const rules = {
    roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
    roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
    dataScope: [{ required: true, message: '请选择数据范围', trigger: 'change' }]
}

function dataScopeText(value) {
    const hit = dataScopeOptions.find((item) => item.value === value)
    return hit ? hit.label : '未设置'
}

async function loadMenuTree() {
    try {
        const data = await getMenuTree()
        menuTree.value = Array.isArray(data) ? data : []
    } catch (error) {
        menuTree.value = []
    }
}

async function selectRole(role) {
    current.value = role
    treeLoading.value = true
    try {
        const checked = await getRoleMenus(role.id)
        await nextTick()
        if (treeRef.value) {
            treeRef.value.setCheckedKeys(Array.isArray(checked) ? checked : [])
        }
    } catch (error) {
        if (treeRef.value) {
            treeRef.value.setCheckedKeys([])
        }
    } finally {
        treeLoading.value = false
    }
}

async function loadRoles() {
    loading.value = true
    try {
        const data = await getRoles()
        roles.value = Array.isArray(data) ? data : []
        const keepId = current.value ? current.value.id : null
        const target = roles.value.find((item) => item.id === keepId) || roles.value[0]
        if (target) {
            await selectRole(target)
        } else {
            current.value = null
            if (treeRef.value) {
                treeRef.value.setCheckedKeys([])
            }
        }
    } catch (error) {
        roles.value = []
    } finally {
        loading.value = false
    }
}

async function handleSaveMenus() {
    if (!current.value || !treeRef.value || saving.value) {
        return
    }
    const checked = treeRef.value.getCheckedKeys()
    const halfChecked = treeRef.value.getHalfCheckedKeys()
    saving.value = true
    try {
        await saveRoleMenus(current.value.id, checked.concat(halfChecked))
        ElMessage.success('角色菜单权限已保存')
    } catch (error) {
        // 错误提示已由请求拦截统一处理
    } finally {
        saving.value = false
    }
}

function resetForm() {
    form.id = null
    form.roleName = ''
    form.roleCode = ''
    form.dataScope = 1
    form.remark = ''
}

function openCreate() {
    resetForm()
    dialogVisible.value = true
}

function openEdit(role) {
    resetForm()
    form.id = role.id
    form.roleName = role.roleName || ''
    form.roleCode = role.roleCode || ''
    form.dataScope = role.dataScope || 1
    form.remark = role.remark || ''
    dialogVisible.value = true
}

function handleDialogClosed() {
    resetForm()
    if (formRef.value) {
        formRef.value.clearValidate()
    }
}

async function handleSaveRole() {
    if (!formRef.value || dialogSaving.value) {
        return
    }
    try {
        await formRef.value.validate()
    } catch (error) {
        return
    }

    const payload = {
        roleName: form.roleName,
        roleCode: form.roleCode,
        dataScope: form.dataScope,
        remark: form.remark
    }
    if (form.id) {
        payload.id = form.id
    }

    dialogSaving.value = true
    try {
        await saveRole(payload)
        ElMessage.success(form.id ? '角色已更新' : '角色创建成功')
        dialogVisible.value = false
        loadRoles()
    } catch (error) {
        // 错误提示已由请求拦截统一处理
    } finally {
        dialogSaving.value = false
    }
}

onMounted(() => {
    loadMenuTree()
    loadRoles()
})
</script>

<style scoped>
.role {
    display: grid;
    grid-template-columns: 300px minmax(0, 1fr);
    gap: 16px;
    align-items: start;
}

.role__left,
.role__right {
    padding: 20px;
}

.role__head {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 12px;
    margin-bottom: 16px;
}

.role__list {
    display: flex;
    flex-direction: column;
    gap: 8px;
    min-height: 120px;
}

.role-item {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px 14px;
    border: 1px solid var(--color-line);
    border-radius: 12px;
    cursor: pointer;
    transition: border-color 0.2s, background 0.2s;
}

.role-item:hover {
    background: #FAF9FE;
    border-color: #D6CDFF;
}

.role-item.is-active {
    background: var(--color-primary-soft);
    border-color: #C9BBFF;
}

.role-item__main {
    flex: 1;
    min-width: 0;
}

.role-item__name {
    color: var(--color-text);
    font-size: 14px;
    font-weight: 500;
}

.role-item__meta {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-top: 6px;
    color: var(--color-text-muted);
    font-size: 12px;
}

.role-item__code {
    padding: 1px 8px;
    border-radius: 6px;
    background: #FFFFFF;
    color: var(--color-primary);
    font-size: 11px;
    border: 1px solid var(--color-line);
}

.role-item__edit {
    opacity: 0;
    transition: opacity 0.2s;
}

.role-item:hover .role-item__edit,
.role-item.is-active .role-item__edit {
    opacity: 1;
}

.role__empty {
    padding: 24px 0;
    color: var(--color-text-muted);
    font-size: 13px;
    text-align: center;
}

.tree-wrap {
    min-height: 320px;
    padding: 8px 4px;
}

.tree-wrap :deep(.el-tree-node__content) {
    height: 36px;
    border-radius: 8px;
}

.tree-wrap :deep(.el-tree-node__content:hover) {
    background: #FAF9FE;
}

.full-width {
    width: 100%;
}

@media (max-width: 1080px) {
    .role {
        grid-template-columns: 1fr;
    }
}
</style>
