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
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>访客验证</title>
    <script src="https://static.geetest.com/v4/gt4.js"></script>
    <script src="https://file.fishpi.cn/tac/jquery.min.js"></script>
</head>

<body>
<div>
    <br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>
    <div id="captcha"></div>
    <br>
    <script>
        var captchaId = "6d886bcaec3f86fcfd6f61bff5af2cb4"
        var product = "float"
        if (product !== 'bind') {
            $('#btn').remove();
        }

        initGeetest4({
            captchaId: captchaId,
            product: product,
        }, function (gt) {
            window.gt = gt
            gt
                .appendTo("#captcha")
                .onSuccess(function (e) {
                    var result = gt.getValidate();
                    var requestJSONObject = {
                        captcha: result
                    };

                    $.ajax({
                        url: "/validateCaptcha",
                        type: "POST",
                        cache: false,
                        data: JSON.stringify(requestJSONObject),
                        success: function (data) {
                            if (data.code === 0) {
                                window.location.href = "/"
                            } else {
                                gt.reset();
                            }
                        }
                    });
                    setTimeout(function () {
                        gt.reset();
                    }, 3000);
                })
        });

    </script>
</div>
<script>

</script>
</body>
</html>
