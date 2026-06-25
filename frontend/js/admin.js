
let allTransactions = [];

document.addEventListener('DOMContentLoaded', () => {
    if (!Auth.requireAdmin()) return;
    Auth.setupNavbar();

    loadStatistics();
    loadAllTransactions();

    document.getElementById('searchUser').addEventListener('input', filterTable);
    document.getElementById('searchTxId').addEventListener('input', filterTable);
    document.getElementById('filterType').addEventListener('change', filterTable);
});

async function loadStatistics() {
    try {
        const res = await fetch(`${API_BASE}/admin/statistics`, {
            headers: Auth.authHeaders()
        });
        if (!res.ok) throw new Error('Failed');

        const stats = await res.json();
        document.getElementById('statTotal').textContent = stats.totalTransactions || 0;
        document.getElementById('statAmount').textContent = formatCurrency(stats.totalAmount || 0);
        document.getElementById('statInternal').textContent = stats.internalCount || 0;
        document.getElementById('statInterbank').textContent = stats.interbankCount || 0;
    } catch (err) {
        console.error('Stats error:', err);
    }
}

async function loadAllTransactions() {
    const tbody = document.getElementById('adminTableBody');

    try {
        const res = await fetch(`${API_BASE}/admin/transactions`, {
            headers: Auth.authHeaders()
        });
        if (!res.ok) throw new Error('Failed');

        allTransactions = await res.json();
        renderTable(allTransactions);
    } catch (err) {
        tbody.innerHTML = '<tr><td colspan="8" class="empty-state"><p>Lỗi tải dữ liệu</p></td></tr>';
    }
}

function renderTable(transactions) {
    const tbody = document.getElementById('adminTableBody');

    if (transactions.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="empty-state"><div class="icon">📭</div><p>Không có giao dịch nào</p></td></tr>';
        return;
    }

    tbody.innerHTML = transactions.map(tx => {
        const actions = [];
        if (tx.status === 'ACTIVE') {
            actions.push(`<button class="btn-sm-action btn-lock" onclick="lockTx(${tx.id})">🔒 LOCK</button>`);
            actions.push(`<button class="btn-sm-action btn-cancel" onclick="cancelTx(${tx.id})">✖ CANCEL</button>`);
        } else if (tx.status === 'LOCKED') {
            actions.push(`<button class="btn-sm-action btn-cancel" onclick="cancelTx(${tx.id})">✖ CANCEL</button>`);
        } else {
            actions.push('<span style="color: var(--text-muted); font-size: 0.78rem;">—</span>');
        }

        return `
            <tr>
                <td>${tx.id}</td>
                <td style="font-family: monospace; font-size: 0.78rem;">${tx.transactionId ? tx.transactionId.substring(0, 8) + '...' : '—'}</td>
                <td>${tx.customerName || 'ID: ' + tx.customerId}</td>
                <td>${getTypeBadge(tx.type)}</td>
                <td class="${tx.amount > 50000000 ? 'amount-high' : ''}" style="font-weight: 600;">${formatCurrency(tx.amount)}</td>
                <td>${getStatusBadge(tx.status)}</td>
                <td style="font-size: 0.8rem;">${formatDate(tx.createdAt)}</td>
                <td style="white-space: nowrap; display: flex; gap: 0.4rem;">${actions.join('')}</td>
            </tr>
        `;
    }).join('');
}

function filterTable() {
    const searchUser = document.getElementById('searchUser').value.toLowerCase();
    const searchTxId = document.getElementById('searchTxId').value.toLowerCase();
    const filterType = document.getElementById('filterType').value;

    const filtered = allTransactions.filter(tx => {
        const matchUser = !searchUser ||
            (tx.customerName && tx.customerName.toLowerCase().includes(searchUser)) ||
            String(tx.customerId).includes(searchUser);
        const matchTxId = !searchTxId ||
            (tx.transactionId && tx.transactionId.toLowerCase().includes(searchTxId));
        const matchType = !filterType || tx.type === filterType;
        return matchUser && matchTxId && matchType;
    });

    renderTable(filtered);
}

async function lockTx(id) {
    if (!confirm('Bạn có chắc chắn muốn KHÓA giao dịch #' + id + '?')) return;

    try {
        const res = await fetch(`${API_BASE}/admin/transactions/${id}/lock`, {
            method: 'PATCH',
            headers: Auth.authHeaders()
        });
        if (!res.ok) {
            const err = await res.json();
            throw new Error(err.message);
        }
        showAlert('adminAlert', '🔒 Giao dịch #' + id + ' đã bị khóa', 'success');
        loadStatistics();
        loadAllTransactions();
    } catch (err) {
        showAlert('adminAlert', '❌ ' + err.message, 'danger');
    }
}

async function cancelTx(id) {
    if (!confirm('Bạn có chắc chắn muốn HỦY giao dịch #' + id + '?')) return;

    try {
        const res = await fetch(`${API_BASE}/admin/transactions/${id}/cancel`, {
            method: 'PATCH',
            headers: Auth.authHeaders()
        });
        if (!res.ok) {
            const err = await res.json();
            throw new Error(err.message);
        }
        showAlert('adminAlert', '✖ Giao dịch #' + id + ' đã bị hủy', 'success');
        loadStatistics();
        loadAllTransactions();
    } catch (err) {
        showAlert('adminAlert', '❌ ' + err.message, 'danger');
    }
}
