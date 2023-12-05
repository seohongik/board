<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="true" %>

<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<title>게시판 글쓰기 페이지</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/board_item_writer_page.css"  />	


</head>
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<c:set var="updatedWhen" value="${updatedWhen}"/>
<body>
	<div id="container">
		<form enctype="multipart/form-data" id="insertForm" name="insertForm" >
				<p id="info"> 게시판 글쓰기 페이지 </p>
		        <table id="table" border="1">
		            <tr>
		                <td class="items" > 작성자 </td>
		                <td class="itemsContent" > <textarea id="writerName"  name="writerName" required="required" maxlength="20"></textarea></td>
		            </tr>
		            <tr>
		                <td class="items" > 작성일시 </td>
		                <td class="itemsContent" > <input type="text" id="updatedWhen"  name="updatedWhen" value="${updatedWhen}" required="required" readonly="readonly" > </td>
		            </tr>
		            <tr>
		                <td class="items" > 제목 </td>
		                <td class="itemsContent"> <textarea id="title" required="required"  name="title" maxlength="200" ></textarea></td>
		            </tr>
		            <tr>
		                <td class="items" > 내용 </td>
		                <td class="itemsContent" > <textArea id="content" required="required" maxlength="716800"> </textArea> </td>
		            </tr>
		            <tr>  
		                <td class="items" > 첨부파일 </td>
		                <td class="itemsContent" > <div id="preview" > </div> </td>
		            </tr>
		        </table>
		</form> 
		
		<div class="buttonCover">
            <input type="file" id="file-input" value="파일 버튼" />
        </div>
	   <div class="buttonCover">
			 <input type="button" value="글 등록 버튼" id="submitbtn" />
	   </div>
        	
	</div>
</body>
<script src="/resources/js/board_item_file_upload_in_writer_with_file.js"></script>
</html>
