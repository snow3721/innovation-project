<template>
  <div class="filter-table">
    <el-table
      :data="data"
      stripe
      :header-cell-style="{ background: '#f8fafc', color: '#4b5563', fontWeight: 600 }"
      @sort-change="handleSortChange"
      @filter-change="handleFilterChange"
      v-loading="loading"
    >
      <slot />
    </el-table>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  data: any[]
  loading?: boolean
}>()

const emit = defineEmits<{
  (e: 'sort-change', prop: string, order: string): void
  (e: 'filter-change', filters: any): void
}>()

function handleSortChange({ prop, order }: any) {
  emit('sort-change', prop, order)
}

function handleFilterChange(filters: any) {
  emit('filter-change', filters)
}
</script>

<style lang="scss" scoped>
.filter-table {
  :deep(.el-table__header-wrapper) {
    th {
      .cell {
        display: flex;
        align-items: center;
        gap: 4px;
      }
    }
  }
}
</style>
