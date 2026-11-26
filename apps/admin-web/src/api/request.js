import { ElMessage } from 'element-plus'

const TOKEN_KEY = 'admin_token'
const INFO_KEY = 'admin_info'
const MENUS_KEY = 'admin_menus'

export function getToken() {
    return localStorage.getItem(TOKEN_KEY) || ''
}

export function saveAuth(token, admin, menus) {
    localStorage.setItem(TOKEN_KEY, token || '')
    localStorage.setItem(INFO_KEY, JSON.stringify(admin || null))
    localStorage.setItem(MENUS_KEY, JSON.stringify(menus || []))
}

export function readCached(key, fallback) {
    try {
        const raw = localStorage.getItem(key)
        return raw ? JSON.parse(raw) : fallback
    } catch (error) {
        return fallback
    }
}

export function clearAuth() {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(INFO_KEY)
    localStorage.removeItem(MENUS_KEY)
}

function buildQuery(params) {
    if (!params) {
        return ''
    }
    const search = new URLSearchParams()
    Object.keys(params).forEach((key) => {
        const value = params[key]
        if (value !== undefined && value !== null && value !== '') {
            search.append(key, value)
        }
    })
    const query = search.toString()
    return query ? `?${query}` : ''
}

async function handleUnauthorized() {
    clearAuth()
    ElMessage.error('登录状态已失效，请重新登录')
    if (window.location.pathname !== '/login') {
        window.location.href = '/login'
    }
}

export default async function request(url, options = {}) {
    const { method = 'GET', data, params, headers = {}, skipAuth = false } = options

    const requestHeaders = { Accept: 'application/json', ...headers }
    if (data !== undefined) {
        requestHeaders['Content-Type'] = 'application/json'
    }
    const token = getToken()
    if (token) {
        requestHeaders.Authorization = `Bearer ${token}`
    }

    let response
    try {
        response = await fetch(`/api${url}${buildQuery(params)}`, {
            method,
            headers: requestHeaders,
            body: data === undefined ? undefined : JSON.stringify(data)
        })
    } catch (error) {
        ElMessage.error('网络异常，请稍后重试')
        throw error
    }

    if (response.status === 401 && !skipAuth) {
        await handleUnauthorized()
        throw new Error('登录状态已失效')
    }

    let payload = null
    try {
        payload = await response.json()
    } catch (error) {
        payload = null
    }

    if (payload && payload.code !== undefined) {
        if (payload.code === 200) {
            return payload.data
        }
        if (payload.code === 401) {
            if (skipAuth) {
                ElMessage.error(payload.message || '账号或密码错误')
            } else {
                await handleUnauthorized()
            }
            throw new Error(payload.message || '登录状态已失效')
        }
        ElMessage.error(payload.message || '请求失败')
        throw new Error(payload.message || '请求失败')
    }

    if (response.status === 401) {
        if (skipAuth) {
            ElMessage.error('账号或密码错误')
            throw new Error('账号或密码错误')
        }
        await handleUnauthorized()
        throw new Error('登录状态已失效')
    }

    if (!response.ok) {
        ElMessage.error('请求失败，请稍后重试')
        throw new Error(`请求失败：${response.status}`)
    }

    return payload
}
