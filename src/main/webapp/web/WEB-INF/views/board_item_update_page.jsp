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
<script>

$(function(){

	
	let hasFile=document.querySelectorAll(".fileLink");
	let preveiwFiles =document.querySelector("#preview");
		
	if(hasFile.length==0){

		let noFile=preveiwFiles.innerText ;
		preveiwFiles.innerText=noFile.replace(noFile,"파일 없음");
	}
	
	$("#toList").on("click", function() {
		let pageNum =$('#pageNum').val();
		location.href='/board/showAllList?pageNum='+pageNum
	});

   /*  $('#updateBtn').click(function(){
    	  var userId=$('input[name=userId]').val();
    	  var writerName=$('input[name=writerName]').val();
    	  var updatedWhen=$('input[name=updatedWhen]').val();
    	  var title=$('#title').val();
    	  var content=$('#content').val();
    	  var id=$('#id').val();
    	  
    	  const data=  {
    			"userId":userId,
    	    	"writerName":writerName,
    	    	"updatedWhen":updatedWhen,
    	    	"title":title,
    	    	"content":content,
    	    	"id":id
    	    }
    	  makeAjax("/board/updateBoardData","POST" ,data , "application/json");
		
    });	    
    	
    
    let	makeAjax=function (url, type, data, contentType){
		$.ajax({
		      url:url,
		      type:type,
		      data:JSON.stringify(data), 
		      contentType: false,
		      success: function(result) {
		          console.log(result);
		          if(result.code===200){
		        	alert("갱신에 성공했습니다.")
		        	var id=$('#id').val();
		       		location.href = "/board/detail/"+id; 	  
		          }else if(result.code===400){
		        	  alert("아이디가 없거나 정보가 일치 하지 않습니다.");
		          }
		          //추가추가
		      },
		      error: function() {
		    	 alert("에러 입니다. 관리자에게 문의하세요"+"admin@mail.com")
		    	 
		      }
		  });
		
	} //endOfAjax; */
    
});
</script>

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

				<div  class="replyBox">

					<input class="parentReplyId" name="parentReplyId" value="${replyMother.parentReplyId}" style="visibility: hidden" />
					<div class='replyResultParentReplyIdAll' style="visibility: hidden">${replyMother.parentReplyId} </div>
					<div class='replyResultWriterName'>작성자 :${replyMother.writerName} </div>
					<div class='replyContentResultContent' >댓글 :${replyMother.content} </div>

					<div class="replyToReplyHere" style="margin-left: 50px">
						<c:forEach var="replyChild" items="${replyListChild}" varStatus="status2">
							<c:if test="${(replyChild.parentReplyId eq replyMother.parentReplyId )&& replyChild.childReplyId ne 0 }">
								<div class='replyResultWriterName'>작성자 ${replyChild.writerName}</div>
								<div class='replyContentResultContent' >댓글: ${replyChild.content}</div>
							</c:if>
						</c:forEach>
					</div>
				</div>
			</c:forEach>

		</div>
		
		<div class="buttonCover">
            <input type="file" id="file-input" value="파일 버튼" />
        </div>
		
		<input type="hidden" name="compareId" id="compareId"
			value="${userIdSess}" />
		<div id="buttonCover">
			<input type="button" value="글 수정 버튼" id="updateBtn" />
		</div>
		<!--  </form> -->
	</div>
</body>
<script src="/resources/js/board_item_file_upload_in_update_with_file.js"></script>
</html>
