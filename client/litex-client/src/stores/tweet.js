import { defineStore } from 'pinia'
import { ref } from 'vue'
import { get, post, del } from '@/utils/request'

export const useTweetStore = defineStore('tweet', () => {
  const tweets = ref([])
  const loading = ref(false)

  const fetchTimeline = async (page = 1, tab = 'foryou') => {
    loading.value = true
    try {
      const url = tab === 'following' ? '/tweets/following' : '/tweets/timeline'
      const res = await get(url, { page, size: 20 })
      if (page === 1) {
        tweets.value = res.data.list
      } else {
        tweets.value.push(...res.data.list)
      }
      return res.data
    } finally {
      loading.value = false
    }
  }

  const createTweet = async (content, images = []) => {
    const data = { content }
    if (images.length > 0) {
      data.images = images
    }
    const res = await post('/tweets', data)
    tweets.value.unshift(res.data)
    return res
  }

  const likeTweet = async (tweetId) => {
    await post(`/tweets/${tweetId}/like`)
    const tweet = tweets.value.find(t => t.id === tweetId)
    if (tweet) {
      tweet.liked = !tweet.liked
      tweet.likeCount += tweet.liked ? 1 : -1
    }
  }

  const retweet = async (tweetId) => {
    await post(`/tweets/${tweetId}/retweet`)
  }

  const deleteTweet = async (tweetId) => {
    await del(`/tweets/${tweetId}`)
    tweets.value = tweets.value.filter(t => t.id !== tweetId)
  }

  return { tweets, loading, fetchTimeline, createTweet, likeTweet, retweet, deleteTweet }
})
