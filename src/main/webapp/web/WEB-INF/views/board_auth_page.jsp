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
<script type="text/javascript" charset="utf-8">
	$(function(){
		$("#authButton").click(function(){
			let data = {
						  "userId":$("#userId").val(),
						  "userPw":$("#userPw").val()
						};
				
			makeAjax("/authExecute" , "POST" ,data , "application/json" );
		});


		$("#authCreate").click(function(){
			let data = {
				"userId":$("#userId").val(),
				"userPw":$("#userPw").val()
			};

			makeAjax("/authCreate" , "POST" ,data , "application/json" );
		});


		let	makeAjax=function (url, type, data, contentType){
			$.ajax({
			      url:url,
			      type:type,
			      data:JSON.stringify(data), 
			      contentType: contentType,
			      success: function(data) {
			          console.log(data);
			          if(data.result.code===200){
			       		location.href = "/board/showAllList?pageNum=1&amount=10"; 	  
			          }else if(data.result.code===400){
			        	  alert("아이디가 없거나 정보가 일치 하지 않습니다.");
			          }
			          //추가추가
			      },
			      error: function() {
			    	 alert("에러 입니다. 관리자에게 문의하세요"+"admin@mail.com")
			      }
			  });
			
		} //endOfAjax;
		
	});
</script>

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
