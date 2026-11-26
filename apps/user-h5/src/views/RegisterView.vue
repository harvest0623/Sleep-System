<template>
    <div class="page">
        <div class="page-inner">
            <div class="auth-head">
                <div class="auth-eyebrow">JOIN US</div>
                <h1 class="auth-title">创建账号</h1>
                <p class="auth-subtitle">三步完成注册，今晚就开始改善睡眠</p>
            </div>

            <div class="glass-card">
                <div class="field">
                    <label class="field-label">手机号</label>
                    <input
                        v-model="phone"
                        class="field-input"
                        type="tel"
                        maxlength="11"
                        placeholder="请输入 11 位手机号"
                    />
                </div>

                <div class="field">
                    <label class="field-label">验证码</label>
                    <div class="field-row">
                        <input
                            v-model="code"
                            class="field-input"
                            type="text"
                            maxlength="6"
                            placeholder="6 位验证码"
                        />
                        <button class="code-btn" :disabled="countdown > 0 || sending" @click="handleSend">
                            {{ countdown > 0 ? countdown + "s" : "获取验证码" }}
                        </button>
                    </div>
                </div>

                <div class="field">
                    <label class="field-label">设置密码</label>
                    <input
                        v-model="password"
                        class="field-input"
                        type="password"
                        maxlength="20"
                        placeholder="至少 8 位，建议包含字母与数字"
                    />
                </div>

                <p class="form-tip" :class="{ ok: tipType === 'ok' }">{{ tip }}</p>

                <button class="btn-primary" :disabled="loading" @click="handleRegister">
                    {{ loading ? "注册中..." : "注册并继续" }}
                </button>
            </div>

            <div class="auth-foot">
                <router-link to="/login">已有账号，去登录</router-link>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { register, sendCaptcha } from "../api/auth";
import { useUserStore } from "../store/user";

const router = useRouter();
const userStore = useUserStore();

const phone = ref("");
const code = ref("");
const password = ref("");
const tip = ref("");
const tipType = ref("error");
const loading = ref(false);
const sending = ref(false);
const countdown = ref(0);

function isPhone(value) {
    return /^1\d{10}$/.test(value);
}

function showTip(message, type = "error") {
    tip.value = message;
    tipType.value = type;
}

async function handleSend() {
    if (!isPhone(phone.value)) {
        showTip("请输入正确的手机号");
        return;
    }
    sending.value = true;
    try {
        const data = await sendCaptcha(phone.value);
        showTip("验证码已发送，开发环境固定为 " + data.devCode, "ok");
        countdown.value = 60;
        const timer = window.setInterval(() => {
            countdown.value -= 1;
            if (countdown.value <= 0) {
                window.clearInterval(timer);
            }
        }, 1000);
    } catch (error) {
        showTip(error.message);
    } finally {
        sending.value = false;
    }
}

async function handleRegister() {
    if (!isPhone(phone.value)) {
        showTip("请输入正确的手机号");
        return;
    }
    if (!code.value) {
        showTip("请输入验证码");
        return;
    }
    if (!password.value || password.value.length < 8) {
        showTip("密码至少 8 位");
        return;
    }
    loading.value = true;
    try {
        const data = await register(phone.value, code.value, password.value);
        userStore.setAuth(data.token, data.user);
        router.replace("/privacy");
    } catch (error) {
        showTip(error.message);
    } finally {
        loading.value = false;
    }
}
</script>
