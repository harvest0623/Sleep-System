<template>
    <div class="splash">
        <div class="glow"></div>
        <div class="brand">
            <div class="moon">
                <span class="star star-a"></span>
                <span class="star star-b"></span>
            </div>
            <h1>晚安</h1>
            <p>好梦开始的地方</p>
        </div>
        <div class="loading">
            <span></span>
        </div>
    </div>
</template>

<script setup>
import { onMounted } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();

onMounted(() => {
    const token = localStorage.getItem("sleep_token");
    window.setTimeout(() => {
        router.replace(token ? "/home" : "/login");
    }, 1600);
});
</script>

<style scoped>
.splash {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background: radial-gradient(120% 90% at 50% 0%, #241B52 0%, #0B0918 55%, #080612 100%);
    position: relative;
    overflow: hidden;
}

.glow {
    position: absolute;
    top: 28%;
    left: 50%;
    width: 320px;
    height: 320px;
    transform: translateX(-50%);
    background: radial-gradient(circle, rgba(124, 92, 255, 0.35), transparent 62%);
    filter: blur(10px);
    animation: breathe 3.6s ease-in-out infinite;
}

.brand {
    position: relative;
    z-index: 1;
    text-align: center;
    animation: rise 0.9s ease both;
}

.moon {
    width: 74px;
    height: 74px;
    margin: 0 auto 26px;
    border-radius: 50%;
    background: linear-gradient(140deg, #FFF6D8, #F2D98B);
    box-shadow: 0 0 48px rgba(242, 217, 139, 0.45);
    position: relative;
}

.moon::after {
    content: "";
    position: absolute;
    top: 6px;
    left: 20px;
    width: 62px;
    height: 62px;
    border-radius: 50%;
    background: #140F2B;
}

.star {
    position: absolute;
    width: 4px;
    height: 4px;
    border-radius: 50%;
    background: #fff;
    opacity: 0.85;
}

.star-a {
    top: -18px;
    right: -22px;
    animation: twinkle 2.4s ease-in-out infinite;
}

.star-b {
    top: 12px;
    right: -40px;
    width: 3px;
    height: 3px;
    animation: twinkle 2.4s ease-in-out 0.8s infinite;
}

h1 {
    font-size: 34px;
    letter-spacing: 8px;
    margin: 0 0 10px;
    font-weight: 600;
    color: #fff;
}

p {
    margin: 0;
    font-size: 13px;
    letter-spacing: 4px;
    color: rgba(245, 243, 255, 0.5);
}

.loading {
    position: absolute;
    bottom: 72px;
    width: 120px;
    height: 3px;
    border-radius: 3px;
    background: rgba(255, 255, 255, 0.1);
    overflow: hidden;
}

.loading span {
    display: block;
    width: 40%;
    height: 100%;
    border-radius: 3px;
    background: linear-gradient(90deg, #8B6BFF, #5232D6);
    animation: slide 1.4s ease-in-out infinite;
}

@keyframes rise {
    from {
        opacity: 0;
        transform: translateY(18px);
    }
    to {
        opacity: 1;
        transform: none;
    }
}

@keyframes breathe {
    0%,
    100% {
        opacity: 0.75;
        transform: translateX(-50%) scale(1);
    }
    50% {
        opacity: 1;
        transform: translateX(-50%) scale(1.08);
    }
}

@keyframes twinkle {
    0%,
    100% {
        opacity: 0.25;
    }
    50% {
        opacity: 1;
    }
}

@keyframes slide {
    0% {
        transform: translateX(-100%);
    }
    100% {
        transform: translateX(320%);
    }
}
</style>
