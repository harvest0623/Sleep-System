<template>
    <div class="page">
        <div class="page-inner">
            <div class="auth-head">
                <div class="auth-eyebrow">RESET</div>
                <h1 class="auth-title">重置密码</h1>
                <p class="auth-subtitle">验证手机号后即可设置新密码</p>
            </div>

            <div class="glass-card">
                <div class="field">
                    <label class="field-label">手机号</label>
                    <input
                        v-model="phone"
                        class="field-input"
                        type="tel"
                        maxlength="11"
                        placeholder="请输入注册时的手机号"
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
                    <label class="field-label">新密码</label>
                    <input
                        v-model="password"
                        class="field-input"
                        type="password"
                        maxlength="20"
                        placeholder="至少 8 位"
                    />
                </div>

                <p class="form-tip" :class="{ ok: tipType === 'ok' }">{{ tip }}</p>

                <button class="btn-primary" :disabled="loading" @click="handleSubmit">
                    {{ loading ? "提交中..." : "确认重置" }}
                </button>
            </div>

            <div class="auth-foot">
                <router-link to="/login">返回登录</router-link>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { resetPassword, sendCaptcha } from "../api/auth";

const router = useRouter();

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

async function handleSubmit() {
    if (!isPhone(phone.value)) {
        showTip("请输入正确的手机号");
        return;
    }
    if (!code.value) {
        showTip("请输入验证码");
        return;
    }
    if (!password.value || password.value.length < 8) {
        showTip("新密码至少 8 位");
        return;
    }
    loading.value = true;
    try {
        await resetPassword(phone.value, code.value, password.value);
        showTip("密码已重置，即将返回登录", "ok");
        window.setTimeout(() => router.replace("/login"), 900);
    } catch (error) {
        showTip(error.message);
    } finally {
        loading.value = false;
    }
}
</script>
