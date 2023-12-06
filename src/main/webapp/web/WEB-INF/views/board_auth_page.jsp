<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<title>인증 페이지</title>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/board_auth_page.css"  />
</head>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>

<body>
    <div id="container">
        <p id="info"> 인증 페이지 </p>
      	<div id="inner">
            <label for="userId"  >아이디</label>
            <input type="text" class="authInput" id="userId"/>
            <label for="userPw" >바밀번호</label>
            <input type="password" class="authInput" id="userPw"/>
            <input type="button" class="authButton" id="authButton" value="인증 버튼"/>
            <input type="button" class="authButton" id="authCreate" value="생성 버튼"/>
        </div>
    </div>   
</body>

<script src="/resources/js/board_auth.js"></script>
</html>