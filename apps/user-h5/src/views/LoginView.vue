<template>
    <div class="login">
        <div class="login__inner">
            <h1 class="login__title">登录</h1>

            <form class="login__form" @submit.prevent="handleLogin">
                <input
                    v-model="phone"
                    class="field"
                    type="tel"
                    maxlength="11"
                    placeholder="请输入手机号"
                />
                <input
                    v-model="code"
                    class="field"
                    type="text"
                    maxlength="6"
                    placeholder="请输入验证码"
                />
                <button class="submit-btn" type="submit">登录</button>
            </form>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const phone = ref('')
const code = ref('')

function handleLogin() {
    router.push('/home')
}
</script>

<style scoped>
.login {
    min-height: 100vh;
    background: var(--color-bg);
    display: flex;
    justify-content: center;
}

.login__inner {
    width: 100%;
    max-width: 375px;
    min-height: 100vh;
    padding: 48px 24px 32px;
    display: flex;
    flex-direction: column;
}

.login__title {
    margin: 0 0 32px;
    font-size: 26px;
    font-weight: 600;
    color: var(--color-text);
}

.login__form {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.field {
    height: 48px;
    padding: 0 18px;
    border-radius: var(--radius-btn);
    border: 1px solid var(--color-line);
    background: var(--color-card);
    color: var(--color-text);
    font-size: 15px;
    outline: none;
}

.field::placeholder {
    color: var(--color-text-muted);
}

.field:focus {
    border-color: var(--color-primary);
}

.submit-btn {
    margin-top: 8px;
    height: 48px;
    border: none;
    border-radius: var(--radius-btn);
    background: var(--color-primary);
    color: var(--color-text);
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
}

.submit-btn:active {
    background: var(--color-primary-deep);
}
</style>