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
    <style>
        .vip_content{
            width: 100%;
            height: 388px;
            background-image:linear-gradient(to right ,#efd35d,#e59230);
        }
    </style>
</head>
<body>
<#include "../header.ftl">
<div class="vip_content">

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
    });
</script>
</body>
</html>