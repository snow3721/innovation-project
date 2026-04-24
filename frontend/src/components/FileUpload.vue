<template>
  <div class="file-upload">
    <el-upload
      :action="uploadUrl"
      :headers="headers"
      :file-list="fileList"
      :on-success="handleSuccess"
      :on-remove="handleRemove"
      :before-upload="beforeUpload"
      :limit="limit"
      :on-exceed="handleExceed"
      :data="{ type: fileType, relationId: relationId }"
    >
      <el-button type="primary" plain>
        <el-icon><Upload /></el-icon>
        点击上传
      </el-button>
      <template #tip>
        <div class="upload-tip">
          支持 {{ acceptText }} 格式，单文件不超过 {{ maxSize }}MB
        </div>
      </template>
    </el-upload>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Upload } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import type { UploadFile } from 'element-plus'

const props = withDefaults(defineProps<{
  fileType?: string
  relationId?: number
  limit?: number
  maxSize?: number
  accept?: string
}>(), {
  fileType: 'apply',
  relationId: 0,
  limit: 5,
  maxSize: 10,
  accept: '.pdf,.doc,.docx'
})

const emit = defineEmits<{
  (e: 'success', fileIds: number[]): void
}>()

const userStore = useUserStore()
const uploadUrl = '/api/v1/files/upload'
const headers = computed(() => ({
  Authorization: `Bearer ${userStore.token}`
}))

const fileList = ref<UploadFile[]>([])
const uploadedIds = ref<number[]>([])

const acceptText = computed(() => props.accept.replace(/\./g, '').toUpperCase())

function beforeUpload(file: File) {
  const isLt = file.size / 1024 / 1024 < props.maxSize
  if (!isLt) {
    ElMessage.error(`文件大小不能超过 ${props.maxSize}MB!`)
    return false
  }
  return true
}

function handleSuccess(response: any) {
  if (response.code === 200) {
    uploadedIds.value.push(response.data.attachId)
    emit('success', uploadedIds.value)
  }
}

function handleRemove() {
  uploadedIds.value.pop()
  emit('success', uploadedIds.value)
}

function handleExceed() {
  ElMessage.warning(`最多上传 ${props.limit} 个文件`)
}
</script>

<style lang="scss" scoped>
.file-upload {
  .upload-tip {
    font-size: 12px;
    color: var(--text-muted);
    margin-top: 4px;
  }
}
</style>
