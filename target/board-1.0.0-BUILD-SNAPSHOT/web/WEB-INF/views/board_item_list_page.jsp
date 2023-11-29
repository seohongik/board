<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="true"%>
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>게시판 전체 페이지</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/board_item_list_page.css"  />
</head>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script src = "/resources/js/board_item_list.js"> </script>
<body>
<body>
	<div id="container">
		<form action="/board/showAllList"method="get">
			<p id="info">게시판 전체 목록 페이지</p>
			<table id="table" border="1">
				<tr>
					<th >순번</th>
					<th >작성자</th>
					<th >제목</th>
					<th >작성일시</th>
					<th >첨부 파일</th>
				</tr>

			<c:forEach var="boardCrudDTO" items="${boardCrudDTOList}">
				<tr>
					<td ><p class="rowNum">${boardCrudDTO.rowNum }</p></td>
					<td ><p class="writerName">
							${boardCrudDTO.writerName}
							</p>
					</td>
					<td ><p class="title">
						<a class="goDetailLink" href="${pageContext.request.contextPath}/board/detail/${boardCrudDTO.id}">${boardCrudDTO.title}</a>
						</p>
					</td>
					<td ><p class="updatedWhen">${boardCrudDTO.updatedWhen}</p></td>
					<td class="multiFile">
						<p class="hasFile">${boardCrudDTO.hasFile}</p>
					</td>
				</tr>
				<input type=hidden readOnly="readOnly" name='id' class='id' value="${boardCrudDTO.id}" />
				<input type=hidden readOnly="readOnly" name='userId' class='userId' value="${boardCrudDTO.userId}" />
			</c:forEach>
			</table>
			<input id="toWriteBtn" type="button" value="글쓰기" onclick="
			    function goWrite() {
				location.href='/board/toWriteFromAllList'} goWrite()">
			<div id="pageInHere" style="display: inline; width: 600px; margin: 0 auto" >
			<ul class='pagination' style="display: flex; flex-wrap: nowrap; justify-content: center;">
	
				<c:if test="${pagelist.pageMaker.prev}">
					<li style='list-style: none; padding: 0 10px;' class='paginate_button previous'>
						<a href='/board/showAllList?pageNum=${pagelist.pageMaker.startPage -1 }'>Previous</a>
					</li>
				</c:if>
				<c:forEach var='num'   begin='${pagelist.pageMaker.startPage}' end='${pagelist.pageMaker.endPage }'>
					<li  style='list-style: none'  class='paginate_button'   >
						<c:choose>
							<c:when test="${num == pagelist.pageMaker.pageNum}">
								<span style="color: red;">${num}</span>
								<input name="pageNum" value="${num}" style="display: none"/>
							</c:when>
							<c:otherwise>
								<a class="blockLink" style="padding: 0 10px;" href="/board/showAllList?pageNum=${num}&amount=${pagelist.pageMaker.amount }">${num}</a>
							</c:otherwise>
						</c:choose>
					</li>
				</c:forEach>
				<c:if test="${pagelist.pageMaker.next}">
					<li style="list-style: none; padding: 0 10px;" class="paginate_button next">
						<a href="/board/showAllList?pageNum=${pagelist.pageMaker.endPage +1 }">Next</a>
					</li>
				</c:if>
			</ul>
		</div>	
		</form>
	</div>
</body>

</html>

