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