import { ref } from 'vue'
import { defineStore } from 'pinia'
import axiosInstance from '@/axios/axios'

export const useTodoStore = defineStore('todo', () => {
  const todos = ref([])
  const pagination = ref({
    pageNo: 0,
    pageSize: 10,
    totalElements: 0,
    totalPages: 0
  })
  const stats = ref({
    total: 0,
    completed: 0,
    active: 0
  })
  const loading = ref(false)
  const error = ref(null)

  const filters = ref({
    completed: null,
    search: '',
    page: 0,
    size: 100,
    sortBy: 'createdAt,desc'
  })

  const fetchStats = async () => {
    try {
      const response = await axiosInstance.get('/todos/stats')
      const apiResponse = response.data
      if (apiResponse && apiResponse.code === 'TASK_READ_SUCCESS') {
        stats.value = apiResponse.data
      }
    } catch (err) {
      console.error(err)
    }
  }

  const fetchTodos = async () => {
    loading.value = true
    error.value = null
    try {
      const params = {
        page: filters.value.page,
        size: filters.value.size,
      }
      if (filters.value.completed !== null) {
        params.completed = filters.value.completed
      }
      if (filters.value.search.trim()) {
        params.search = filters.value.search.trim()
      }
      if (filters.value.sortBy) {
        params.sorts = filters.value.sortBy
      }

      const response = await axiosInstance.get('/todos', { params })
      const apiResponse = response.data
      if (apiResponse && apiResponse.code === 'TASK_READ_SUCCESS') {
        todos.value = apiResponse.data.items
        pagination.value = {
          pageNo: apiResponse.data.pageNo,
          pageSize: apiResponse.data.pageSize,
          totalElements: apiResponse.data.totalElements,
          totalPages: apiResponse.data.totalPages
        }
      } else {
        error.value = apiResponse.message || 'Lỗi tải công việc'
      }
      await fetchStats()
    } catch (err) {
      console.error(err)
      error.value = err.response?.data?.message || 'Lỗi kết nối'
    } finally {
      loading.value = false
    }
  }

  const createTodo = async (title) => {
    loading.value = true
    try {
      const response = await axiosInstance.post('/todos', { title })
      const apiResponse = response.data
      if (apiResponse && apiResponse.code === 'TASK_CREATE_SUCCESS') {
        filters.value.page = 0
        await fetchTodos()
        return { success: true }
      } else {
        return { success: false, message: apiResponse.message || 'Lỗi thêm công việc' }
      }
    } catch (err) {
      console.error(err)
      return { 
        success: false, 
        message: err.response?.data?.message || 'Lỗi không hợp lệ'
      }
    } finally {
      loading.value = false
    }
  }

  const updateTodo = async (id, title, completed) => {
    try {
      const response = await axiosInstance.put(`/todos/${id}`, { title, completed })
      const apiResponse = response.data
      if (apiResponse && apiResponse.code === 'TASK_UPDATE_SUCCESS') {
        const idx = todos.value.findIndex(t => t.id === id)
        if (idx !== -1) {
          todos.value[idx] = apiResponse.data
        }
        await fetchStats()
        return { success: true }
      } else {
        return { success: false, message: apiResponse.message || 'Lỗi cập nhật công việc' }
      }
    } catch (err) {
      console.error(err)
      return { 
        success: false, 
        message: err.response?.data?.message || 'Lỗi không hợp lệ'
      }
    }
  }

  const deleteTodo = async (id) => {
    try {
      const response = await axiosInstance.delete(`/todos/${id}`)
      const apiResponse = response.data
      if (apiResponse && apiResponse.code === 'TASK_DELETE_SUCCESS') {
        if (todos.value.length === 1 && filters.value.page > 0) {
          filters.value.page--
        }
        await fetchTodos()
        return { success: true }
      } else {
        return { success: false, message: apiResponse.message || 'Lỗi xóa công việc' }
      }
    } catch (err) {
      console.error(err)
      return { 
        success: false, 
        message: err.response?.data?.message || 'Lỗi không hợp lệ'
      }
    }
  }

  return {
    todos,
    pagination,
    stats,
    loading,
    error,
    filters,
    fetchTodos,
    fetchStats,
    createTodo,
    updateTodo,
    deleteTodo
  }
})
