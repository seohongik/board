<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="true"%>
<html>
<head>
<title>게시판 상세 내용 페이지</title>
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/resources/css/board_item_detail_page.css" />
</head>

<c:set var="userIdSess" value="${userIdSess}" />
<c:set var="map" value="${map}" />
<c:set var="multiFileNameList" value="${multiFileNameList}" />

<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script>

$(function() {
		
	let hasFile=document.querySelectorAll(".fileLink");
	let preveiwFiles =document.querySelector("#previewFiles");
		
	if(hasFile.length==0){

		let noFile=preveiwFiles.innerText ;
		preveiwFiles.innerText=noFile.replace(noFile,"파일 없음");
	}
	
	
	$("#deleteBtn").on("click",function() {
		if ($("#userId").val() === $("#compareId").val()) {
					let writerName = $('#writerName').val();
					let updatedWhen = $('#updatedWhen').val();
					let title = $('#title').val();
					let id = $('#id').val();
					let userId = $('#userId').val();

					let data = {
						"writerName" : writerName,
						"updatedWhen" : updatedWhen,
						"title" : title,
						"userId" : userId,
						"id" : id
					}
				    makeAjax("/board/deleteBoardData", "POST", data,"application/json");

				} else {
					alert("권한 없음");
				}

			});

	$("#toUpdateBtn").on("click", function() {
		if ($("#userId").val() === $("#compareId").val()) {
			let id = $("#id").val();
			location.href = "/board/toUpdateFromDetail/" + id
		}//endOfIf
		else {
			alert("권한 없음");
		}
	});//endOfclick
	
	$("#toList").on("click", function() {
		let pageNum =$('#pageNum').val();
		location.href='/board/showAllList?pageNum='+pageNum
	});//endOfclick
	
		
})
		

	
	let makeAjax = function(url, type, data,contentType) {
	        $.ajax({
					url : url,
					type : type,
					data : JSON.stringify(data),
					contentType : contentType,
					success : function(result) {
						console.log(result);
						if (result.code === 200) {
							if(confirm("삭제하시겠습니까")){
								let pageNum =$('#pageNum').val();
								location.href = "/board/showAllList?pageNum="+pageNum;
							}else{
								
							}
						} 
						//추가추가
					},
					error : function() {
						alert("에러 입니다. 관리자에게 문의하세요"+ "admin@mail.com")
					}
				});

	} //endOfAjax;
</script>
<body>
	<div id="container">
		<p id="info">게시판 상세 내용 페이지</p>
		<span><button id="toList" > 전체페이지 가기 버튼</button></span>
		
		<input id="id" name="id" value=${map.get('id')} type="hidden" readonly="readonly" />
		<input id="pageNum" name="pageNum" value=${map.get('pageNum')} type="hidden" readonly="readonly" />	
		<input id="userId" name="userId" value=${map.get("userId")} type="hidden" readonly="readonly" />

		<table id="table" border="1">
			<tr>
				<th class="items">작성자</th>
				<td class="itemsContent" id="writer">
					<p id="writerName">${map.get("writerName")}</p>
				</td>
			</tr>
			<tr>
				<th class="items">작성일시</th>
				<td class="itemsContent" id="updated">
					<p id="updatedWhen"  >${map.get("updatedWhen")}</p>
				</td>
			</tr>
			<tr>
				<th class="items">제목</th>
				<td class="itemsContent" id="tit">
					<textarea readonly="readonly" id="title" >${map.get("title")}</textarea>
				</td>
			</tr>
			<tr>
				<th class="items">내용</th>
				<td class="itemsContent" id="content">
					<textarea id="contentArea" readonly="readonly" >${map.get("content")}</textarea>
				</td>
			</tr>

			<tr>
				<th class="items"><p>첨부파일 여부</p></th>
				<td class="itemsContent" id="previewFiles" >
				
					<c:forEach var="fileItems" items="${multiFileNameList}" varStatus="status">
						<div class="preview">
							<a class="fileLink" href="/board/downloadFile/${map.get('id')}?userId=${map.get('userId')}&fileMeta=${fileItems.fileMeta}">${fileItems.fileName}</a>
						</div>
					</c:forEach>
				</td>
			</tr>
		</table>
		<input type="hidden" name="compareId" id="compareId" value="${userIdSess}" />
		<div id="buttonCover">
			<input type="button" value="글 수정 버튼" id="toUpdateBtn" /> 
			<input type="button" value="글 삭제 버튼" id="deleteBtn" />
		</div>
	</div>
</body>
</html>