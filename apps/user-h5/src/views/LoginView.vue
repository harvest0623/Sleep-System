<template>
    <div class="page">
        <div class="page-inner">
            <div class="auth-head">
                <div class="auth-eyebrow">SLEEP WELL</div>
                <h1 class="auth-title">欢迎回来</h1>
                <p class="auth-subtitle">用手机号快速登录，开启今晚的好眠</p>
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

                <p class="form-tip" :class="{ ok: tipType === 'ok' }">{{ tip }}</p>

                <button class="btn-primary" :disabled="loading" @click="handleLogin">
                    {{ loading ? "登录中..." : "登录" }}
                </button>
            </div>

            <div class="auth-foot">
                <router-link to="/register">注册新账号</router-link>
                <router-link to="/forgot">忘记密码</router-link>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { login, sendCaptcha, fetchProfile } from "../api/auth";
import { useUserStore } from "../store/user";

const router = useRouter();
const userStore = useUserStore();

const phone = ref("");
const code = ref("");
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

async function handleLogin() {
    if (!isPhone(phone.value)) {
        showTip("请输入正确的手机号");
        return;
    }
    if (!code.value) {
        showTip("请输入验证码");
        return;
    }
    loading.value = true;
    try {
        const data = await login(phone.value, code.value);
        userStore.setAuth(data.token, data.user);
        const profile = await fetchProfile();
        router.replace(profile.profile ? "/home" : "/onboard");
    } catch (error) {
        showTip(error.message);
    } finally {
        loading.value = false;
    }
}
</script>
