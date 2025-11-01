let vipList = []
let isYear = false;

// 获取会员列表
function updateStats() {
    fetch('/api/membership/levels')
        .then(response => response.json())
        .then(data => {
            if (data.code === 0) {
                vipList = simpleProcessVIPData(data);
                loadPageData();
            }
        })
        .catch(error => {
            console.error('Error fetching data:', error);
        });
}

// 确保DOM完全加载后再执行
document.addEventListener('DOMContentLoaded', function () {
    // 初始加载数据
    updateStats();
    checkVip();
    initScript();
});

function initScript() {
    // 手风琴交互效果
    document.querySelectorAll('.accordion-header').forEach(button => {
        button.addEventListener('click', () => {
            const isActive = button.classList.contains('active');

            // 关闭所有打开的项
            document.querySelectorAll('.accordion-header').forEach(header => {
                header.classList.remove('active');
            });
            document.querySelectorAll('.accordion-content').forEach(content => {
                content.classList.remove('show');
            });

            // 如果点击的项之前是关闭的，则打开它
            if (!isActive) {
                button.classList.add('active');
                button.nextElementSibling.classList.add('show');
            }
        });
    });

    // 价格tab切换效果
    document.querySelectorAll('.pricing-tab').forEach(tab => {
        tab.addEventListener('click', () => {
            const period = tab.getAttribute('data-period');

            // 更新tab状态
            document.querySelectorAll('.pricing-tab').forEach(t => {
                t.classList.remove('active');
            });
            tab.classList.add('active');

            // 切换价格显示
            if (period === 'monthly') {
                isYear = false;
                document.querySelectorAll('.price-monthly').forEach(el => {
                    el.classList.remove('hide');
                });
                document.querySelectorAll('.price-yearly').forEach(el => {
                    el.classList.remove('show');
                });
            } else {
                isYear = true;
                document.querySelectorAll('.price-monthly').forEach(el => {
                    el.classList.add('hide');
                });
                document.querySelectorAll('.price-yearly').forEach(el => {
                    el.classList.add('show');
                });
            }
        });
    });
}

function simpleProcessVIPData(originalData) {
    const result = {};

    originalData.data.forEach(item => {
        const vipLevel = item.lvCode.split('_')[0];

        if (!result[vipLevel]) {
            result[vipLevel] = {
                name: item.lvName,
                benefits: JSON.parse(item.benefits),
                monthly: {},
                yearly: {}
            };
        }

        if (item.durationType === '月卡') {
            result[vipLevel].monthly = {
                price: item.price,
                duration: item.durationValue,
                code: item.lvCode,
                oId: item.oId
            };
        } else if (item.durationType === '年卡') {
            result[vipLevel].yearly = {
                price: item.price,
                duration: item.durationValue,
                code: item.lvCode,
                oId: item.oId
            };
        }
    });

    return result;
}

function loadPageData() {
    let vipCardGrid = document.querySelector('#vipGrid');
    let html = '';

    let vipDetailGrid = document.querySelector('#vipDetailGrid');
    let detailHtml = '';

    for (let item in vipList) {
        const vip = vipList[item];
        html += `<div class="card">
            <h3>${vip.name}</h3>
            <p>限时特惠: ${vip.monthly.price} 积分/月</p>
            <a href="#" class="btn" onclick="buyVip('${item}')">立即开通</a>
        </div>`

        detailHtml += `<div class="pricing-card ${item === 'VIP2' ? 'popular' : ''}">
                ${item === 'VIP2' ? '<div class="popular-badge">最受欢迎</div>' : ''}
                <div>
                    <h3>${vip.name}</h3>
                    <div class="price">
                        <span class="price-monthly">${vip.monthly.price}积分 <span>/月</span></span>
                        <span class="price-yearly">${vip.yearly.price}积分 <span>/年</span>
                                <div class="price-savings">节省 ${vip.monthly.price * 2}积分</div>
                            </span>
                    </div>
                    <ul class="pricing-features">
                       ${vip.benefits.bold != null ? ' <li><i class="fas fa-check"></i> 昵称加粗</li>' : ''}
                       ${vip.benefits.underline != null ? ' <li><i class="fas fa-check"></i> 昵称下划线</li>' : ''}
                       ${vip.benefits.color != null ? ' <li><i class="fas fa-check"></i> 昵称颜色</li>' : ''}
                       ${vip.benefits.autoCheckin != null ? ' <li><i class="fas fa-check"></i> 自动签到(年付)</li>' : ''}
                       ${vip.benefits.checkinCard != null ? ' <li><i class="fas fa-check"></i> 免签卡(年付): ' + vip.benefits.checkinCard + '张</li>' : ''}
                       ${vip.benefits.metal != null ? ' <li><i class="fas fa-check"></i> DIY动态勋章</li>' : ''}
                       ${vip.benefits.jointVip != null ? ' <li><i class="fas fa-check"></i> 联合会员</li>' : ''}
                    </ul>
                </div>
                <a href="#" class="btn" onclick="buyVip('${item}')">立即开通</a>
            </div>`
    }
    vipCardGrid.innerHTML = html;
    vipDetailGrid.innerHTML = detailHtml;
}

function buyVip(vip) {
    const priceType = isYear ? 'yearly' : 'monthly';
    if (confirm(`确定要开通${vipList[vip].name}一${isYear ? '年' : '月'}？此操作不可恢复。`)) {
        fetch('/api/membership/open',
            {
                method: 'POST',
                body: JSON.stringify({
                    oId: vipList[vip][priceType].oId,
                })
            })
            .then(response => response.json())
            .then(data => {
                if (data.code === 0) {
                    vipList = simpleProcessVIPData(data);
                    loadPageData();
                }
            })
            .catch(error => {
                console.error('Error fetching data:', error);
            });
    }
}

function checkVip() {
    fetch('/api/membership/'+'1761926961020')
        .then(response => response.json())
        .then(data => {
            if (data.code === 0) {
                console.log(data)
            }
        })
        .catch(error => {
            console.error('Error fetching data:', error);
        });
}