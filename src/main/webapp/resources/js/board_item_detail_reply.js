$("#replyBtn").click(function () {
    let id = Number($('#id').val());
    let userId = $('#userId').val();
    let writerName = $('#replyWriterName').val();
    let content = $('#replyContent').val();
    let whichBtn = "init"
    let data = {
        "id": id,
        "userId": userId,
        "writerName": writerName,
        "content": content,
        "whichBtn": whichBtn,
    }
    makeAjaxReply("/board/makeReply", "POST", data, "application/json");
});

const unfoldReplyToWriteWindow = document.querySelectorAll(".unfoldReplyWindowBtn");
for (let i = 0; i < unfoldReplyToWriteWindow.length; i++) {
    const btn = unfoldReplyToWriteWindow[i];
    btn.onclick = () => {
        const initReReply = document.getElementsByClassName("initReReply");
        const replyToReplyHere = document.getElementsByClassName("replyToReplyHere");
        //for(let j=0; j<reReplyWindow.length; j++) {
        initReReply.item(i).style.display = "block";
        //}
    };
}


const foldReplyNotToWriteWindowBtn = document.querySelectorAll(".foldReplyWindowBtn");
for (let i = 0; i < foldReplyNotToWriteWindowBtn.length; i++) {
    const btn = foldReplyNotToWriteWindowBtn[i];
    btn.onclick = () => {
        const reReplyWindow = document.getElementsByClassName("initReReply");
        if (reReplyWindow.item(i).style.display === "block") {
            reReplyWindow.item(i).style.display = "none"
        }
    }
}

$(".updateReply").each(function (index, item) {
    $($(".updateReply")[index]).click(function () {
        $($("input[name=replyResultWriterName]")[index]).removeAttr("readOnly");
        $($("textarea[name=replyResultContent]")[index]).removeAttr("readOnly");
        $($("input[name=replyResultWriterName]")[index]).css("border", "");
        $($("textarea[name=replyResultContent]")[index]).css("border", "");

    })
})

$(".initReReply").each(function (index, item) {

    $($(".makeReReplyBtn")[index]).click(function (e) {

        let id = Number($('#id').val());
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
$(".parentResult input[name=replyResultWriterName]").each(function (index, item) {


    $($(".sendUpdateReplyParent")[index]).click(function () {

        let id = Number($('#id').val());
        let parentReplyId = $($(".parentResult input[name=parentReplyId]").eq(index)).val();
        let childReplyId = $($(".parentResult input[name=childReplyId]").eq(index)).val();
        let writerName = $($(".parentResult input[name=replyResultWriterName]").eq(index)).val();
        let content = $($(".parentResult textarea[name=replyResultContent]").eq(index)).val();

        let data = {
            "id": id,
            "parentReplyId": parentReplyId,
            "childReplyId": childReplyId,
            "writerName": writerName,
            "content": content,
        }

        makeAjaxReply("/board/updateReply", "POST", data, "application/json");
    });
})


$(".childResult input[name=replyResultWriterName]").each(function (index, item) {


    $($(".sendUpdateReplyChild")[index]).click(function () {

        let id =Number($('#id').val());
        let parentReplyId = $($(".childResult input[name=parentReplyId]").eq(index)).val();
        let childReplyId = $($(".childResult input[name=childReplyId]").eq(index)).val();
        let writerName = $($(".childResult input[name=replyResultWriterName]").eq(index)).val();
        let content = $($(".childResult textarea[name=replyResultContent]").eq(index)).val();

        let data = {
            "id": id,
            "parentReplyId": parentReplyId,
            "childReplyId": childReplyId,
            "writerName": writerName,
            "content": content,
        }

        makeAjaxReply("/board/updateReply", "POST", data, "application/json");
    });
})

let makeAjaxReply = function (url, type, data, contentType) {
    $.ajax({
        url: url,
        type: type,
        data: JSON.stringify(data),
        contentType: contentType,
        success: function (result) {

            if (result.code === 200) {
                alert("댓글 갱신에 성공 했습니다.")
                location.reload();
            }
        },
        error: function (error) {
            alert(error.responseJSON.resDescription);
        }
    });

} //endOfAjax;
