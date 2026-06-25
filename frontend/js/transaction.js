
document.addEventListener('DOMContentLoaded', () => {
    if (!Auth.requireUser()) return;
    Auth.setupNavbar();

    const user = Auth.getUser();

    const transactionType = document.getElementById('transactionType');
    const amount = document.getElementById('amount');

    transactionType.addEventListener('change', updateForm);
    amount.addEventListener('input', updateForm);

    function updateForm() {
        const type = transactionType.value;
        const amt = parseFloat(amount.value) || 0;

        const receiverAccountGroup = document.getElementById('receiverAccountGroup');
        const receiverBankGroup = document.getElementById('receiverBankGroup');
        const swiftCodeGroup = document.getElementById('swiftCodeGroup');
        const digitalSignatureGroup = document.getElementById('digitalSignatureGroup');

        if (type === 'INTERNAL') {
            receiverAccountGroup.classList.remove('hidden');
            receiverBankGroup.classList.add('hidden');
            swiftCodeGroup.classList.add('hidden');
            digitalSignatureGroup.classList.add('hidden');
        } else if (type === 'INTERBANK') {
            receiverAccountGroup.classList.add('hidden');
            receiverBankGroup.classList.remove('hidden');
            swiftCodeGroup.classList.remove('hidden');

            if (amt > 50000000) {
                digitalSignatureGroup.classList.remove('hidden');
            } else {
                digitalSignatureGroup.classList.add('hidden');
            }
        } else {
            receiverAccountGroup.classList.add('hidden');
            receiverBankGroup.classList.add('hidden');
            swiftCodeGroup.classList.add('hidden');
            digitalSignatureGroup.classList.add('hidden');
        }
    }

    loadTransactions();

    document.getElementById('transactionForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const btn = document.getElementById('btnSubmit');
        btn.disabled = true;
        btn.innerHTML = '<span class="spinner"></span> Đang xử lý...';

        const type = transactionType.value;
        const body = {
            type: type,
            senderAccount: document.getElementById('senderAccount').value,
            amount: parseFloat(amount.value)
        };

        if (type === 'INTERNAL') {
            body.receiverAccount = document.getElementById('receiverAccount').value;
        } else if (type === 'INTERBANK') {
            body.receiverBank = document.getElementById('receiverBank').value;
            body.swiftCode = document.getElementById('swiftCode').value;
            body.digitalSignature = document.getElementById('digitalSignature').value || '';
        }

        try {
            const res = await fetch(`${API_BASE}/user/transactions`, {
                method: 'POST',
                headers: Auth.authHeaders(),
                body: JSON.stringify(body)
            });

            if (!res.ok) {
                const err = await res.json();
                throw new Error(err.message || 'Tạo giao dịch thất bại');
            }

            showAlert('formAlert', '✅ Giao dịch đã được tạo thành công!', 'success');
            document.getElementById('transactionForm').reset();
            updateForm();
            loadTransactions();
        } catch (err) {
            showAlert('formAlert', '❌ ' + err.message, 'danger');
        } finally {
            btn.disabled = false;
            btn.textContent = 'Tạo giao dịch';
        }
    });
});

async function loadTransactions() {
    const tbody = document.getElementById('transactionTableBody');

    try {
        const res = await fetch(`${API_BASE}/user/transactions`, {
            headers: Auth.authHeaders()
        });

        if (!res.ok) throw new Error('Failed to load');

        const transactions = await res.json();

        if (transactions.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="empty-state"><div class="icon">📭</div><p>Chưa có giao dịch</p></td></tr>';
            return;
        }

        tbody.innerHTML = transactions.map(tx => `
            <tr>
                <td style="font-family: monospace; font-size: 0.78rem;">${tx.transactionId ? tx.transactionId.substring(0, 8) + '...' : '—'}</td>
                <td>${getTypeBadge(tx.type)}</td>
                <td class="${tx.amount > 50000000 ? 'amount-high' : ''}">${formatCurrency(tx.amount)}</td>
                <td>${getStatusBadge(tx.status)}</td>
                <td style="font-size: 0.8rem;">${formatDate(tx.createdAt)}</td>
            </tr>
        `).join('');
    } catch (err) {
        tbody.innerHTML = '<tr><td colspan="5" class="empty-state"><p>Lỗi tải dữ liệu</p></td></tr>';
    }
}
