const { createApp } = Vue

createApp({
    data() {
        return {}
    },
    methods: {
        callService1() {
            jsBridge.callService('service_1', 'params_1')
                .then((result) => {
                    console.log('service_1: ', result)
                })
                .catch((error) => {
                    console.error('service_1: ', error)
                })
        },
        callService2() {
            jsBridge.callService('service_2', 'params_2')
                .then((result) => {
                    console.log('service_2: ', result)
                })
                .catch((error) => {
                    console.error('service_2: ', error)
                })
        },
        callService3() {
            jsBridge.callService('service_3')
                .then((result) => {
                    console.log('service_3: ', result)
                })
                .catch((error) => {
                    console.error('service_3: ', error)
                })
        },
    },
}).mount('#app')
