import { defineStore } from 'pinia'
import { ref } from 'vue'
import { get, post } from '@/utils/request'

export const useNotificationStore = defineStore('notification', () => {
  const list = ref([])
  const unreadCount = ref(0)
  const loading = ref(false)

  const fetchList = async () => {
    loading.value = true
    try {
      const res = await get('/notifications')
      list.value = res.data || []
    } finally {
      loading.value = false
    }
  }

  const fetchUnreadCount = async () => {
    const res = await get('/notifications/unread')
    unreadCount.value = res.data || 0
  }

  const markAsRead = async (id) => {
    await post(`/notifications/${id}/read`)
    const item = list.value.find(n => n.id === id)
    if (item) item.isread = true
    unreadCount.value = Math.max(0, unreadCount.value - 1)
  }

  const markAllAsRead = async () => {
    await post('/notifications/read')
    list.value.forEach(n => n.isread = true)
    unreadCount.value = 0
  }

  return { list, unreadCount, loading, fetchList, fetchUnreadCount, markAsRead, markAllAsRead }
})
