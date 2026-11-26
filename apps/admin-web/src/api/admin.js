import request from './request'

export function login(data) {
    return request('/admin/auth/login', { method: 'POST', data, skipAuth: true })
}

export function resetPassword(data) {
    return request('/admin/auth/reset-password', { method: 'POST', data, skipAuth: true })
}

export function getInfo() {
    return request('/admin/auth/info')
}

export function getDashboardSummary() {
    return request('/admin/dashboard/summary')
}

export function getAdmins(params) {
    return request('/admin/admins', { params })
}

export function saveAdmin(data) {
    return request('/admin/admins', { method: 'POST', data })
}

export function updateAdminStatus(id, status) {
    return request(`/admin/admins/${id}/status`, { method: 'PUT', params: { status } })
}

export function deleteAdmin(id) {
    return request(`/admin/admins/${id}`, { method: 'DELETE' })
}

export function getRoles() {
    return request('/admin/roles')
}

export function saveRole(data) {
    return request('/admin/roles', { method: 'POST', data })
}

export function getRoleMenus(id) {
    return request(`/admin/roles/${id}/menus`)
}

export function saveRoleMenus(id, menuIds) {
    return request(`/admin/roles/${id}/menus`, { method: 'PUT', data: menuIds })
}

export function getMenuTree() {
    return request('/admin/menus/tree')
}

export function getLogs(params) {
    return request('/admin/logs', { params })
}
