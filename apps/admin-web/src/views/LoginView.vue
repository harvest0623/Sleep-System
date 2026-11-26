<template>
    <div class="login">
        <section class="login__brand">
            <div class="brand-inner">
                <div class="brand-head">
                    <span class="brand-head__mark">眠</span>
                    <span class="brand-head__text">SLEEP SYSTEM</span>
                </div>

                <h1 class="brand-inner__title">助眠服务<br />管理后台</h1>
                <p class="brand-inner__desc">
                    集中管理用户、预约与运营数据，让每一次睡眠服务都井然有序。
                </p>

                <ul class="brand-inner__list">
                    <li>
                        <el-icon><CircleCheck /></el-icon>
                        <span>账号与权限统一管理</span>
                    </li>
                    <li>
                        <el-icon><CircleCheck /></el-icon>
                        <span>运营数据实时掌握</span>
                    </li>
                    <li>
                        <el-icon><CircleCheck /></el-icon>
                        <span>关键操作全程留痕</span>
                    </li>
                </ul>

                <div class="brand-inner__foot">© 2026 Sleep System</div>
            </div>
            <span class="brand-glow brand-glow--one"></span>
            <span class="brand-glow brand-glow--two"></span>
        </section>

        <section class="login__form">
            <div class="form-card">
                <h2 class="form-card__title">欢迎回来</h2>
                <p class="form-card__sub">请使用管理员账号登录系统</p>

                <el-form
                    ref="formRef"
                    :model="form"
                    :rules="rules"
                    label-position="top"
                    size="large"
                    @submit.prevent="handleLogin"
                >
                    <el-form-item label="账号" prop="username">
                        <el-input
                            v-model="form.username"
                            placeholder="请输入管理员账号"
                            :prefix-icon="User"
                            clearable
                        />
                    </el-form-item>

                    <el-form-item label="密码" prop="password">
                        <el-input
                            v-model="form.password"
                            type="password"
                            placeholder="请输入登录密码"
                            :prefix-icon="Lock"
                            show-password
                            @keyup.enter="handleLogin"
                        />
                    </el-form-item>

                    <div class="form-card__row">
                        <span class="form-card__tip">请勿在公共设备保存账号信息</span>
                        <router-link class="form-card__link" to="/forgot">忘记密码？</router-link>
                    </div>

                    <el-button
                        class="form-card__submit"
                        type="primary"
                        size="large"
                        :loading="loading"
                        @click="handleLogin"
                    >
                        登录
                    </el-button>
                </el-form>

                <p class="form-card__copy">Sleep System · 助眠服务平台</p>
            </div>
        </section>
    </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { CircleCheck, Lock, User } from '@element-plus/icons-vue'
import { useAuthStore } from '../store/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const formRef = ref(null)
const loading = ref(false)
const form = reactive({
    username: '',
    password: ''
})

const rules = {
    username: [{ required: true, message: '请输入管理员账号', trigger: 'blur' }],
    password: [{ required: true, message: '请输入登录密码', trigger: 'blur' }]
}

async function handleLogin() {
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
        await authStore.login({ username: form.username, password: form.password })
        ElMessage.success('登录成功')
        const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/dashboard'
        router.replace(redirect)
    } catch (error) {
        // 错误提示已由请求拦截统一处理
    } finally {
        loading.value = false
    }
}
</script>

<style scoped>
.login {
    display: grid;
    grid-template-columns: 1.05fr 1fr;
    min-height: 100vh;
    background: var(--color-bg);
}

.login__brand {
    position: relative;
    display: flex;
    align-items: center;
    padding: 64px 72px;
    background: var(--color-dark);
    overflow: hidden;
}

.brand-inner {
    position: relative;
    z-index: 1;
    max-width: 420px;
}

.brand-head {
    display: flex;
    align-items: center;
    gap: 12px;
}

.brand-head__mark {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 40px;
    height: 40px;
    border-radius: 12px;
    background: var(--color-primary);
    color: #FFFFFF;
    font-size: 17px;
    font-weight: 600;
}

.brand-head__text {
    color: #B9B5D2;
    font-size: 12px;
    letter-spacing: 3px;
}

.brand-inner__title {
    margin-top: 56px;
    color: #FFFFFF;
    font-size: 36px;
    font-weight: 600;
    line-height: 1.35;
    letter-spacing: 1px;
}

.brand-inner__desc {
    margin-top: 20px;
    color: #A9A5C0;
    font-size: 14px;
    line-height: 1.9;
}

.brand-inner__list {
    margin: 40px 0 0;
    padding: 0;
    list-style: none;
}

.brand-inner__list li {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 16px;
    color: #D6D3E6;
    font-size: 14px;
}

.brand-inner__list .el-icon {
    color: var(--color-primary);
    font-size: 16px;
}

.brand-inner__foot {
    margin-top: 64px;
    color: #6B6788;
    font-size: 12px;
    letter-spacing: 0.5px;
}

.brand-glow {
    position: absolute;
    border-radius: 50%;
    filter: blur(80px);
    opacity: 0.45;
}

.brand-glow--one {
    top: -120px;
    right: -80px;
    width: 360px;
    height: 360px;
    background: rgba(109, 74, 255, 0.55);
}

.brand-glow--two {
    bottom: -160px;
    left: -60px;
    width: 320px;
    height: 320px;
    background: rgba(109, 74, 255, 0.25);
}

.login__form {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 48px 40px;
    background: #FFFFFF;
}

.form-card {
    width: 100%;
    max-width: 380px;
}

.form-card__title {
    color: var(--color-text);
    font-size: 26px;
    font-weight: 600;
}

.form-card__sub {
    margin: 10px 0 36px;
    color: var(--color-text-muted);
    font-size: 14px;
}

.form-card__row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 24px;
}

.form-card__tip {
    color: var(--color-text-muted);
    font-size: 12px;
}

.form-card__link {
    color: var(--color-primary);
    font-size: 13px;
}

.form-card__link:hover {
    color: var(--color-primary-dark);
}

.form-card__submit {
    width: 100%;
    height: 46px;
    font-size: 15px;
    letter-spacing: 2px;
}

.form-card__copy {
    margin-top: 40px;
    color: #B9B5C9;
    font-size: 12px;
    text-align: center;
}

@media (max-width: 980px) {
    .login {
        grid-template-columns: 1fr;
    }

    .login__brand {
        display: none;
    }
}
</style>
