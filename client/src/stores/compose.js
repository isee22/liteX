import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useComposeStore = defineStore('compose', () => {
  const visible = ref(false)
  
  const open = () => { visible.value = true }
  const close = () => { visible.value = false }
  
  return { visible, open, close }
})
