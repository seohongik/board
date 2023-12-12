$(function () {

    let hasFile = document.querySelectorAll(".fileLink");
    let preveiwFiles = document.querySelector("#previewFiles");

    if (hasFile.length === 0) {

        let noFile = preveiwFiles.innerText;
        preveiwFiles.innerText = noFile.replace(noFile, "파일 없음");
    }


    $("#deleteBtn").click(function () {
        if ($("#userId").val() === $("#compareId").val()) {
            let writerName = $('#writerName').val();
            let updatedWhen = $('#updatedWhen').val();
            let title = $('#title').val();
            let id = Number($('#id').val());
            let userId = $('#userId').val();

            let data = {
                "writerName": writerName,
                "updatedWhen": updatedWhen,
                "title": title,
                "userId": userId,
                "id": id
            }
            if (confirm("삭제하시겠습니까") === true) {
                makeAjax("/board/deleteBoardData", "POST", data, "application/json");
            } else {
                let id = $("#id").val();


                location.href = "/board/detail/" + id;
            }
        } else {
            alert("권한 없음");
        }

    });

    $("#toUpdateBtn").click(function () {
        if ($("#userId").val() === $("#compareId").val()) {
            let id = Number($("#id").val());
            location.href = "/board/toUpdateFromDetail/" + id
        }//endOfIf
        else {
            alert("권한 없음");
        }
    });//endOfclick

    $("#toList").click(function () {
        let pageNum = $('#pageNum').val();
        location.href = '/board/showAllList?pageNum=' + pageNum
    });//endOfclick

})

let makeAjax = function (url, type, data, contentType) {
    $.ajax({
        url: url,
        type: type,
        data: JSON.stringify(data),
        contentType: contentType,
        success: function (result) {
            console.log(result);
            if (result.code === 200) {

                let pageNum = $('#pageNum').val();
                location.href = "/board/showAllList?pageNum=" + pageNum;

            }
            //추가추가
        },
        error: function () {
            alert("에러 입니다. 관리자에게 문의하세요" + "admin@mail.com")
        }
    });

} //endOfAjax;