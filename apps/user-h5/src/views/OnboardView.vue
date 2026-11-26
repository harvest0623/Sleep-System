<template>
    <div class="page">
        <div class="page-inner">
            <div class="steps">
                <span v-for="index in 3" :key="index" class="step" :class="{ active: step >= index }"></span>
            </div>

            <div class="auth-head">
                <div class="auth-eyebrow">STEP {{ step }} / 3</div>
                <h1 class="auth-title">{{ current.title }}</h1>
                <p class="auth-subtitle">{{ current.subtitle }}</p>
            </div>

            <div class="glass-card">
                <template v-if="step === 1">
                    <p class="block-label">你目前的入睡困难程度</p>
                    <div class="scale">
                        <button
                            v-for="level in 5"
                            :key="level"
                            class="scale-item"
                            :class="{ selected: difficulty === level }"
                            @click="difficulty = level"
                        >
                            <strong>{{ level }}</strong>
                            <span>{{ levelText[level - 1] }}</span>
                        </button>
                    </div>
                </template>

                <template v-if="step === 2">
                    <p class="block-label">平均需要多久才能入睡</p>
                    <div class="chips">
                        <button
                            v-for="item in latencyOptions"
                            :key="item.value"
                            class="chip"
                            :class="{ selected: latency === item.value }"
                            @click="latency = item.value"
                        >
                            {{ item.label }}
                        </button>
                    </div>

                    <p class="block-label spaced">夜里容易醒来的时段（可多选）</p>
                    <div class="chips">
                        <button
                            v-for="item in wakeOptions"
                            :key="item"
                            class="chip"
                            :class="{ selected: wakePeriods.includes(item) }"
                            @click="toggle(wakePeriods, item)"
                        >
                            {{ item }}
                        </button>
                    </div>
                </template>

                <template v-if="step === 3">
                    <p class="block-label">你更偏好哪类助眠内容（可多选）</p>
                    <div class="chips">
                        <button
                            v-for="item in preferenceOptions"
                            :key="item"
                            class="chip"
                            :class="{ selected: preferences.includes(item) }"
                            @click="toggle(preferences, item)"
                        >
                            {{ item }}
                        </button>
                    </div>

                    <p class="block-label spaced">希望每晚几点提醒你准备入睡</p>
                    <input v-model="remindTime" class="field-input" type="time" />
                </template>

                <p class="form-tip">{{ tip }}</p>

                <div class="actions">
                    <button v-if="step > 1" class="btn-ghost" @click="step -= 1">上一步</button>
                    <button class="btn-primary" :disabled="loading" @click="handleNext">
                        {{ step === 3 ? (loading ? "保存中..." : "完成建档") : "下一步" }}
                    </button>
                </div>
            </div>

            <button class="skip" @click="handleSkip">先跳过，稍后完善</button>
        </div>
    </div>
</template>

<script setup>
import { computed, ref } from "vue";
import { useRouter } from "vue-router";
import { saveOnboard } from "../api/auth";

const router = useRouter();

const step = ref(1);
const difficulty = ref(3);
const latency = ref(30);
const wakePeriods = ref([]);
const preferences = ref([]);
const remindTime = ref("22:30");
const tip = ref("");
const loading = ref(false);

const levelText = ["很轻", "较轻", "一般", "较重", "很重"];

const latencyOptions = [
    { value: 15, label: "15 分钟内" },
    { value: 30, label: "30 分钟左右" },
    { value: 45, label: "45 分钟" },
    { value: 60, label: "1 小时以上" },
    { value: 90, label: "很难入睡" }
];

const wakeOptions = ["入睡后 1-2 小时", "凌晨 2-4 点", "凌晨 4-6 点", "整夜易醒"];

const preferenceOptions = ["白噪音", "自然音", "轻音乐", "冥想引导", "睡前故事"];

const current = computed(() => {
    if (step.value === 1) {
        return { title: "先了解你的睡眠", subtitle: "这决定了我们为你推荐的内容强度" };
    }
    if (step.value === 2) {
        return { title: "你的入睡习惯", subtitle: "越具体，报告越准确" };
    }
    return { title: "今晚想听点什么", subtitle: "随时可以在设置中修改" };
});

function toggle(list, value) {
    const index = list.indexOf(value);
    if (index >= 0) {
        list.splice(index, 1);
    } else {
        list.push(value);
    }
}

async function handleNext() {
    if (step.value < 3) {
        step.value += 1;
        return;
    }
    loading.value = true;
    try {
        await saveOnboard({
            difficulty: difficulty.value,
            latency: latency.value,
            wakePeriods: wakePeriods.value,
            preferences: preferences.value,
            remindTime: remindTime.value
        });
        router.replace("/home");
    } catch (error) {
        tip.value = error.message;
    } finally {
        loading.value = false;
    }
}

function handleSkip() {
    router.replace("/home");
}
</script>

<style scoped>
.steps {
    display: flex;
    gap: 8px;
    padding-top: 12px;
}

.step {
    flex: 1;
    height: 4px;
    border-radius: 4px;
    background: rgba(255, 255, 255, 0.1);
    transition: background 0.3s;
}

.step.active {
    background: linear-gradient(90deg, #8B6BFF, #5232D6);
}

.block-label {
    font-size: 13px;
    color: var(--color-text-muted);
    margin: 0 0 12px;
}

.block-label.spaced {
    margin-top: 24px;
}

.scale {
    display: flex;
    gap: 8px;
}

.scale-item {
    flex: 1;
    padding: 14px 4px;
    border-radius: 14px;
    border: 1px solid var(--color-line);
    background: rgba(255, 255, 255, 0.04);
    color: var(--color-text);
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
    cursor: pointer;
    transition: 0.2s;
}

.scale-item strong {
    font-size: 17px;
}

.scale-item span {
    font-size: 10.5px;
    color: var(--color-text-muted);
}

.scale-item.selected {
    border-color: var(--color-primary);
    background: var(--color-primary-soft);
}

.chips {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
}

.chip {
    padding: 10px 16px;
    border-radius: 999px;
    border: 1px solid var(--color-line);
    background: rgba(255, 255, 255, 0.04);
    color: var(--color-text);
    font-size: 13px;
    cursor: pointer;
    transition: 0.2s;
}

.chip.selected {
    border-color: var(--color-primary);
    background: var(--color-primary-soft);
    color: #fff;
}

.actions {
    display: flex;
    gap: 12px;
    margin-top: 8px;
}

.actions .btn-ghost {
    flex: 0 0 96px;
}

.skip {
    display: block;
    margin: 18px auto 0;
    border: none;
    background: none;
    color: var(--color-text-faint);
    font-size: 12.5px;
    cursor: pointer;
}
</style>
