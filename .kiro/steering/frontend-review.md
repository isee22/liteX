# 前端代码审查规则

## 修改前端代码时必须检查

### 1. 交互完整性检查

每次修改 Vue 组件时，必须检查：

- **所有 @click 事件**：是否绑定了处理函数，函数是否已定义
- **所有 @confirm/@input 事件**：是否有对应处理逻辑
- **v-for 循环**：key 是否正确，数据源是否初始化
- **v-if/v-show 条件**：条件变量是否正确更新

### 2. API 调用检查

- **请求路径**：是否与后端路由一致
- **请求方法**：GET/POST/PUT/DELETE 是否正确
- **参数格式**：query 参数用 `get(url, { params })`，body 用 `post(url, data)`
- **响应处理**：检查 `res.data` 还是 `res.data.list`，与后端返回格式匹配
- **错误处理**：try-catch 是否有 toast 提示

### 3. 页面参数获取

uni-app 页面参数必须用 `onLoad` 获取：
```javascript
import { onLoad } from '@dcloudio/uni-app'
onLoad((options) => {
  const id = options?.id
})
```

不要用 `onMounted` + `getCurrentPages().options`

### 4. 数据状态同步

- 点赞/关注等操作后，本地状态是否同步更新
- 列表数据的 liked/isfollowing 等状态是否从 API 正确读取
- 删除操作后是否从列表移除

### 5. 导航方式

- tabBar 页面用 `uni.switchTab`
- 普通页面用 `uni.navigateTo`
- 替换页面用 `uni.redirectTo`
- 返回用 `uni.navigateBack`

## 修改后端 API 时必须检查

### 1. 前端调用点

修改 API 返回格式后，搜索前端所有调用该 API 的地方，确保数据处理一致

### 2. 返回格式统一

列表数据统一返回：`{ list: [...] }` 或直接返回数组，不要混用

## 新增功能时

### 1. 完整实现

不要只写 UI，必须同时实现：
- 前端事件绑定
- API 调用
- 后端路由和处理逻辑
- 数据库操作（如需要）

### 2. 检查清单

新增页面或组件后，逐个检查：
- [ ] 所有按钮是否可点击
- [ ] 所有输入框是否有响应
- [ ] 所有列表是否正确渲染
- [ ] 空状态是否正确显示
- [ ] 加载状态是否正确显示
