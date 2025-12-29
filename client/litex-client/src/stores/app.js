import { defineStore } from 'pinia'
import { ref } from 'vue'
import { get } from '@/utils/request'

export const useAppStore = defineStore('app', () => {
  const trends = ref([])
  const recommendUsers = ref([])
  const trendsLoaded = ref(false)
  const recommendLoaded = ref(false)

  const fetchTrends = async (force = false) => {
    if (trendsLoaded.value && !force) return
    try {
      const res = await get('/trends')
      trends.value = (res.data || []).slice(0, 4)
      trendsLoaded.value = true
    } catch (e) {}
  }

  const fetchRecommend = async (force = false) => {
    if (recommendLoaded.value && !force) return
    try {
      const res = await get('/user/recommend')
      recommendUsers.value = (res.data || []).slice(0, 3)
      recommendLoaded.value = true
    } catch (e) {}
  }

  const removeRecommendUser = (id) => {
    recommendUsers.value = recommendUsers.value.filter(item => (item.user?.id || item.id) !== id)
  }

  return { 
    trends, recommendUsers, trendsLoaded, recommendLoaded,
    fetchTrends, fetchRecommend, removeRecommendUser
  }
})
