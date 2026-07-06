import axios from "axios";

const axiosInstance = axios.create({
    baseURL: import.meta.env.VITE_API_URL,
    timeout: 50000,
    headers: {
        'Content-Type': 'application/json'
    }
})

axiosInstance.interceptors.request.use(
    (config) => {
        return config
    },
    (error) => {
        console.log([error])
        return Promise.reject(error)
    }
)

axiosInstance.interceptors.response.use(
    (config) => {
        return config
    },
    (error) => {
        console.log([error])
        return Promise.reject(error)
    }
)

export default axiosInstance
