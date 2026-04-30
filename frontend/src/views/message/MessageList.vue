<template>
  <div class="page-container">
    <div class="page-header">
      <h2>消息中心</h2>
      <div class="header-actions">
        <el-select v-model="filters.type" placeholder="消息类型" clearable style="width: 140px" @change="loadMessages">
          <el-option label="系统通知" value="system" />
          <el-option label="审核通知" value="audit" />
          <el-option label="评审通知" value="review" />
          <el-option label="里程碑" value="milestone" />
          <el-option label="成果通知" value="achievement" />
        </el-select>
        <el-select v-model="filters.isRead" placeholder="读取状态" clearable style="width: 120px" @change="loadMessages">
          <el-option label="未读" :value="0" />
          <el-option label="已读" :value="1" />
        </el-select>
        <el-button @click="handleMarkAllRead" :disabled="unreadCount === 0">全部已读</el-button>
      </div>
    </div>

    <div class="card">
      <div v-loading="loading" class="message-list">
        <el-empty v-if="messages.length === 0" description="暂无消息" />

        <div
          v-for="msg in messages"
          :key="msg.messageId"
          class="message-item"
          :class="{ unread: msg.isRead === 0 }"
          @click="handleRead(msg)"
        >
          <div class="msg-icon">
            <el-icon :size="20" :color="typeColor(msg.type)">
              <component :is="typeIcon(msg.type)" />
            </el-icon>
          </div>
          <div class="msg-body">
            <div class="msg-header">
              <span class="msg-title">{{ msg.title }}</span>
              <el-tag size="small" :type="typeTagType(msg.type)" effect="plain">{{ typeLabel(msg.type) }}</el-tag>
            </div>
            <div class="msg-content">{{ msg.content }}</div>
            <div class="msg-footer">
              <span class="msg-time">{{ msg.createTime }}</span>
            </div>
          </div>
          <div class="msg-actions" @click.stop>
            <el-button v-if="msg.isRead === 0" link type="primary" size="small" @click="handleRead(msg)">标为已读</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(msg)">删除</el-button>
          </div>
        </div>
      </div>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="loadMessages"
          @current-change="loadMessages"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Bell, Edit, Flag, Trophy, InfoFilled, Promotion } from '@element-plus/icons-vue'
import { getMessages, getUnreadCount, markRead, markAllRead, deleteMessage } from '@/api/message'

const loading = ref(false)
const messages = ref<any[]>([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const unreadCount = ref(0)

const filters = reactive({ type: '', isRead: undefined as number | undefined })

async function loadMessages() {
  loading.value = true
  try {
    const params: any = { page: page.value, size: size.value }
    if (filters.type) params.type = filters.type
    if (filters.isRead !== undefined) params.isRead = filters.isRead
    const res: any = await getMessages(params)
    messages.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch {} finally { loading.value = false }
}

async function loadUnreadCount() {
  try {
    const res: any = await getUnreadCount()
    unreadCount.value = res.data || 0
  } catch {}
}

async function handleRead(msg: any) {
  if (msg.isRead === 0) {
    try {
      await markRead(msg.messageId)
      msg.isRead = 1
      unreadCount.value = Math.max(0, unreadCount.value - 1)
    } catch {}
  }
  // 如果有关联项目，可以跳转到项目详情
  if (msg.relationId) {
    // router.push(`/projects/${msg.relationId}`)
  }
}

async function handleMarkAllRead() {
  try {
    await ElMessageBox.confirm('确认将所有消息标为已读？', '提示', { type: 'info' })
    await markAllRead()
    ElMessage.success('已全部标为已读')
    loadMessages()
    loadUnreadCount()
  } catch {}
}

async function handleDelete(msg: any) {
  try {
    await ElMessageBox.confirm('确认删除该消息？', '提示', { type: 'warning' })
    await deleteMessage(msg.messageId)
    ElMessage.success('已删除')
    loadMessages()
    loadUnreadCount()
  } catch {}
}

function typeIcon(type: string) {
  const map: Record<string, any> = { system: Bell, audit: Edit, review: Promotion, milestone: Flag, achievement: Trophy }
  return map[type] || InfoFilled
}

function typeColor(type: string) {
  const map: Record<string, string> = { system: '#909399', audit: '#E6A23C', review: '#409EFF', milestone: '#67C23A', achievement: '#F56C6C' }
  return map[type] || '#909399'
}

function typeLabel(type: string) {
  const map: Record<string, string> = { system: '系统', audit: '审核', review: '评审', milestone: '里程碑', achievement: '成果' }
  return map[type] || type
}

function typeTagType(type: string): '' | 'success' | 'warning' | 'danger' | 'info' | 'primary' {
  const map: Record<string, '' | 'success' | 'warning' | 'danger' | 'info' | 'primary'> = { system: 'info', audit: 'warning', review: '', milestone: 'success', achievement: 'danger' }
  return map[type] || 'info'
}

onMounted(() => {
  loadMessages()
  loadUnreadCount()
})
</script>

<style lang="scss" scoped>
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;

  h2 { margin: 0; }

  .header-actions {
    display: flex;
    align-items: center;
    gap: 8px;
  }
}

.message-list {
  min-height: 400px;
}

.message-item {
  display: flex;
  align-items: flex-start;
  padding: 16px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  cursor: pointer;
  transition: background 0.2s;

  &:hover {
    background: var(--el-fill-color-light);
  }

  &.unread {
    background: #ecf5ff;

    .msg-title { font-weight: 600; }

    &::before {
      content: '';
      width: 8px;
      height: 8px;
      background: #409eff;
      border-radius: 50%;
      position: absolute;
      left: 4px;
      margin-top: 6px;
    }

    position: relative;
    padding-left: 20px;
  }

  .msg-icon {
    flex-shrink: 0;
    width: 40px;
    height: 40px;
    border-radius: 8px;
    background: var(--el-fill-color);
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 12px;
  }

  .msg-body {
    flex: 1;
    min-width: 0;

    .msg-header {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 4px;

      .msg-title {
        font-size: 14px;
        color: var(--el-text-color-primary);
      }
    }

    .msg-content {
      font-size: 13px;
      color: var(--el-text-color-regular);
      line-height: 1.6;
      margin-bottom: 4px;
    }

    .msg-footer {
      .msg-time {
        font-size: 12px;
        color: var(--el-text-color-placeholder);
      }
    }
  }

  .msg-actions {
    flex-shrink: 0;
    margin-left: 12px;
    display: flex;
    gap: 4px;
  }
}

.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
