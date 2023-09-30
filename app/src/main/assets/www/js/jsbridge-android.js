(function () {
    if (window.jsBridge) {
        return;
    }

    if (!window._androidNativeBridge) {
        return;
    }
    let nativeBridge = window._androidNativeBridge;

    let callIdSeed = 0;
    let callRegistry = {};

    window.jsBridge = {
        platform: 'android',
        callService(name, params) {
            return new Promise((resolve, reject) => {
                let callId = callIdSeed++;
                callRegistry[callId] = { resolve, reject };
                nativeBridge.callService(callId, name, params);
            });
        },
    };

    window._androidCallbackBridge = {
        resolve(callId, result) {
            callRegistry[callId].resolve(result);
            delete callRegistry[callId];
        },
        reject(callId, error) {
            callRegistry[callId].reject(error);
            delete callRegistry[callId];
        },
    };
})();
