import { defineStore } from 'pinia'
import { ref } from 'vue'
import { get, post } from '@/utils/request'

export const useMessageStore = defineStore('message', () => {
  const conversations = ref([])
  const currentChat = ref([])
  const unreadCount = ref(0)
  const loading = ref(false)

  const fetchConversations = async () => {
    loading.value = true
    try {
      const res = await get('/messages')
      conversations.value = res.data || []
    } finally {
      loading.value = false
    }
  }

  const fetchUnreadCount = async () => {
    const res = await get('/messages/unread')
    unreadCount.value = res.data || 0
  }

  const fetchChat = async (userid) => {
    const res = await get(`/messages/${userid}`)
    currentChat.value = res.data || []
  }

  const sendMessage = async (touserid, content) => {
    const res = await post('/messages', { touserid, content })
    currentChat.value.push(res.data)
    return res.data
  }

  return { conversations, currentChat, unreadCount, loading, fetchConversations, fetchUnreadCount, fetchChat, sendMessage }
})
