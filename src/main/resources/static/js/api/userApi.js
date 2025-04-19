/**
 * 用戶相關API服務
 */
console.log('userApi.js 模塊加載中...');

// 基礎URL
const BASE_URL = '/user';
console.log('API 基礎URL:', BASE_URL);

// 獲取請求頭
function getHeaders() {
  const token = localStorage.getItem('token');
  const headers = {
    'Content-Type': 'application/json'
  };
  if (token) {
    headers['Authorization'] = token;
  }
  return headers;
}

/**
 * 發送手機驗證碼
 * @param {string} phone - 手機號碼
 * @returns {Promise} - 返回Promise對象
 */
export function sendCode(phone) {
  console.log('調用 sendCode 函數，手機號:', phone);
  const url = `${BASE_URL}/code?phone=${phone}`;
  console.log('請求URL:', url);
  
  return fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    }
  })
  .then(response => {
    console.log('收到響應:', response);
    return response.json();
  })
  .then(data => {
    console.log('解析後的數據:', data);
    return data;
  })
  .catch(error => {
    console.error('發送驗證碼請求失敗:', error);
    throw error;
  });
}

/**
 * 用戶登錄
 * @param {Object} loginData - 登錄數據
 * @param {string} loginData.phone - 手機號碼
 * @param {string} loginData.code - 驗證碼
 * @returns {Promise} - 返回Promise對象
 */
export function login(loginData) {
  console.log('調用 login 函數，登錄數據:', loginData);
  const url = `${BASE_URL}/login`;
  console.log('請求URL:', url);
  
  return fetch(url, {
    method: 'POST',
    headers: getHeaders(),
    body: JSON.stringify({
      loginFormDTO: loginData
    })
  })
  .then(response => {
    console.log('收到響應:', response);
    return response.json();
  })
  .then(data => {
    console.log('解析後的數據:', data);
    return data;
  })
  .catch(error => {
    console.error('登錄請求失敗:', error);
    throw error;
  });
}

/**
 * 用戶登出
 * @returns {Promise} - 返回Promise對象
 */
export function logout() {
  console.log('調用 logout 函數');
  const url = `${BASE_URL}/logout`;
  console.log('請求URL:', url);
  
  return fetch(url, {
    method: 'POST',
    headers: getHeaders()
  })
  .then(response => {
    console.log('收到響應:', response);
    return response.json();
  })
  .then(data => {
    console.log('解析後的數據:', data);
    return data;
  })
  .catch(error => {
    console.error('登出請求失敗:', error);
    throw error;
  });
}

/**
 * 獲取當前登錄用戶信息
 * @returns {Promise} - 返回Promise對象
 */
export function getCurrentUser() {
  console.log('調用 getCurrentUser 函數');
  const url = `${BASE_URL}/me`;
  console.log('請求URL:', url);
  
  return fetch(url, {
    method: 'GET',
    headers: getHeaders()
  })
  .then(response => {
    console.log('收到響應:', response);
    return response.json();
  })
  .then(data => {
    console.log('解析後的數據:', data);
    return data;
  })
  .catch(error => {
    console.error('獲取用戶信息請求失敗:', error);
    throw error;
  });
} 