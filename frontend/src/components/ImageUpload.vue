<template>
  <div class="image-upload">
    <el-upload
      :action="uploadUrl"
      :headers="headers"
      :show-file-list="false"
      :before-upload="beforeUpload"
      :on-success="handleSuccess"
      :on-error="handleError"
      accept="image/*"
    >
      <div v-if="modelValue" class="image-preview">
        <img :src="modelValue" alt="预览" />
        <div class="image-overlay">
          <el-icon :size="20"><Upload /></el-icon>
          <span>更换图片</span>
        </div>
      </div>
      <div v-else class="upload-placeholder">
        <el-icon :size="28"><Plus /></el-icon>
        <span>上传图片</span>
      </div>
    </el-upload>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Upload, Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const props = defineProps<{
  modelValue: string
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
}>()

const userStore = useUserStore()
const uploadUrl = '/api/v1/files/upload'
const headers = computed(() => ({
  Authorization: `Bearer ${userStore.token}`
}))

function beforeUpload(file: File) {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

function handleSuccess(response: any) {
  if (response.code === 200) {
    emit('update:modelValue', response.data.minioPath || response.data.attachId)
    ElMessage.success('上传成功')
  }
}

function handleError() {
  ElMessage.error('上传失败')
}
</script>

<style lang="scss" scoped>
.image-upload {
  :deep(.el-upload) {
    border: 2px dashed var(--border-color);
    border-radius: var(--radius-md);
    cursor: pointer;
    transition: all 0.3s;
    overflow: hidden;

    &:hover {
      border-color: var(--primary);
    }
  }

  .upload-placeholder {
    width: 120px;
    height: 120px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 6px;
    color: var(--text-muted);
    font-size: 13px;
  }

  .image-preview {
    width: 120px;
    height: 120px;
    position: relative;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .image-overlay {
      position: absolute;
      inset: 0;
      background: rgba(0,0,0,0.5);
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: 4px;
      color: #fff;
      font-size: 12px;
      opacity: 0;
      transition: opacity 0.3s;
    }

    &:hover .image-overlay {
      opacity: 1;
    }
  }
}
</style>
