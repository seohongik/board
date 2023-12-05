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
<c:set var="replyListMother" value="${replyListMother}" />
<c:set var="replyListChild" value="${replyListChild}" />


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



		$("#deleteBtn").click(function() {
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
				if(confirm("삭제하시겠습니까")===true){
					makeAjax("/board/deleteBoardData", "POST", data,"application/json");
				}else{
					let id = $("#id").val();


					location.href ="/board/detail/"+id;
				}
			} else {
				alert("권한 없음");
			}

		});

		$("#toUpdateBtn").click( function() {
			if ($("#userId").val() === $("#compareId").val()) {
				let id = $("#id").val();
				location.href = "/board/toUpdateFromDetail/" + id
			}//endOfIf
			else {
				alert("권한 없음");
			}
		});//endOfclick

		$("#toList").click( function() {
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

					let pageNum =$('#pageNum').val();
					location.href = "/board/showAllList?pageNum="+pageNum;

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

	<table id="boardtable" border="1">
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
				<textarea readonly="readonly" id="title" class="boardTextWindow" >${map.get("title")}</textarea>
			</td>
		</tr>
		<tr>
			<th class="items">내용</th>
			<td class="itemsContent" id="content">
				<textarea id="contentArea" readonly="readonly" class="boardTextWindow">${map.get("content")}</textarea>
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
		<input type="button" value="글 수정 하러 가기 버튼" id="toUpdateBtn" />
		<input type="button" value="글 삭제 버튼" id="deleteBtn" />
	</div>
	<div>
		<table>
			<tr>
				<td>
					<label for="replyWriterName" > 댓글 작성자</label>
					<input id="replyWriterName" name="replyWriterName" />
					<br>
					<label for="replyContent">댓글 내용</label>
					<textarea id="replyContent" name="replyContent" style="width: 709px; height: 104px;"> </textarea>
				</td>
			</tr>
		</table>
		<input type="button" value="댓글 달기" id="replyBtn"/>
	</div>


	<div id="replyHere">

		<c:forEach var="replyMother" items="${replyListMother}" varStatus="status">
			<div  class="reReplyResult parentResult">
				<input class="parentReplyId" name="parentReplyId" value="${replyMother.parentReplyId}" style="visibility: hidden" />
				<input class="childReplyId" name="childReplyId" value="${ replyMother.childReplyId}"  style="visibility: hidden"/>
				<div class='replyResultParentReplyIdAll' style="visibility: hidden">${replyMother.parentReplyId} </div>
				작성자 :<input class='replyResultWriterName' name="replyResultWriterName" readonly="readonly" value="${replyMother.writerName}"/>
				<textarea class='replyResultContent' name="replyResultContent" readonly="readonly">${replyMother.content} </textarea>
				<button class="unfoldReplyWindowBtn"> 답글 창 열기</button>
				<button class="foldReplyWindowBtn"> 답글 창 접기</button>
				<button class="removeReply"  onclick="function removeReply(){location.href='/board/removeReply?id=${replyMother.id}&parentReplyId=${replyMother.parentReplyId}&childReplyId='}removeReply()">댓글 삭제</button>

				<br>
				<button class="updateReply">수정하러가기</button>
				<button class="sendUpdateReplyParent">부모 댓글 수정</button>
			</div>
			<div class="replyToReplyHere childResult" style="margin-left: 50px">
				<c:forEach var="replyChild" items="${replyListChild}" varStatus="status2">
					<c:if test="${(replyChild.parentReplyId eq replyMother.parentReplyId )&& replyChild.childReplyId ne 0 }">
						<input class="parentReplyId" name="parentReplyId" value="${ replyChild.parentReplyId}"  style="visibility: hidden"/>
						<input class="childReplyId" name="childReplyId" value="${ replyChild.childReplyId}"  style="visibility: hidden"/>
						작성자:<input class='replyResultWriterName' name="replyResultWriterName" readonly="readonly"  value="${replyChild.writerName}">
						<textarea class='replyResultContent' name="replyResultContent" readonly="readonly"> ${replyChild.content}</textarea>
						<button class="removeReply" onclick="function removeReply(){location.href='/board/removeReply?id=${replyChild.id}&parentReplyId=${replyChild.parentReplyId}&childReplyId=${replyChild.childReplyId}'}removeReply()" >댓글 삭제</button>
						<button class="updateReply">수정하러가기</button>
						<button class="sendUpdateReplyChild">자식 댓글 수정</button>
					</c:if>

				</c:forEach>

			</div>

			<div class="initReReply" style="display:none;">
				<input class="parentReplyId" name="parentReplyId" value="${replyMother.parentReplyId}"  style="visibility: hidden"/>
				<input class="childReplyId" name="childReplyId" value="${replyMother.childReplyId}"  style="visibility: hidden"/>
				<label >댓글 작성자</label>
				<input class="replyResultWriterName" name='replyResultWriterName'/>
				<br>
				<label >댓글 내용</label>
				<textarea class="replyResultContent" name='replyResultContent' style='width: 709px; height: 104px;'> </textarea>
				<input type='button' class='makeReReplyBtn' value='댓댓글 달기' >
			</div>
		</c:forEach>



	</div>


</div>
<script>
	$("#replyBtn").click( function() {
		let id = $('#id').val();
		let userId = $('#userId').val();
		let writerName = $('#replyWriterName').val();
		let content =  $('#replyContent').val();
		let whichBtn = "init"

		let data = {
			"id" : id,
			"userId": userId,
			"writerName" : writerName,
			"content" : content,
			"whichBtn":whichBtn,
		}

		makeAjaxReply("/board/makeReply", "POST", data,"application/json");

	});

	const unfoldReplyToWriteWindow=document.querySelectorAll(".unfoldReplyWindowBtn");

	for(let i=0; i<unfoldReplyToWriteWindow.length; i++) {

		const btn = unfoldReplyToWriteWindow[i];
		btn.onclick = () => {

			const initReReply=document.getElementsByClassName("initReReply");
			const replyToReplyHere=document.getElementsByClassName("replyToReplyHere");

			//for(let j=0; j<reReplyWindow.length; j++) {

			initReReply.item(i).style.display = "block";

			//}

		};
	}


	const foldReplyNotToWriteWindowBtn=document.querySelectorAll(".foldReplyWindowBtn");

	for(let i=0; i<foldReplyNotToWriteWindowBtn.length; i++){

		const btn = foldReplyNotToWriteWindowBtn[i];
		btn.onclick=()=> {

			const reReplyWindow=document.getElementsByClassName("initReReply");

			if(reReplyWindow.item(i).style.display === "block"){

				reReplyWindow.item(i).style.display="none"
			}

		}
	}

	$(".updateReply").click(function (){

		$("input[name=replyResultWriterName]").removeAttr("readOnly");
		$("textarea[name=replyResultContent]").removeAttr("readOnly");

	})


	$(".initReReply").each(function (index,item){

		$($(".makeReReplyBtn")[index]).click(function (e){

				let id = $("#id").val();
				let userId = $('#userId').val();
				let parentReplyId = $($(".initReReply input[name=parentReplyId]").eq(index)).val();
				let childReplyId = $($(".initReReply input[name=childReplyId]").eq(index)).val();
				let writerName = $($(".initReReply input[name=replyResultWriterName]").eq(index)).val();
				let content = $($(".initReReply textarea[name=replyResultContent]").eq(index)).val();
				let whichBtn = "reReply"
				let data = {
					"id": id,
					"parentReplyId": parentReplyId,
					"childReplyId": childReplyId,
					"writerName": writerName,
					"content": content,
					"whichBtn": whichBtn,
					"userId": userId,
				}

				makeAjaxReply("/board/makeReply", "POST", data, "application/json");
		})
	});
	$(".parentResult input[name=replyResultWriterName]").each(function (index,item){


		$($(".sendUpdateReplyParent")[index]).click(function (){

			let id = $("#id").val();
			let parentReplyId = $($(".parentResult input[name=parentReplyId]").eq(index)).val();
			let childReplyId =  $($(".parentResult input[name=childReplyId]").eq(index)).val();
			let writerName = $($(".parentResult input[name=replyResultWriterName]").eq(index)).val();
			let content = $($(".parentResult textarea[name=replyResultContent]").eq(index)).val();

			let data = {
				"id":id,
				"parentReplyId":parentReplyId,
				"childReplyId":childReplyId,
				"writerName":writerName,
				"content":content,
			}

			makeAjaxReply("/board/updateReply", "POST", data, "application/json");
		});
	})


	$(".childResult input[name=replyResultWriterName]").each(function (index,item){


		$($(".sendUpdateReplyChild")[index]).click(function (){

			let id = $("#id").val();
			let parentReplyId = $($(".childResult input[name=parentReplyId]").eq(index)).val();
			let childReplyId =  $($(".childResult input[name=childReplyId]").eq(index)).val();
			let writerName = $($(".childResult input[name=replyResultWriterName]").eq(index)).val();
			let content = $($(".childResult textarea[name=replyResultContent]").eq(index)).val();

			let data = {
				"id":id,
				"parentReplyId":parentReplyId,
				"childReplyId":childReplyId,
				"writerName":writerName,
				"content":content,
			}

			makeAjaxReply("/board/updateReply", "POST", data, "application/json");
		});
	})

	let makeAjaxReply = function(url, type, data,contentType) {
		$.ajax({
			url : url,
			type : type,
			data : JSON.stringify(data),
			contentType : contentType,
			success : function(result) {

				if (result.code === 200) {
					alert("댓글 갱신에 성공 했습니다.")
					location.reload();
				}
			},
			error : function() {
				alert("에러 입니다. 관리자에게 문의하세요"+ "admin@mail.com")
			}
		});

	} //endOfAjax;
</script>
</body>
</html>