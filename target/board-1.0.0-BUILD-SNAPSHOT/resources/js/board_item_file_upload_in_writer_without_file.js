/**
 * 
 */
$(function(){

    $('#submitbtn').click(function(){
    	  
    	  var writerName=$('input[name=writerName]').val();
    	  var updatedWhen=$('input[name=updatedWhen]').val();
    	  var title=$('input[name=title]').val();
    	  var content=$('#content').val();
    	  
    	  const data=  {
    	    	"writerName":writerName,
    	    	"updatedWhen":updatedWhen,
    	    	"title":title,
    	    	"content":content
    	    }
    	  makeAjax("/board/insertBoardData","POST" ,data , "application/json");
		
    });	    
    	
    
    let	makeAjax=function (url, type, data, contentType){
		$.ajax({
		      url:url,
		      type:type,
		      data:JSON.stringify(data), 
		      contentType: contentType,
		      success: function(result) {
		          console.log(result);
		          if(result.code===200){
		       		location.href = "/board/showAllList?pageNum=1&amount=10"; 	  
		          }else if(result.code===400){
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
