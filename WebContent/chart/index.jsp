<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<title>Document</title>
</head>
<body>
	<canvas id="chart" width="800" height="400"></canvas>
</body>
<script src="chart/bower_components/jquery/dist/jquery.min.js"></script>
<script src="chart/bower_components/Chart.js/Chart.min.js"></script>
<script>
var data = <c:out value="${data}" escapeXml="false"></c:out>;
function drawLine(label, data1, data2){
	var data = {
    labels: label,
    datasets: [
               {
                   label: "My First dataset",
                   fillColor: "rgba(28,132,198,0.3)",
                   strokeColor: "rgba(28,132,198,1)",
                   pointColor: "rgba(28,132,198,1)",
                   pointStrokeColor: "#fff",
                   pointHighlightFill: "#fff",
                   pointHighlightStroke: "rgba(220,220,220,1)",
                   data: data1
               },
               {
                   label: "My Second dataset",
                   fillColor: "rgba(198, 114, 28, 0.3)",
                   strokeColor: "rgba(198, 114, 28, 1)",
                   pointColor: "rgba(198, 114, 28, 1)",
                   pointStrokeColor: "#fff",
                   pointHighlightFill: "#fff",
                   pointHighlightStroke: "rgba(151,187,205,1)",
                   data: data2
               }
           ]
	};
	var ctx = document.getElementById("chart").getContext("2d");
	var myChart = new Chart(ctx).Line(data);
}

drawLine(data["label"], data["data1"], data["data2"])

</script>

<br>蓝色是微博热度，橙色是百度热度

</html>