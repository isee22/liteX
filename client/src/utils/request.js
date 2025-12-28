export const BASE_URL = 'http://localhost:8080/api'
export const SERVER_URL = 'http://localhost:8080'

export const request = (options) => {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('token')
    
    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data,
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : ''
      },
      success: (res) => {
        const data = res.data
        // 统一判断业务码
        if (data.code === 0) {
          resolve(data)
        } else {
          // 未登录
          if (data.code === 401) {
            uni.removeStorageSync('token')
            uni.navigateTo({ url: '/pages/login/index' })
          }
          console.error('请求失败:', data)
          uni.showToast({ title: data.msg || '请求失败', icon: 'none', duration: 2000 })
          reject(data)
        }
      },
      fail: (err) => {
        uni.showToast({ title: '网络错误', icon: 'none' })
        reject(err)
      }
    })
  })
}

export const get = (url, data) => request({ url, method: 'GET', data })
export const post = (url, data) => request({ url, method: 'POST', data })
export const put = (url, data) => request({ url, method: 'PUT', data })
export const del = (url, data) => request({ url, method: 'DELETE', data })
