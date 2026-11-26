<template>
    <div class="login">
        <el-card shadow="never" class="login__card">
            <h1 class="login__title">助眠服务管理后台</h1>

            <el-form label-position="top">
                <el-form-item label="账号">
                    <el-input v-model="username" placeholder="请输入账号" />
                </el-form-item>
                <el-form-item label="密码">
                    <el-input
                        v-model="password"
                        type="password"
                        placeholder="请输入密码"
                        show-password
                    />
                </el-form-item>
            </el-form>

            <el-button type="primary" class="login__submit" @click="handleLogin">
                登录
            </el-button>
        </el-card>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const username = ref('')
const password = ref('')

function handleLogin() {
    router.push('/dashboard')
}
</script>

<style scoped>
.login {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--color-bg);
}

.login__card {
    width: 380px;
    border-radius: 16px;
    border: 1px solid var(--color-line);
}

.login__title {
    margin: 8px 0 24px;
    font-size: 20px;
    font-weight: 600;
    text-align: center;
    color: var(--color-text);
}

.login__submit {
    width: 100%;
}
</style>