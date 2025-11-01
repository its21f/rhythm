let memberships = [];
// 初始化页面
document.addEventListener('DOMContentLoaded', function () {
    // 绑定事件
    bindEvents();
    // 加载数据
    // loadMemberships();
    // loadCoupons();
    loadMembershipLevels();
});

// 绑定事件
function bindEvents() {
    // Tab切换
    document.querySelectorAll('.tab').forEach(tab => {
        tab.addEventListener('click', function () {
            const tabId = this.getAttribute('data-tab');
            // 更新tab状态
            document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
            this.classList.add('active');

            // 更新内容显示
            document.querySelectorAll('.tab-content').forEach(content => {
                content.classList.remove('active');
            });
            document.getElementById(tabId).classList.add('active');
        });
    });

    // 表单提交
    document.getElementById('membershipForm').addEventListener('submit', function (e) {
        e.preventDefault();
        addMembership();
    });

    document.getElementById('couponForm').addEventListener('submit', function (e) {
        e.preventDefault();
        addCoupon();
    });

    // 搜索框回车事件
    document.getElementById('membershipSearch').addEventListener('keypress', function (e) {
        if (e.key === 'Enter') searchMemberships();
    });

    document.getElementById('couponSearch').addEventListener('keypress', function (e) {
        if (e.key === 'Enter') searchCoupons();
    });
}

// 会员月卡相关函数
function addMembership() {
    const formData = {
        lvName: document.getElementById('lvName').value.trim(),
        lvCode: document.getElementById('lvCode').value.trim(),
        price: parseFloat(document.getElementById('price').value),
        durationType: document.getElementById('durationType').value.trim(),
        durationValue: parseInt(document.getElementById('durationValue').value),
        benefits: document.getElementById('benefits').value.trim(),
    };

    if (!formData.lvName || !formData.lvCode || !formData.price || !formData.durationType || !formData.durationValue || !formData.benefits) {
        showMessage('请填写所有必填字段', 'error');
        return;
    }

    fetch('/admin/membership/level', {
        method: "POST",
        headers: {
            "Content-type": "application-json"
        },
        body: JSON.stringify(formData)
    })
        .then(response => response.json())
        .then(data => {
            console.log(data)
            loadMemberships();
            resetMembershipForm();
            showMessage('会员月卡添加成功！', 'success');
        })
}

function loadMembershipLevels() {
    fetch('/api/membership/levels')
        .then(response => response.json())
        .then(data => {
            console.log(data)
            if (data.code === 0) {
                memberships = data.data;
                loadMemberships(memberships);
            }
        })
        .catch(error => {
            console.error('Error fetching data:', error);
        });
}

function loadMemberships(filteredData = []) {
    const data = filteredData;
    const tbody = document.getElementById('membershipList');
    tbody.innerHTML = '';
    if (data.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" style="text-align: center; color: #999;">暂无会员月卡数据</td></tr>';
    } else {
        data.forEach(member => {
            console.log(member)
            const row = document.createElement('tr');
            row.innerHTML = `
                    <td>${member.lvName}</td>
                    <td>${member.lvCode}</td>
                    <td>¥${member.price}</td>
                    <td>${member.durationType}</td>
                    <td>${member.durationValue}天</td>
                    <td>${member.benefits}</td>
                    <td>${member.createdAt}</td>
                    <td class="action-buttons">
                        <button class="action-btn edit-btn" onclick="editMembership(${member.oId})">编辑</button>
                        <button class="action-btn delete-btn" onclick="deleteMembership(${member.oId})">删除</button>
                    </td>
                `;
            console.log(row)
            tbody.appendChild(row);
        });
    }
}

function editMembership(id) {
    const member = memberships.find(m => m.id === id);
    if (member) {
        document.getElementById('lvName').value = member.lvName;
        document.getElementById('lvCode').value = member.lvCode;
        document.getElementById('price').value = member.price;
        document.getElementById('durationType').value = member.durationType;
        document.getElementById('durationValue').value = member.durationValue;
        document.getElementById('benefits').value = member.benefits;

        // 更改按钮为更新模式
        const submitBtn = document.querySelector('#membershipForm button[type="submit"]');
        submitBtn.textContent = '更新会员月卡';
        submitBtn.setAttribute('data-edit-id', id);
        submitBtn.onclick = function (e) {
            e.preventDefault();
            updateMembership(id);
        };

        showMessage('请修改会员信息后点击更新', 'success');
    }
}

function updateMembership(id) {
    const index = memberships.findIndex(m => m.id === id);
    if (index !== -1) {
        memberships[index] = {
            ...memberships[index],
            lvName: document.getElementById('lvName').value.trim(),
            lvCode: document.getElementById('lvCode').value.trim(),
            price: parseFloat(document.getElementById('price').value),
            durationType: document.getElementById('durationType').value.trim(),
            durationValue: parseInt(document.getElementById('durationValue').value),
            benefits: document.getElementById('benefits').value.trim()
        };

        localStorage.setItem('memberships', JSON.stringify(memberships));
        loadMemberships();
        resetMembershipForm();
        showMessage('会员月卡更新成功！', 'success');
    }
}

function deleteMembership(id) {
    if (confirm('确定要删除这个会员月卡吗？此操作不可恢复。')) {
        memberships = memberships.filter(m => m.id !== id);
        localStorage.setItem('memberships', JSON.stringify(memberships));
        loadMemberships();
        showMessage('会员月卡删除成功！', 'success');
    }
}

function resetMembershipForm() {
    document.getElementById('membershipForm').reset();
    const submitBtn = document.querySelector('#membershipForm button[type="submit"]');
    submitBtn.textContent = '添加会员月卡';
    submitBtn.removeAttribute('data-edit-id');
    submitBtn.onclick = function (e) {
        e.preventDefault();
        addMembership();
    };
}

function searchMemberships() {
    const searchTerm = document.getElementById('membershipSearch').value.toLowerCase().trim();
    if (!searchTerm) {
        loadMemberships();
        return;
    }

    const filtered = memberships.filter(member =>
        member.lvName.toLowerCase().includes(searchTerm) ||
        member.lvCode.toLowerCase().includes(searchTerm)
    );

    loadMemberships(filtered);
}

function clearMembershipSearch() {
    document.getElementById('membershipSearch').value = '';
    loadMemberships();
}

// 优惠券相关函数
function addCoupon() {
    const formData = {
        id: Date.now(),
        name: document.getElementById('couponName').value.trim(),
        code: document.getElementById('couponCode').value.trim().toUpperCase(),
        discount: parseFloat(document.getElementById('discount').value),
        times: parseInt(document.getElementById('times').value),
        startDate: document.getElementById('startDate').value,
        endDate: document.getElementById('endDate').value,
        usedCount: 0,
        status: 'active',
        createdAt: new Date().toLocaleString('zh-CN')
    };

    if (!formData.name || !formData.code || !formData.discount) {
        showMessage('请填写所有必填字段', 'error');
        return;
    }

    // 检查优惠券代码是否已存在
    if (coupons.some(coupon => coupon.code === formData.code)) {
        showMessage('优惠券代码已存在，请使用其他代码', 'error');
        return;
    }

    coupons.unshift(formData);
    localStorage.setItem('coupons', JSON.stringify(coupons));

    loadCoupons();
    resetCouponForm();
    showMessage('优惠券添加成功！', 'success');
}

function loadCoupons(filteredData = []) {
    const data = filteredData;
    const tbody = document.getElementById('couponList');

    tbody.innerHTML = '';

    if (data.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" style="text-align: center; color: #999;">暂无优惠券数据</td></tr>';
        return;
    }

    data.forEach(coupon => {

        const timesText = coupon.times === -1 ? '无限制' : coupon.times;

        const row = document.createElement('tr');
        row.innerHTML = `
                    <td>${coupon.name}</td>
                    <td><strong>${coupon.code}</strong></td>
                    <td>¥${coupon.discount}</td>
                    <td>${timesText}</td>
                    <td>${coupon.startDate} 至 ${coupon.endDate}</td>
                    <td><span class="status-badge ${statusClass}">${statusText}</span></td>
                    <td>${coupon.createdAt}</td>
                    <td class="action-buttons">
                        <button class="action-btn edit-btn" onclick="editCoupon(${coupon.id})">编辑</button>
                        <button class="action-btn delete-btn" onclick="deleteCoupon(${coupon.id})">删除</button>
                    </td>
                `;
        tbody.appendChild(row);
    });
}

function editCoupon(id) {
    const coupon = coupons.find(c => c.id === id);
    if (coupon) {
        document.getElementById('couponName').value = coupon.name;
        document.getElementById('couponCode').value = coupon.code;
        document.getElementById('discount').value = coupon.discount;
        document.getElementById('times').value = coupon.times;
        document.getElementById('startDate').value = coupon.startDate;
        document.getElementById('endDate').value = coupon.endDate;

        // 更改按钮为更新模式
        const submitBtn = document.querySelector('#couponForm button[type="submit"]');
        submitBtn.textContent = '更新优惠券';
        submitBtn.setAttribute('data-edit-id', id);
        submitBtn.onclick = function (e) {
            e.preventDefault();
            updateCoupon(id);
        };

        showMessage('请修改优惠券信息后点击更新', 'success');
    }
}

function updateCoupon(id) {
    const index = coupons.findIndex(c => c.id === id);
    if (index !== -1) {
        coupons[index] = {
            ...coupons[index],
            name: document.getElementById('couponName').value.trim(),
            code: document.getElementById('couponCode').value.trim().toUpperCase(),
            discount: parseFloat(document.getElementById('discount').value),
            times: parseInt(document.getElementById('times').value),
            startDate: document.getElementById('startDate').value,
            endDate: document.getElementById('endDate').value
        };

        localStorage.setItem('coupons', JSON.stringify(coupons));
        loadCoupons();
        resetCouponForm();
        showMessage('优惠券更新成功！', 'success');
    }
}

function deleteCoupon(id) {
    if (confirm('确定要删除这个优惠券吗？此操作不可恢复。')) {
        coupons = coupons.filter(c => c.id !== id);
        localStorage.setItem('coupons', JSON.stringify(coupons));
        loadCoupons();
        showMessage('优惠券删除成功！', 'success');
    }
}

function resetCouponForm() {
    document.getElementById('couponForm').reset();

    const submitBtn = document.querySelector('#couponForm button[type="submit"]');
    submitBtn.textContent = '添加优惠券';
    submitBtn.removeAttribute('data-edit-id');
    submitBtn.onclick = function (e) {
        e.preventDefault();
        addCoupon();
    };
}

function searchCoupons() {
    const searchTerm = document.getElementById('couponSearch').value.toLowerCase().trim();
    if (!searchTerm) {
        loadCoupons();
        return;
    }

    const filtered = coupons.filter(coupon =>
        coupon.name.toLowerCase().includes(searchTerm) ||
        coupon.code.toLowerCase().includes(searchTerm)
    );

    loadCoupons(filtered);
}

function clearCouponSearch() {
    document.getElementById('couponSearch').value = '';
    loadCoupons();
}

// 通用函数
function showMessage(text, type) {
    const messageEl = document.getElementById('message');
    messageEl.textContent = text;
    messageEl.className = `message message-${type}`;
    messageEl.style.display = 'block';

    setTimeout(() => {
        messageEl.style.display = 'none';
    }, 3000);
}