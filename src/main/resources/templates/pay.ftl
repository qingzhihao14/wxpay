<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <script src="/static/qrcode.js"></script>
    <script src="https://cdn.bootcss.com/jquery/3.4.1/jquery.js"></script>
</head>
<center>
    <div id="qrcode"></div>
</center>
<script type="text/javascript">
    new QRCode(document.getElementById("qrcode"), "${map.code_url}");  // 设置要生成二维码的链接
</script>
<script type="text/javascript">
    var int=self.setInterval("querystatus()",3000);
    function querystatus() {
        $.get("/wxpay/queryorder/${map.no}",function(data,status){
            if (data==="支付中"){
                console.log("支付中");
            } else {
                clearInterval(int)
                window.location.href="/wxpay/success"
            }
        })
    }
</script>
</body>
</html>