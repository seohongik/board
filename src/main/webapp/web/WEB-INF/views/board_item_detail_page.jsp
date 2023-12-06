<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="true" %>
<html>
<head>
    <title>게시판 상세 내용 페이지</title>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/resources/css/board_item_detail_page.css"/>
</head>

<c:set var="userIdSess" value="${userIdSess}"/>
<c:set var="map" value="${map}"/>
<c:set var="multiFileNameList" value="${multiFileNameList}"/>
<c:set var="replyListMother" value="${replyListMother}"/>
<c:set var="replyListChild" value="${replyListChild}"/>


<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<body>
<div id="container">
    <p id="info">게시판 상세 내용 페이지</p>
    <span><button id="toList"> 전체페이지 가기 버튼</button></span>

    <input id="id" name="id" value=${map.get('id')} type="hidden" readonly="readonly"/>
    <input id="pageNum" name="pageNum" value=${map.get('pageNum')} type="hidden" readonly="readonly"/>
    <input id="userId" name="userId" value=${map.get("userId")} type="hidden" readonly="readonly"/>

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
                <p id="updatedWhen">${map.get("updatedWhen")}</p>
            </td>
        </tr>
        <tr>
            <th class="items">제목</th>
            <td class="itemsContent" id="tit">
                <textarea readonly="readonly" id="title" class="boardTextWindow">${map.get("title")}</textarea>
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
            <td class="itemsContent" id="previewFiles">

                <c:forEach var="fileItems" items="${multiFileNameList}" varStatus="status">
                    <div class="preview">
                        <a class="fileLink"
                           href="/board/downloadFile/${map.get('id')}?userId=${map.get('userId')}&fileMeta=${fileItems.fileMeta}">${fileItems.fileName}</a>
                    </div>
                </c:forEach>
            </td>
        </tr>
    </table>
    <input type="hidden" name="compareId" id="compareId" value="${userIdSess}"/>
    <div id="buttonCover">
        <input type="button" value="글 수정 하러 가기 버튼" id="toUpdateBtn"/>
        <input type="button" value="글 삭제 버튼" id="deleteBtn"/>
    </div>

    <div id="replyContainer" style="width: 50%">
        <div>
            <table>
                <tr>
                    <td style="width: 100px;height: 100px;">
                        <label for="replyWriterName"> 댓글 작성자</label>
                        <input id="replyWriterName" name="replyWriterName">
                        <br>
                        <label for="replyContent">댓글 내용</label>
                        <textarea id="replyContent" name="replyContent" style="width: 709px;"> </textarea>
                    </td>
                </tr>
            </table>
            <input type="button" value="댓글 달기" id="replyBtn"/>
        </div>


        <div id="replyHere">

            <c:forEach var="replyMother" items="${replyListMother}" varStatus="status">
                <div class="reReplyResult parentResult">
                    <input class="parentReplyId" name="parentReplyId" value="${replyMother.parentReplyId}"
                           style="visibility: hidden"/>
                    <input class="childReplyId" name="childReplyId" value="${ replyMother.childReplyId}"
                           style="visibility: hidden"/>
                    <div class='replyResultParentReplyIdAll'
                         style="visibility: hidden">${replyMother.parentReplyId} </div>
                    작성자 :<input class='replyResultWriterName' name="replyResultWriterName" readonly="readonly"
                                value="${replyMother.writerName}" style="border:darkcyan;"/>
                    <br>
                    콘텐츠 :<textarea class='replyResultContent' name="replyResultContent" readonly="readonly"
                                   style="width: 709px; border:darkcyan;">${replyMother.content} </textarea>
                    <button class="unfoldReplyWindowBtn"> 자식 답글 창 열기</button>
                    <button class="foldReplyWindowBtn"> 자식 답글 창 접기</button>
                    <button class="removeReply"
                            onclick="function removeReply(){location.href='/board/removeReply?id=${replyMother.id}&parentReplyId=${replyMother.parentReplyId}&childReplyId='}removeReply()">
                        댓글 삭제
                    </button>

                    <br>
                    <button class="updateReply">수정 활성화</button>
                    <button class="sendUpdateReplyParent">부모 댓글 수정</button>
                </div>
                <div class="replyToReplyHere childResult" style="margin-left: 50px">
                    <c:forEach var="replyChild" items="${replyListChild}" varStatus="status2">
                        <c:if test="${(replyChild.parentReplyId eq replyMother.parentReplyId )&& replyChild.childReplyId ne 0 }">
                            <input class="parentReplyId" name="parentReplyId" value="${ replyChild.parentReplyId}"
                                   style="visibility: hidden"/>
                            <input class="childReplyId" name="childReplyId" value="${ replyChild.childReplyId}"
                                   style="visibility: hidden"/>
							<br>
                            작성자:<input class='replyResultWriterName' name="replyResultWriterName" readonly="readonly" value="${replyChild.writerName}" style="border:darkcyan;">
                            <br>
                            콘텐츠:<textarea class='replyResultContent' name="replyResultContent" readonly="readonly" style="width: 709px;  border:darkcyan;"> ${replyChild.content}</textarea>
                            <button class="removeReply"
                                    onclick="function removeReply(){location.href='/board/removeReply?id=${replyChild.id}&parentReplyId=${replyChild.parentReplyId}&childReplyId=${replyChild.childReplyId}'}removeReply()">
                                댓글 삭제
                            </button>
                            <button class="updateReply">수정 활성화</button>
                            <button class="sendUpdateReplyChild">자식 댓글 수정</button>
                        </c:if>

                    </c:forEach>

                </div>

                <div class="initReReply" style="display:none;">
                    <input class="parentReplyId" name="parentReplyId" value="${replyMother.parentReplyId}"
                           style="visibility: hidden"/>
                    <input class="childReplyId" name="childReplyId" value="${replyMother.childReplyId}"
                           style="visibility: hidden"/>
                    <br>
                    <label>댓글 작성자</label>
                    <input class="replyResultWriterName" name='replyResultWriterName'/>
                    <br>
                    <label>댓글 내용</label>
                    <textarea class="replyResultContent" name='replyResultContent'
                              style='width: 709px; height: 89px;'> </textarea>
                    <input type='button' class='makeReReplyBtn' value='댓댓글 달기'>
                </div>
            </c:forEach>
        </div>
    </div>
</div>
<script src="/resources/js/board_item_detail_board.js"></script>
<script src="/resources/js/board_item_detail_reply.js"></script>
</body>
</html>