/**
 * 
 */
 
 
const formDataSend = new FormData();
const formDataDel = new FormData();
const realSendFormDataWithFile = new FormData();

let formDataUploadTime = new FormData();
 const handler = {
    init() {
	    
        const fileInput = document.querySelector('#file-input');
        const preview = document.querySelector('#preview');
       
        fileInput.addEventListener('change', () => {
        
	       	 if( preview.innerText ==="파일 없음"){
	       		    let noFile=preview.innerText ;
	       	 		preview.innerText=noFile.replace(noFile,"");
	       	 }
	       	 
	       	let uplodeTime = Date.now()+".000"
	        
            const files = Array.from(fileInput.files)
           
            files.forEach(file => {
                preview.innerHTML += `
                <p id="${file.lastModified}" class="files">
                    ${file.name}
                    <button data-index='${file.lastModified}'  class='file-remove'>X</button>
                </p>`;
                
                formDataUploadTime.append(file.lastModified,uplodeTime);
                
                formDataSend.append(file.lastModified,file)
                
            });

        });
        
	    	
    },
	 removeFile: () => {

	        document.addEventListener('click', (e) => {
	        
		        if(e.target.className !== 'file-remove') return;
		        let removeTargetId = e.target.dataset.index;
		        const removeTarget = document.getElementById(removeTargetId);
		        const files = document.querySelector('#file-input').files;
		        const dataTransfer = new DataTransfer();
		
		        
		        Array.from(files)
		            .filter(file => file.lastModified !== removeTargetId)
		            .forEach(file => {
		             dataTransfer.items.add(file);
		         });
		         
		       
		         
		        document.querySelector('#file-input').files = dataTransfer.files;
					
				let delId = removeTargetId;	
		        removeTarget.remove(delId);
		        
		        formDataSend.delete(delId);
		        formDataUploadTime.delete(delId);
		        
		        let fileMeta = e.target.value;
		        formDataDel.append('deleteFileCondition',fileMeta);

		    })
    
    },

}
	

 handler.init();
 handler.removeFile();

  $(function(){

    $('#updateBtn').on("click", function (){
    
    	  const textData=  {
    	    	"writerName":$('#writerName').val(),
    	    	"updatedWhen":$('#updatedWhen').val(),
    	    	"title":$('#title').val(),
    	    	"content":$('#content').val(),
    	    	"id":$('#id').val(),
    	    	"userId":$('#userId').val(),
    	  	
    	  }
          
         Object.entries(textData).forEach(item => realSendFormDataWithFile.append(item[0] , item[1])); 
        
        //updateFiles
        
        formDataSend.forEach((key,value) => {
              								 realSendFormDataWithFile.append('updateFiles',key); 
      										});
     
     
     	formDataUploadTime.forEach((key,value) => {
     	 	
      										 realSendFormDataWithFile.append('uploadTime',key); 
      										});
       
    	formDataDel.forEach((key,value) => {
      										 realSendFormDataWithFile.append('deleteFileCondition',key); 
      										});	 
    
	     
    	     $.ajax({             
    	        url: "/board/updateBoardDataWithFile",  
    	        type : "POST",
    	        data :  realSendFormDataWithFile,  
    	        contentType:false,
				processData:false,
    	        success: function (result) { 
		        	if(result.code===200){
		        		alert("글 작성이 완료되었습니다.");
		        		
		        		let pageNum=$("#pageNum").val();
		        		
		        		location.href='/board/showAllList?pageNum='+pageNum;
		        	}
    	        },          
    	        error: function (error) {

					//400 에러만 처리 나중에 코드가 생길시 더 추가
					alert(error.responseJSON.resDescription);

    	         }     
    		});
    	    
    	});
    });
