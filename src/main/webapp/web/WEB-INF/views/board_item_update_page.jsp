<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="true"%>
<html>
<head>
<title>게시판 수정 페이지</title>
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/resources/css/board_item_update_page.css" />
</head>

<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<c:set var="userIdSess" value="${userIdSess}" />
<c:set var="map" value="${map}" />
<c:set var="replyListMother" value="${replyListMother}" />
<c:set var="replyListChild" value="${replyListChild}" />

<body>

	<div id="container">
	<!-- <form enctype="multipart/form-data" id="updateForm" name="updateForm" > -->
		<p id="info">게시판 수정 페이지</p>
		 <span><button id="toList" > 전체페이지 가기 버튼</button></span>
		 <input id="id" name="id" value=${map.get('id')} type="hidden" readonly="readonly" /> 
		 <input id="userId" name="userId" value=${map.get("userId")} type="hidden" readonly="readonly" />
		 <input type="hidden" readOnly="readOnly" id="writerName" name="writerName" value="${map.get('writerName')}" />
		 <input type="hidden" readOnly="readOnly" id="updatedWhen" name="updatedWhen" value='${map.get("updatedWhen")}'/>
		 <input id="pageNum" name="pageNum" value=${map.get('pageNum')} type="hidden" readonly="readonly" />	
		<table id="table" border="1">
			<tr>
				<th class="items">작성자</th>
				<td class="itemsContent" id="writer" >
				${map.get('writerName')}</td>
			</tr>
			<tr>
				<th class="items">작성일시</th>
				<td class="itemsContent" id="update">${map.get("updatedWhen")}</td>
			</tr>
			<tr>
				<th class="items">제목</th>
				<td class="itemsContent" id="tit">
				<textarea id="title" name="title" required="required"  maxlength="200">${map.get('title')}</textarea></td>
			</tr>
			<tr>
				<th class="items">내용</th>
				<td class="itemsContent" id="con">
					<textarea id="content" name="content" required="required" maxlength="716800">${map.get("content")}</textarea>
				</td>
			</tr>
			<tr>
				<th class="items"><p>첨부파일 여부</p></th>
				
				<td class="itemsContent" id="multiFile">
				<div id="preview">	
					<c:forEach var="fileItems" items="${multiFileNameList}" varStatus="status">
						<div>
						 	<p class=fileLink id="${status.index}"> ${fileItems.fileName}     <button data-index='${status.index}' value="${ fileItems.fileMeta}" class='file-remove'>X</button></p>
						</div> 
					</c:forEach>
				 </div>
				</td>
			</tr>
		</table>
		<div class="buttonCover">
			<input type="file" id="file-input" value="파일 버튼" />
		</div>
		<input type="hidden" name="compareId" id="compareId"
			   value="${userIdSess}" />
		<div id="buttonCover">
			<input type="button" value="글 수정 버튼" id="updateBtn" />
		</div>


		<div id="replyContainer" style="width: 50%">

			<div id="replyHere">
				<c:forEach var="replyMother" items="${replyListMother}" varStatus="status">
					<div class="reReplyResult parentResult">
						<div class='replyResultParentReplyIdAll'
							 style="visibility: hidden">${replyMother.parentReplyId} </div>
						작성자 :<div class='replyResultWriterName'
								  style="border:darkcyan;">${replyMother.writerName}</div>
						<br>
						콘텐츠 :<div class='replyResultContent'
									   style="width: 709px; border:darkcyan;">${replyMother.content} </div>

					</div>
					<div class="replyToReplyHere childResult" style="margin-left: 50px">
						<c:forEach var="replyChild" items="${replyListChild}" varStatus="status2">
							<c:if test="${(replyChild.parentReplyId eq replyMother.parentReplyId )&& replyChild.childReplyId ne 0 }">
								작성자:<div class='replyResultWriterName'  style="border:darkcyan;">${replyChild.writerName}</div>
								<br>
								콘텐츠:<div class='replyResultContent'  style="width: 709px;  border:darkcyan;"> ${replyChild.content}</div >
							</c:if>
						</c:forEach>
					</div>
				</c:forEach>
			</div>
		</div>
	</div>
</body>
<script src="/resources/js/board_item_file_upload_in_update_with_file.js"></script>
</html>
