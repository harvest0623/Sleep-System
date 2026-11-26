<template>
    <div class="forgot">
        <div class="forgot__card app-card">
            <div class="forgot__head">
                <span class="forgot__mark">眠</span>
                <div>
                    <h1 class="forgot__title">重置登录密码</h1>
                    <p class="forgot__sub">通过管理员账号与预留手机号完成验证</p>
                </div>
            </div>

            <el-form
                ref="formRef"
                :model="form"
                :rules="rules"
                label-position="top"
                size="large"
            >
                <el-form-item label="管理员账号" prop="username">
                    <el-input v-model="form.username" placeholder="请输入管理员账号" clearable />
                </el-form-item>

                <el-form-item label="预留手机号" prop="phone">
                    <el-input v-model="form.phone" placeholder="请输入 11 位手机号" maxlength="11" clearable />
                </el-form-item>

                <el-form-item label="短信验证码" prop="code">
                    <el-input v-model="form.code" placeholder="请输入验证码" maxlength="6" clearable />
                </el-form-item>

                <el-form-item label="新密码" prop="newPassword">
                    <el-input
                        v-model="form.newPassword"
                        type="password"
                        placeholder="请输入不少于 6 位的新密码"
                        show-password
                    />
                </el-form-item>
            </el-form>

            <el-button
                class="forgot__submit"
                type="primary"
                size="large"
                :loading="loading"
                @click="handleSubmit"
            >
                确认重置
            </el-button>

            <div class="forgot__foot">
                <router-link class="forgot__link" to="/login">返回登录</router-link>
            </div>
        </div>
    </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { resetPassword } from '../api/admin'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
    username: '',
    phone: '',
    code: '',
    newPassword: ''
})

const rules = {
    username: [{ required: true, message: '请输入管理员账号', trigger: 'blur' }],
    phone: [
        { required: true, message: '请输入预留手机号', trigger: 'blur' },
        { pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }
    ],
    code: [{ required: true, message: '请输入短信验证码', trigger: 'blur' }],
    newPassword: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 6, message: '密码长度不能少于 6 位', trigger: 'blur' }
    ]
}

async function handleSubmit() {
    if (!formRef.value || loading.value) {
        return
    }
    try {
        await formRef.value.validate()
    } catch (error) {
        return
    }

    loading.value = true
    try {
        await resetPassword({
            username: form.username,
            phone: form.phone,
            code: form.code,
            newPassword: form.newPassword
        })
        ElMessage.success('密码重置成功，请使用新密码登录')
        router.replace('/login')
    } catch (error) {
        // 错误提示已由请求拦截统一处理
    } finally {
        loading.value = false
    }
}
</script>

<style scoped>
.forgot {
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 100vh;
    padding: 40px 24px;
    background: var(--color-bg);
}

.forgot__card {
    width: 100%;
    max-width: 440px;
    padding: 36px 36px 28px;
}

.forgot__head {
    display: flex;
    align-items: center;
    gap: 14px;
    margin-bottom: 28px;
}

.forgot__mark {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 44px;
    height: 44px;
    border-radius: 14px;
    background: var(--color-primary-soft);
    color: var(--color-primary);
    font-size: 18px;
    font-weight: 600;
}

.forgot__title {
    color: var(--color-text);
    font-size: 20px;
    font-weight: 600;
}

.forgot__sub {
    margin-top: 6px;
    color: var(--color-text-muted);
    font-size: 13px;
}

.forgot__submit {
    width: 100%;
    height: 44px;
    margin-top: 8px;
}

.forgot__foot {
    margin-top: 20px;
    text-align: center;
}

.forgot__link {
    color: var(--color-primary);
    font-size: 13px;
}
</style>
