import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { getInfo, login as loginApi } from '../api/admin'
import { clearAuth, getToken, readCached, saveAuth } from '../api/request'

export const useAuthStore = defineStore('auth', () => {
    const token = ref(getToken())
    const admin = ref(readCached('admin_info', null))
    const menus = ref(readCached('admin_menus', []))

    const isLoggedIn = computed(() => Boolean(token.value))
    const adminName = computed(() => {
        const info = admin.value || {}
        return info.realName || info.username || '管理员'
    })

    function applyAuth(data) {
        token.value = data.token || getToken()
        admin.value = data.admin || null
        menus.value = data.menus || []
        saveAuth(token.value, admin.value, menus.value)
    }

    async function login(payload) {
        const data = await loginApi(payload)
        applyAuth(data)
        return data
    }

    async function loadInfo() {
        const data = await getInfo()
        admin.value = data.admin || null
        menus.value = data.menus || []
        saveAuth(token.value, admin.value, menus.value)
        return data
    }

    function logout() {
        token.value = ''
        admin.value = null
        menus.value = []
        clearAuth()
    }

    return {
        token,
        admin,
        menus,
        isLoggedIn,
        adminName,
        login,
        loadInfo,
        logout
    }
})
