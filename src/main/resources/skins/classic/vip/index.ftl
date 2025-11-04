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
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<#include "../header.ftl">
<div class="container">

    <div class="grid" id="vipGrid">

    </div>
    <#if membership.state == 1>
    <div class="change-config-box" id="vipConfigBox" style="text-align: center;">
        <div>效果预览：</div>
        <div id="configBox" style="margin-bottom: 2rem;font-size: 2rem"></div>
        <button id="configBold" onclick="changeBold()">加粗</button>
        <button id="configLine" onclick="changeLine()">下划线</button>
        <input id="configColor" type="color" onchange="changeColor(this)"/>
        <button onclick="saveConfig()">保存配置</button>
    </div>
    </#if>

    <section class="pricing">
        <h2>选择适合您的方案</h2>

        <!-- 价格切换tab -->
        <div class="pricing-tabs">
            <button class="pricing-tab active" data-period="monthly">按月计费</button>
            <button class="pricing-tab" data-period="yearly">按年计费</button>
        </div>

        <div class="pricing-cards" id="vipDetailGrid">
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
                    <p>
                        “联合会员”是【FishPI机器人开放平台】推出的一项超值特权服务。用户只需一次订阅、一个会员身份，即可在平台所有已对接的第三方机器人应用/程序中，自动解锁对应的VIP高级功能。</p>
                </div>
            </div>

            <div class="accordion-item">
                <button class="accordion-header">是否支持从低会员升级高会员？</button>
                <div class="accordion-content">
                    <p>不支持，请在会员到期后，重新开通。</p>
                </div>
            </div>

            <div class="accordion-item">
                <button class="accordion-header">DIY动态勋章是永久的吗？</button>
                <div class="accordion-content">
                    <p>DIY动态勋章不是永久的，仅在会员有效期内有效，会员过期后会回收。</p>
                </div>
            </div>
        </div>
    </section>
</div>
<#include "../footer.ftl">
<script src="${staticServePath}/js/common${miniPostfix}.js?${staticResourceVersion}"></script>
<script>
    var user = ${user};
    var membership = ${membership};
</script>
<script src="${staticServePath}/skins/classic/vip/js/vip.js?${staticResourceVersion}"></script>
</body>
</html>