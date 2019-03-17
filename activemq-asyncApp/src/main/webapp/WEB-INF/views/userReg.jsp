<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%
    String path = request.getContextPath();
    System.out.println(path);
    String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + path + "/";
    System.out.println(basePath);
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <base href="<%=basePath%>">

    <title>用户注册</title>

    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <script type="text/javascript" src="<%=basePath%>/assets/js/jquery-1.11.0.min.js"></script>
    <style type="text/css">
        .h1 {
            margin: 0 auto;
        }

        #producer {
            width: 48%;
            border: 1px solid blue;
            height: 80%;
            align: center;
            margin: 0 auto;
        }

        body {
            text-align: center;
        }

        div {
            text-align: center;
        }

        textarea {
            width: 80%;
            height: 100px;
            border: 1px solid gray;
        }

        button {
            background-color: rgb(62, 156, 66);
            border: none;
            font-weight: bold;
            color: white;
            height: 30px;
        }
    </style>
    <script type="text/javascript">

        function send() {
            $.ajax({
                type: 'get',
                url: '<%=basePath%>saveUser?name=' + $("#name").val()
                + '&email=' + $("#email").val() + '&number=' + $("#number").val()
                + '&address=' + $("#address").val(),
                dataType: 'text',
                success: function (data) {
                    if (data == "suc") {
                        alert("注册成功！");
                    } else {
                        alert("注册失败！");
                    }
                },
                error: function (data) {
                    alert("注册错误！");
                }

            });
        }
    </script>
</head>

<body>
<h1>用户注册</h1>
<div id="producer">
    用户姓名：<input type="text" id="name" value="Mark"/>
    <br>
    用户邮件：<input type="text" id="email" value="Mark@xiangxue.com"/>
    <br>
    用户手机：<input type="text" id="number" value="1234567890"/>
    <br>
    用户地址：<input type="text" id="address" value="享学公司"/>
    <br>
    <button onclick="send()">注 册</button>
    <br>
</div>
</body>
</html>
