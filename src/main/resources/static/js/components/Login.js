/**
 * 登錄組件
 */
console.log('Login.js 模塊加載中...');

// 使用絕對路徑導入
import { sendCode, login } from '/js/api/userApi.js';

class Login {
  constructor() {
    console.log('Login 類初始化');
    this.phoneInput = document.getElementById('phone');
    this.codeInput = document.getElementById('code');
    this.sendCodeBtn = document.getElementById('sendCodeBtn');
    this.loginBtn = document.getElementById('loginBtn');
    this.countdown = 60;
    this.timer = null;
    
    // 檢查元素是否存在
    if (!this.phoneInput) console.error('手機號輸入框不存在');
    if (!this.codeInput) console.error('驗證碼輸入框不存在');
    if (!this.sendCodeBtn) console.error('發送驗證碼按鈕不存在');
    if (!this.loginBtn) console.error('登錄按鈕不存在');
    
    this.init();
  }
  
  init() {
    console.log('初始化事件監聽器');
    // 綁定事件
    if (this.sendCodeBtn) {
      this.sendCodeBtn.addEventListener('click', this.handleSendCode.bind(this));
      console.log('發送驗證碼按鈕事件已綁定');
    }
    
    if (this.loginBtn) {
      this.loginBtn.addEventListener('click', this.handleLogin.bind(this));
      console.log('登錄按鈕事件已綁定');
    }
  }
  
  // 處理發送驗證碼
  async handleSendCode() {
    console.log('處理發送驗證碼');
    const phone = this.phoneInput.value.trim();
    console.log('手機號:', phone);
    
    // 驗證手機號
    if (!this.validatePhone(phone)) {
      console.log('手機號格式不正確');
      alert('請輸入正確的手機號碼');
      return;
    }
    
    try {
      console.log('準備發送驗證碼請求');
      // 禁用按鈕
      this.sendCodeBtn.disabled = true;
      
      // 發送驗證碼
      console.log('調用 sendCode API');
      const result = await sendCode(phone);
      console.log('API 響應:', result);
      
      if (result.code === 1) {
        // 開始倒計時
        this.startCountdown();
        alert('驗證碼已發送，請注意查收');
      } else {
        alert(result.msg || '發送驗證碼失敗');
        this.sendCodeBtn.disabled = false;
      }
    } catch (error) {
      console.error('發送驗證碼錯誤:', error);
      alert('發送驗證碼失敗，請稍後再試');
      this.sendCodeBtn.disabled = false;
    }
  }
  
  // 處理登錄
  async handleLogin() {
    console.log('處理登錄');
    const phone = this.phoneInput.value.trim();
    const code = this.codeInput.value.trim();
    console.log('手機號:', phone, '驗證碼:', code);
    
    // 驗證輸入
    if (!this.validatePhone(phone)) {
      console.log('手機號格式不正確');
      alert('請輸入正確的手機號碼');
      return;
    }
    
    if (!code) {
      console.log('驗證碼為空');
      alert('請輸入驗證碼');
      return;
    }
    
    try {
      console.log('準備發送登錄請求');
      // 禁用登錄按鈕
      this.loginBtn.disabled = true;
      
      // 發送登錄請求
      console.log('調用 login API');
      const result = await login({
        phone,
        code
      });
      console.log('API 響應:', result);
      
      if (result.code === 1) {
        // 登錄成功
        alert('登錄成功');
        // 存儲token
        localStorage.setItem('token', result.data);
        // 跳轉到首頁
        window.location.href = '/';
      } else {
        alert(result.msg || '登錄失敗');
        this.loginBtn.disabled = false;
      }
    } catch (error) {
      console.error('登錄錯誤:', error);
      alert('登錄失敗，請稍後再試');
      this.loginBtn.disabled = false;
    }
  }
  
  // 開始倒計時
  startCountdown() {
    console.log('開始倒計時');
    this.countdown = 60;
    this.sendCodeBtn.textContent = `${this.countdown}秒後重新發送`;
    
    this.timer = setInterval(() => {
      this.countdown--;
      this.sendCodeBtn.textContent = `${this.countdown}秒後重新發送`;
      
      if (this.countdown <= 0) {
        clearInterval(this.timer);
        this.sendCodeBtn.disabled = false;
        this.sendCodeBtn.textContent = '發送驗證碼';
      }
    }, 1000);
  }
  
  // 驗證手機號
  validatePhone(phone) {
    const phoneRegex = /^1[3-9]\d{9}$/;
    return phoneRegex.test(phone);
  }
}

// 初始化登錄組件
console.log('等待 DOM 加載完成');
document.addEventListener('DOMContentLoaded', () => {
  console.log('DOM 加載完成，初始化 Login 組件');
  try {
    new Login();
    console.log('Login 組件初始化成功');
  } catch (error) {
    console.error('Login 組件初始化失敗:', error);
  }
}); 