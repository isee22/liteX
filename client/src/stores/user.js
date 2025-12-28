import { defineStore } from 'pinia'
import { ref } from 'vue'
import { post, get } from '@/utils/request'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref(null)
  const isLoggedIn = ref(false)
  const showLoginModal = ref(false)

  const login = async (username, password) => {
    const res = await post('/auth/login', { username, password })
    uni.setStorageSync('token', res.data.token)
    userInfo.value = res.data.user
    isLoggedIn.value = true
    return res
  }

  const register = async (data) => {
    const res = await post('/auth/register', data)
    uni.setStorageSync('token', res.data.token)
    userInfo.value = res.data.user
    isLoggedIn.value = true
    return res
  }

  const logout = () => {
    uni.removeStorageSync('token')
    userInfo.value = null
    isLoggedIn.value = false
  }

  const fetchUserInfo = async () => {
    const res = await get('/user/me')
    userInfo.value = res.data
    isLoggedIn.value = true
  }

  const checkLogin = () => {
    const token = uni.getStorageSync('token')
    if (token) {
      fetchUserInfo()
    }
  }

  const openLoginModal = () => { showLoginModal.value = true }
  const hideLoginModal = () => { showLoginModal.value = false }
  
  // 需要登录时调用，未登录则弹出登录框
  const requireLogin = () => {
    if (!isLoggedIn.value) {
      showLoginModal.value = true
      return false
    }
    return true
  }

  return { 
    userInfo, isLoggedIn, showLoginModal,
    login, register, logout, fetchUserInfo, checkLogin,
    openLoginModal, hideLoginModal, requireLogin
  }
})
