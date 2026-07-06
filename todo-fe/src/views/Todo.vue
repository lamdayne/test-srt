<template>
    <div class="max-w-md mx-auto px-6 py-12 text-neutral-800">
        <header class="mb-8">
            <h1 class="text-xl font-bold tracking-tight text-neutral-900">Todo</h1>
            <div class="text-xs text-neutral-500 mt-2 flex gap-3">
                <span>Tổng: <strong>{{ store.stats.total }}</strong></span>
                <span>Đang làm: <strong>{{ store.stats.active }}</strong></span>
                <span>Hoàn thành: <strong>{{ store.stats.completed }}</strong></span>
            </div>
        </header>

        <div class="space-y-4 mb-6">
            <form @submit.prevent="handleAddTask" class="flex gap-2">
                <input type="text" v-model="newTaskTitle" placeholder="Thêm việc cần làm..."
                    class="flex-1 bg-white border border-neutral-200 rounded px-3 py-2 text-sm focus:outline-none focus:border-neutral-800 transition-colors"
                    :class="{ 'border-red-400': isInputInvalid }" @input="isInputInvalid = false" />
                <button type="submit" :disabled="store.loading"
                    class="bg-neutral-900 hover:bg-neutral-800 text-white text-sm font-medium px-4 py-2 rounded transition-colors disabled:opacity-50 cursor-pointer">
                    Thêm
                </button>
            </form>

            <div>
                <input type="text" v-model="searchInput" @input="handleSearch" placeholder="Tìm kiếm công việc..."
                    class="w-full bg-white border border-neutral-200 rounded px-3 py-2 text-sm focus:outline-none focus:border-neutral-800" />
            </div>
        </div>

        <div class="mb-6 flex gap-1 text-xs text-neutral-500">
            <button @click="setFilterStatus(null)"
                class="px-2 py-1 rounded hover:text-neutral-900 transition-colors cursor-pointer"
                :class="store.filters.completed === null ? 'bg-neutral-200 text-neutral-900 font-medium' : ''">
                Tất cả
            </button>
            <span class="text-neutral-300">/</span>
            <button @click="setFilterStatus(false)"
                class="px-2 py-1 rounded hover:text-neutral-900 transition-colors cursor-pointer"
                :class="store.filters.completed === false ? 'bg-neutral-200 text-neutral-900 font-medium' : ''">
                Đang làm
            </button>
            <span class="text-neutral-300">/</span>
            <button @click="setFilterStatus(true)"
                class="px-2 py-1 rounded hover:text-neutral-900 transition-colors cursor-pointer"
                :class="store.filters.completed === true ? 'bg-neutral-200 text-neutral-900 font-medium' : ''">
                Hoàn thành
            </button>
        </div>

        <section class="min-h-37.5">
            <div v-if="store.loading && store.todos.length === 0" class="py-6 text-center text-xs text-neutral-450">
                Đang tải...
            </div>

            <div v-else-if="store.todos.length === 0"
                class="py-8 text-center text-xs text-neutral-400 border border-dashed border-neutral-200 rounded">
                Trống.
            </div>

            <div v-else class="divide-y divide-neutral-100">
                <div v-for="todo in store.todos" :key="todo.id" class="py-3.5 flex items-center justify-between gap-4">
                    <div class="flex items-center gap-3.5 min-w-0">
                        <button @click="toggleTodoStatus(todo)"
                            class="w-4 h-4 rounded-full border border-neutral-300 flex items-center justify-center transition-colors cursor-pointer shrink-0"
                            :class="todo.completed ? 'bg-neutral-800 border-neutral-800 text-white' : 'hover:border-neutral-800 bg-white'">
                            <svg xmlns="http://www.w3.org/2000/svg" class="w-2.5 h-2.5" viewBox="0 0 20 20"
                                fill="currentColor" v-if="todo.completed">
                                <path fill-rule="evenodd"
                                    d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                                    clip-rule="evenodd" />
                            </svg>
                        </button>

                        <span class="text-sm text-neutral-850 truncate cursor-pointer"
                            :class="{ 'line-through text-neutral-400': todo.completed }"
                            @dblclick="openEditModal(todo)">
                            {{ todo.title }}
                        </span>
                    </div>

                    <div class="flex items-center gap-2.5 text-xs shrink-0">
                        <button @click="openEditModal(todo)"
                            class="text-neutral-400 hover:text-neutral-800 transition-colors cursor-pointer">
                            Sửa
                        </button>
                        <span class="text-neutral-200">|</span>
                        <button @click="handleDeleteTodo(todo.id)"
                            class="text-neutral-400 hover:text-red-600 transition-colors cursor-pointer">
                            Xóa
                        </button>
                    </div>
                </div>
            </div>
        </section>

        <div class="fixed bottom-6 right-6 z-50 flex flex-col gap-2 max-w-xs w-full pointer-events-none">
            <TransitionGroup name="toast">
                <div v-for="toast in toasts" :key="toast.id"
                    class="pointer-events-auto p-3.5 rounded bg-neutral-900 text-white text-xs shadow-md flex items-center justify-between gap-3">
                    <span>{{ toast.message }}</span>
                    <button @click="dismissToast(toast.id)" class="text-neutral-400 hover:text-white cursor-pointer">
                        ✕
                    </button>
                </div>
            </TransitionGroup>
        </div>

        <Teleport to="body">
            <Transition name="modal">
                <div v-if="showEditModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
                    <div class="absolute inset-0 bg-neutral-950/20" @click="closeEditModal"></div>

                    <div
                        class="relative bg-white border border-neutral-200 w-full max-w-xs rounded p-6 shadow-md z-10 text-neutral-800">
                        <h3 class="text-sm font-bold text-neutral-900 border-b border-neutral-100 pb-3">Chỉnh sửa</h3>

                        <form @submit.prevent="handleUpdateTodo" class="mt-4 space-y-4">
                            <div>
                                <input type="text" v-model="editTitle"
                                    class="w-full bg-white border border-neutral-200 rounded px-3 py-2 text-sm focus:outline-none focus:border-neutral-800"
                                    :class="{ 'border-red-400': isEditInputInvalid }"
                                    @input="isEditInputInvalid = false" />
                            </div>

                            <div class="flex items-center justify-between pt-1">
                                <span class="text-xs text-neutral-500">Hoàn thành</span>
                                <input type="checkbox" v-model="editCompleted"
                                    class="w-4 h-4 accent-neutral-800 cursor-pointer" />
                            </div>

                            <div class="flex justify-end gap-2 pt-4 border-t border-neutral-100 mt-4">
                                <button type="button" @click="closeEditModal"
                                    class="px-3.5 py-1.5 border border-neutral-200 hover:bg-neutral-50 rounded text-xs font-medium cursor-pointer">
                                    Hủy
                                </button>
                                <button type="submit"
                                    class="px-4 py-1.5 bg-neutral-900 hover:bg-neutral-800 text-white rounded text-xs font-medium cursor-pointer">
                                    Lưu
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </Transition>
        </Teleport>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useTodoStore } from '@/stores/todoStore'

const store = useTodoStore()

const newTaskTitle = ref('')
const isInputInvalid = ref(false)
const searchInput = ref('')
let searchDebounceTimer = null

const showEditModal = ref(false)
const editingTodoId = ref(null)
const editTitle = ref('')
const editCompleted = ref(false)
const isEditInputInvalid = ref(false)

const toasts = ref([])
let toastIdCounter = 0

const showToast = (message) => {
    const id = toastIdCounter++
    toasts.value.push({ id, message })
    setTimeout(() => {
        dismissToast(id)
    }, 3000)
}

const dismissToast = (id) => {
    toasts.value = toasts.value.filter(t => t.id !== id)
}

onMounted(() => {
    store.fetchTodos()
})

const handleAddTask = async () => {
    const trimmed = newTaskTitle.value.trim()
    if (!trimmed) {
        isInputInvalid.value = true
        showToast('Tiêu đề trống')
        return
    }

    const result = await store.createTodo(trimmed)
    if (result.success) {
        newTaskTitle.value = ''
        showToast('Đã thêm')
    } else {
        showToast(result.message)
    }
}

const toggleTodoStatus = async (todo) => {
    const result = await store.updateTodo(todo.id, todo.title, !todo.completed)
    if (result.success) {
        showToast(todo.completed ? 'Chưa hoàn thành' : 'Đã hoàn thành')
    } else {
        showToast(result.message)
    }
}

const handleDeleteTodo = async (id) => {
    const result = await store.deleteTodo(id)
    if (result.success) {
        showToast('Đã xóa')
    } else {
        showToast(result.message)
    }
}

const openEditModal = (todo) => {
    editingTodoId.value = todo.id
    editTitle.value = todo.title
    editCompleted.value = todo.completed
    isEditInputInvalid.value = false
    showEditModal.value = true
}

const closeEditModal = () => {
    showEditModal.value = false
    editingTodoId.value = null
    editTitle.value = ''
    editCompleted.value = false
    isEditInputInvalid.value = false
}

const handleUpdateTodo = async () => {
    const trimmed = editTitle.value.trim()
    if (!trimmed) {
        isEditInputInvalid.value = true
        showToast('Tiêu đề trống')
        return
    }

    const result = await store.updateTodo(editingTodoId.value, trimmed, editCompleted.value)
    if (result.success) {
        showToast('Đã cập nhật')
        closeEditModal()
    } else {
        showToast(result.message)
    }
}

const handleSearch = () => {
    clearTimeout(searchDebounceTimer)
    searchDebounceTimer = setTimeout(() => {
        store.filters.search = searchInput.value
        store.filters.page = 0
        store.fetchTodos()
    }, 350)
}

const setFilterStatus = (completedStatus) => {
    store.filters.completed = completedStatus
    store.filters.page = 0
    store.fetchTodos()
}
</script>

<style scoped>
.toast-enter-active,
.toast-leave-active {
    transition: all 0.2s ease;
}

.toast-enter-from {
    opacity: 0;
    transform: translateY(10px);
}

.toast-leave-to {
    opacity: 0;
}

.modal-enter-active,
.modal-leave-active {
    transition: opacity 0.15s ease;
}

.modal-enter-from,
.modal-leave-to {
    opacity: 0;
}
</style>