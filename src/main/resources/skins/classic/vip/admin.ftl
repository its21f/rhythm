<#--

    Rhythm - A modern community (forum/BBS/SNS/blog) platform written in Java.
    Modified version from Symphony, Thanks Symphony :)
    Copyright (C) 2012-present, b3log.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

-->
<#include "../macro-head.ftl">
<!DOCTYPE html>
<html>
<head>
    <@head title="VIP管理 - ${symphonyLabel}">
        <meta name="robots" content="none"/>
    </@head>
    <link rel="stylesheet" href="${staticServePath}/css/home.css?${staticResourceVersion}"/>
    <link rel="stylesheet" href="${staticServePath}/skins/classic/vip/css/admin.css?${staticResourceVersion}"/>
    <style>

    </style>
</head>
<body>
<#include "../header.ftl">
<div class="container">

    <div class="tabs">
        <button class="tab active" data-tab="membership">会员月卡管理</button>
        <button class="tab" data-tab="coupons">优惠券管理</button>
    </div>

    <!-- 会员月卡管理 -->
    <div id="membership" class="tab-content active">
        <div class="card">
            <h2>添加会员月卡</h2>
            <form id="membershipForm">
                <div class="form-row">
                    <div class="form-group">
                        <label>等级名称 *</label>
                        <input type="text" class="form-control" id="lvName" value="" required>
                    </div>
                    <div class="form-group">
                        <label>等级代码 *</label>
                        <input type="text" class="form-control" id="lvCode" value="" required>
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label>价格 *</label>
                        <input type="number" class="form-control" id="price" min="0" step="1" value="0" required>
                    </div>
                    <div class="form-group">
                        <label>时长类型 *</label>
                        <input type="text" class="form-control" id="durationType" value="" required>
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label>时长值 *</label>
                        <input type="number" class="form-control" id="durationValue" min="1" value="" required>
                    </div>
                    <div class="form-group">
                        <label>权益 *</label>
                        <input type="text" class="form-control" id="benefits" required>
                    </div>
                </div>
                <button type="submit" class="btn btn-success">添加会员月卡</button>
                <button type="button" class="btn btn-outline" onclick="resetMembershipForm()">重置表单</button>
            </form>
        </div>

        <div class="card">
            <h2>会员月卡列表</h2>
            <div class="search-box">
                <input type="text" class="search-input" id="membershipSearch" placeholder="搜索等级名称或代码...">
                <button class="btn" onclick="searchMemberships()">搜索</button>
                <button class="btn btn-outline" onclick="clearMembershipSearch()">清除</button>
            </div>
            <div class="table-container">
                <table id="membershipTable">
                    <thead>
                    <tr>
                        <th>等级名称</th>
                        <th>等级代码</th>
                        <th>价格</th>
                        <th>时长类型</th>
                        <th>时长值</th>
                        <th>权益</th>
                        <th>创建时间</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody id="membershipList">
                    <!-- 会员数据将通过JavaScript动态加载 -->
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- 优惠券管理 -->
    <div id="coupons" class="tab-content">
        <div class="card">
            <h2>添加优惠券</h2>
            <form id="couponForm">
                <div class="form-row">
                    <div class="form-group">
                        <label>优惠券代码</label>
                        <input type="text" class="form-control" id="couponCode" maxlength="32" minlength="8">
                    </div>
                    <div class="form-group">
                        <label>优惠券类型</label>
                        <input type="text" class="form-control" id="couponName">
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label>折扣金额 (百分比) *</label>
                        <input type="number" class="form-control" id="discount" min="0" step="0.01" value="100"
                               required>
                    </div>
                    <div class="form-group">
                        <label>使用次数 *</label>
                        <input type="number" class="form-control" id="times" value="-1" required>
                        <small style="color: #666;">-1 表示无限制</small>
                    </div>
                </div>
                <button type="submit" class="btn btn-success">添加优惠券</button>
                <button type="button" class="btn btn-outline" onclick="resetCouponForm()">重置表单</button>
            </form>
        </div>

        <div class="card">
            <h2>优惠券列表</h2>
            <div class="search-box">
                <input type="text" class="search-input" id="couponSearch" placeholder="搜索优惠券名称或代码...">
                <button class="btn" onclick="searchCoupons()">搜索</button>
                <button class="btn btn-outline" onclick="clearCouponSearch()">清除</button>
            </div>
            <div class="table-container">
                <table id="couponTable">
                    <thead>
                    <tr>
                        <th>优惠券代码</th>
                        <th>优惠券类型</th>
                        <th>折扣</th>
                        <th>使用次数</th>
                        <th>创建时间</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody id="couponList">
                    <!-- 优惠券数据将通过JavaScript动态加载 -->
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <div id="message" class="message" style="display: none;"></div>
</div>
<#include "../footer.ftl">
<script>
    const memberships = [];
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

        fetch('/admin/membership/level',{
            method:"POST",
            headers:{
                "Content-type":"application-json"
            },
            body:JSON.stringify(formData)
        })
            .then(response => response.json())
            .then(data => {
                console.log(data)
                loadMemberships();
                resetMembershipForm();
                showMessage('会员月卡添加成功！', 'success');
            })
    }

    function loadMembershipLevels(){
        fetch('/api/membership/levels')
            .then(response => response.json())
            .then(data => {
                console.log(data)
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
            <#if data??>
            data.forEach(member => {
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
                        <button class="action-btn edit-btn" onclick="editMembership(${member.id})">编辑</button>
                        <button class="action-btn delete-btn" onclick="deleteMembership(${member.id})">删除</button>
                    </td>
                `;
                tbody.appendChild(row);

            });
            </#if>
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
            const statusClass = coupon.status === 'active' ? 'status-active' :
                coupon.status === 'expired' ? 'status-expired' : 'status-inactive';

            const statusText = {
                'active': '有效',
                'inactive': '无效',
                'expired': '已过期'
            }[coupon.status] || coupon.status;

            const timesText = coupon.times === -1 ? '无限制' : coupon.times;

            const row = document.createElement('tr');
            <#--row.innerHTML = `-->
            <#--        <td>${coupon.name}</td>-->
            <#--        <td><strong>${coupon.code}</strong></td>-->
            <#--        <td>¥${coupon.discount}</td>-->
            <#--        <td>${timesText}</td>-->
            <#--        <td>${coupon.startDate} 至 ${coupon.endDate}</td>-->
            <#--        <td><span class="status-badge ${statusClass}">${statusText}</span></td>-->
            <#--        <td>${coupon.createdAt}</td>-->
            <#--        <td class="action-buttons">-->
            <#--            <button class="action-btn edit-btn" onclick="editCoupon(${coupon.id})">编辑</button>-->
            <#--            <button class="action-btn delete-btn" onclick="deleteCoupon(${coupon.id})">删除</button>-->
            <#--        </td>-->
            <#--    `;-->
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
        <#--const messageEl = document.getElementById('message');-->
        <#--messageEl.textContent = text;-->
        <#--messageEl.className = `message message-${type}`;-->
        <#--messageEl.style.display = 'block';-->

        <#--setTimeout(() => {-->
        <#--    messageEl.style.display = 'none';-->
        <#--}, 3000);-->
    }
</script>
</body>
</html>