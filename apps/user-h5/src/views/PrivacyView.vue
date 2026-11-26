<template>
    <div class="page">
        <div class="page-inner">
            <div class="auth-head">
                <div class="auth-eyebrow">PRIVACY</div>
                <h1 class="auth-title">隐私与授权</h1>
                <p class="auth-subtitle">我们只在必要范围内使用你的数据，用于改善睡眠体验</p>
            </div>

            <div class="glass-card agreement">
                <div v-for="item in agreements" :key="item.title" class="agreement-item">
                    <div class="agreement-head">
                        <span>{{ item.title }}</span>
                        <button class="link" @click="toggle(item.title)">
                            {{ expanded === item.title ? "收起" : "查看" }}
                        </button>
                    </div>
                    <p v-if="expanded === item.title" class="agreement-body">{{ item.content }}</p>
                </div>
            </div>

            <label class="check-row">
                <input v-model="agreed" type="checkbox" />
                <span>我已阅读并同意《用户协议》与《隐私政策》，同意在必要范围内处理我的睡眠数据</span>
            </label>

            <p class="form-tip">{{ tip }}</p>

            <button class="btn-primary" :disabled="!agreed" @click="handleNext">
                {{ agreed ? "同意并继续" : "请先阅读并勾选协议" }}
            </button>
        </div>
    </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();

const agreed = ref(false);
const expanded = ref("");
const tip = ref("");

const agreements = [
    {
        title: "用户协议",
        content: "本系统提供睡眠记录、助眠内容与专业咨询服务，不构成医疗诊断。如存在严重睡眠障碍，请及时就医。"
    },
    {
        title: "隐私政策",
        content: "我们仅采集手机号用于账号识别，睡眠记录与测评数据仅用于生成个性化报告。你可以随时在设置中导出或删除数据。"
    },
    {
        title: "健康数据授权",
        content: "睡眠时长、入睡时间、夜醒次数等属于敏感健康信息，我们采用加密存储，且不会向第三方出售。"
    }
];

function toggle(title) {
    expanded.value = expanded.value === title ? "" : title;
}

function handleNext() {
    if (!agreed.value) {
        tip.value = "请先阅读并勾选协议";
        return;
    }
    router.replace("/onboard");
}
</script>

<style scoped>
.agreement {
    padding: 8px 20px;
}

.agreement-item {
    padding: 16px 0;
    border-bottom: 1px solid var(--color-line);
}

.agreement-item:last-child {
    border-bottom: none;
}

.agreement-head {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 14px;
}

.link {
    border: none;
    background: none;
    color: var(--color-primary);
    font-size: 12.5px;
    padding: 0;
    cursor: pointer;
}

.agreement-body {
    margin: 12px 0 0;
    font-size: 13px;
    line-height: 1.8;
    color: var(--color-text-muted);
}

.check-row {
    display: flex;
    gap: 10px;
    align-items: flex-start;
    margin: 20px 4px 0;
    font-size: 12.5px;
    line-height: 1.7;
    color: var(--color-text-muted);
}

.check-row input {
    margin-top: 4px;
    width: 16px;
    height: 16px;
    accent-color: var(--color-primary);
    flex: none;
}
</style>
