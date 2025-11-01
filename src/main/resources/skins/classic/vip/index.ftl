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
    <@head title="摸鱼派VIP - ${symphonyLabel}">
        <meta name="robots" content="none"/>
    </@head>
    <link rel="stylesheet" href="${staticServePath}/css/home.css?${staticResourceVersion}"/>
    <link rel="stylesheet" href="${staticServePath}/skins/classic/vip/css/vips.css?${staticResourceVersion}"/>
</head>
<body>
<#include "../header.ftl">
<div class="container">

    <div class="grid">
        <div class="card">
            <div class="card-icon">
                <i class="fas fa-palette"></i>
            </div>
            <h3>精美设计</h3>
            <p>精心设计的用户界面，注重细节和用户体验，提供愉悦的视觉感受。</p>
            <a href="#" class="btn">探索设计</a>
        </div>

        <div class="card">
            <div class="card-icon">
                <i class="fas fa-bolt"></i>
            </div>
            <h3>极致性能</h3>
            <p>轻量级代码，快速加载，流畅的动画效果，提供卓越的性能体验。</p>
            <a href="#" class="btn">查看性能</a>
        </div>

        <div class="card">
            <div class="card-icon">
                <i class="fas fa-bolt"></i>
            </div>
            <h3>极致性能</h3>
            <p>轻量级代码，快速加载，流畅的动画效果，提供卓越的性能体验。</p>
            <a href="#" class="btn">查看性能</a>
        </div>

        <div class="card">
            <div class="card-icon">
                <i class="fas fa-mobile-alt"></i>
            </div>
            <h3>完全响应</h3>
            <p>自适应各种屏幕尺寸，从手机到桌面设备都能完美呈现内容。</p>
            <a href="#" class="btn">测试响应</a>
        </div>
    </div>

    <section class="pricing">
        <h2>选择适合您的方案</h2>

        <!-- 价格切换tab -->
        <div class="pricing-tabs">
            <button class="pricing-tab active" data-period="monthly">按月计费</button>
            <button class="pricing-tab" data-period="yearly">按年计费</button>
        </div>

        <div class="pricing-cards">
            <div class="pricing-card">
                <h3>体验版</h3>
                <div class="price">
                    <span class="price-monthly">4096积分 <span>/月</span></span>
                    <span class="price-yearly">40960积分 <span>/年</span>
                        <div class="price-savings">节省 8192积分</div>
                    </span>
                </div>
                <ul class="pricing-features">
                    <li><i class="fas fa-check"></i> 基本组件库</li>
                    <li><i class="fas fa-check"></i> 社区支持</li>
                    <li><i class="fas fa-check"></i> 基础文档</li>
                    <li><i class="fas fa-times"></i> 高级组件</li>
                    <li><i class="fas fa-times"></i> 优先支持</li>
                </ul>
                <a href="#" class="btn btn-outline">立即购买</a>
            </div>

            <div class="pricing-card popular">
                <div class="popular-badge">最受欢迎</div>
                <h3>专业版</h3>
                <div class="price">
                    <span class="price-monthly">10240积分 <span>/月</span></span>
                    <span class="price-yearly">102400积分 <span>/年</span>
                            <div class="price-savings">节省 20480积分</div>
                        </span>
                </div>
                <ul class="pricing-features">
                    <li><i class="fas fa-check"></i> 完整组件库</li>
                    <li><i class="fas fa-check"></i> 优先支持</li>
                    <li><i class="fas fa-check"></i> 详细文档</li>
                    <li><i class="fas fa-check"></i> 高级组件</li>
                    <li><i class="fas fa-check"></i> 定期更新</li>
                </ul>
                <a href="#" class="btn">立即购买</a>
            </div>

            <div class="pricing-card">
                <h3>企业版</h3>
                <div class="price">
                    <span class="price-monthly">20480积分 <span>/月</span></span>
                    <span class="price-yearly">204800积分 <span>/年</span>
                            <div class="price-savings">节省 40960积分</div>
                        </span>
                </div>
                <ul class="pricing-features">
                    <li><i class="fas fa-check"></i> 完整组件库</li>
                    <li><i class="fas fa-check"></i> 专属支持</li>
                    <li><i class="fas fa-check"></i> 定制组件</li>
                    <li><i class="fas fa-check"></i> 源码访问</li>
                    <li><i class="fas fa-check"></i> 白标解决方案</li>
                </ul>
                <a href="#" class="btn btn-outline">立即购买</a>
            </div>

            <div class="pricing-card">
                <h3>企业定制版</h3>
                <div class="price">
                    <span class="price-monthly">40960积分 <span>/月</span></span>
                    <span class="price-yearly">409600积分 <span>/年</span>
                            <div class="price-savings">节省 81920积分</div>
                        </span>
                </div>
                <ul class="pricing-features">
                    <li><i class="fas fa-check"></i> 完整组件库</li>
                    <li><i class="fas fa-check"></i> 专属支持</li>
                    <li><i class="fas fa-check"></i> 定制组件</li>
                    <li><i class="fas fa-check"></i> 源码访问</li>
                    <li><i class="fas fa-check"></i> 白标解决方案</li>
                </ul>
                <a href="#" class="btn btn-outline">立即购买</a>
            </div>
        </div>
    </section>

    <section class="faq-section">
        <h2>常见问题</h2>
        <div class="accordion">
            <div class="accordion-item">
                <button class="accordion-header">是否可以申请退款？</button>
                <div class="accordion-content">
                    <p>不可以。</p>
                </div>
            </div>

            <div class="accordion-item">
                <button class="accordion-header">什么是联合会员？</button>
                <div class="accordion-content">
                    <p>“联合会员”是【FishPI机器人开放平台】推出的一项超值特权服务。用户只需一次订阅、一个会员身份，即可在平台所有已对接的第三方机器人应用/程序中，自动解锁对应的VIP高级功能。</p>
                </div>
            </div>

            <div class="accordion-item">
                <button class="accordion-header">是否支持从低会员升级高会员？</button>
                <div class="accordion-content">
                    <p>不支持，请在会员到期后，重新开通。</p>
                </div>
            </div>

            <div class="accordion-item">
                <button class="accordion-header">自定义动态勋章是永久的吗？</button>
                <div class="accordion-content">
                    <p>自定义动态勋章不是永久的，仅在会员有效期内有效，会员过期后会回收。</p>
                </div>
            </div>
        </div>
    </section>
</div>
<#include "../footer.ftl">
<script>
    // 获取会员列表
    function updateStats() {
        fetch('/api/membership/levels')
            .then(response => response.json())
            .then(data => {
                console.log(data)
            })
            .catch(error => {
                console.error('Error fetching data:', error);
            });
    }

    // 确保DOM完全加载后再执行
    document.addEventListener('DOMContentLoaded', function() {
        // 初始加载数据
        updateStats();
        initScript();
    });

    function initScript(){
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
                    document.querySelectorAll('.price-monthly').forEach(el => {
                        el.classList.remove('hide');
                    });
                    document.querySelectorAll('.price-yearly').forEach(el => {
                        el.classList.remove('show');
                    });
                } else {
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
</script>
</body>
</html>