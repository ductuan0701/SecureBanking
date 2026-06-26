const API_BASE = 'https://api.ductuan71.top/api';

const Auth = {
    getToken() {
        return localStorage.getItem('sb_token');
    },
    setToken(token) {
        localStorage.setItem('sb_token', token);
    },
    getUser() {
        const data = localStorage.getItem('sb_user');
        return data ? JSON.parse(data) : null;
    },
    setUser(user) {
        localStorage.setItem('sb_user', JSON.stringify(user));
    },
    isLoggedIn() {
        return !!this.getToken();
    },
    getRole() {
        const user = this.getUser();
        return user ? user.role : null;
    },
    clear() {
        localStorage.removeItem('sb_token');
        localStorage.removeItem('sb_user');
    },
    authHeaders() {
        return {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${this.getToken()}`
        };
    },
    async logout() {
        try {
            await fetch(`${API_BASE}/auth/logout`, {
                method: 'POST',
                headers: this.authHeaders()
            });
        } catch (e) {  }
        this.clear();
        window.location.href = 'index.html';
    },
    requireAuth() {
        if (!this.isLoggedIn()) {
            window.location.href = 'index.html';
            return false;
        }
        return true;
    },
    requireAdmin() {
        if (!this.requireAuth()) return false;
        if (this.getRole() !== 'ROLE_ADMIN') {
            window.location.href = 'transaction.html';
            return false;
        }
        return true;
    },
    requireUser() {
        if (!this.requireAuth()) return false;
        return true;
    },
    setupNavbar() {
        const user = this.getUser();
        if (!user) return;

        const userNameEl = document.getElementById('navUserName');
        const userAvatarEl = document.getElementById('navUserAvatar');
        const logoutBtn = document.getElementById('btnLogout');

        if (userNameEl) userNameEl.textContent = user.fullName || user.username;
        if (userAvatarEl) userAvatarEl.textContent = (user.fullName || user.username).charAt(0).toUpperCase();
        if (logoutBtn) logoutBtn.addEventListener('click', () => this.logout());
    }
};

function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
}

function formatDate(dateStr) {
    if (!dateStr) return '—';
    const d = new Date(dateStr);
    return d.toLocaleString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' });
}

function getStatusBadge(status) {
    const cls = status === 'ACTIVE' ? 'badge-active' : status === 'LOCKED' ? 'badge-locked' : 'badge-cancelled';
    return `<span class="badge-status ${cls}">${status}</span>`;
}

function getTypeBadge(type) {
    const cls = type === 'INTERNAL' ? 'badge-internal' : 'badge-interbank';
    const label = type === 'INTERNAL' ? 'Internal' : 'Interbank';
    return `<span class="${cls}">${label}</span>`;
}

function showAlert(id, message, type = 'success') {
    const el = document.getElementById(id);
    if (!el) return;
    el.className = `alert-custom alert-${type}`;
    el.textContent = message;
    el.style.display = 'block';
    setTimeout(() => { el.style.display = 'none'; }, 5000);
}
