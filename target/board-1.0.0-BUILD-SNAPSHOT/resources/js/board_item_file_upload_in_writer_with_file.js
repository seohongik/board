
//let fileId=0; 
let formDataSend = new FormData();
let formDataDel = new FormData();
let formDataUploadTime = new FormData();
 const handler = {
    init() {
        const fileInput = document.querySelector('#file-input');
        const preview = document.querySelector('#preview');
        
        fileInput.addEventListener('change', () => {  
            
            let uploadTime = (Date.now()+".000");
            
            console.log(uploadTime);
           
                      
            const files = Array.from(fileInput.files)
            files.forEach(file => {
            
                formDataUploadTime.append(file.lastModified, uploadTime);
             
                preview.innerHTML += `
                <p id="${file.lastModified}">
                    ${file.name}
                    <button data-index='${file.lastModified}' class='file-remove'>X</button>
                </p>`;
                
                
                formDataSend.append(file.lastModified,file);
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
            .filter(file => file.lastModified != removeTargetId)
            .forEach(file => {
             dataTransfer.items.add(file);
         });
         
        
		let lastModified = removeTargetId; 
        formDataDel.append('deleteFiles',lastModified);

		formDataUploadTime.delete(lastModified);

        document.querySelector('#file-input').files = dataTransfer.files;
		formDataSend.delete(formDataDel.getAll('deleteFiles'));
		
		

        removeTarget.remove(lastModified);
    })
    
    },

}

 handler.init();
 handler.removeFile();

  $(function(){

    $('#submitbtn').on("click",function(e){
                 
          const realSendFormDataWithFile = new FormData();
    	 
    	  const textData=  {
    	    	"writerName":$('#writerName').val(),
    	    	"updatedWhen":$('#updatedWhen').val(),
    	    	"title":$('#title').val(),
    	    	"content":$('#content').val(),
    	    	"id":$('#id').val(),
    	    	"userId":$('#userId').val()
    	  }
          
          Object.entries(textData).forEach(item => realSendFormDataWithFile.append(item[0] , item[1])); /*textDataAdd*/
          
          const objFileData={};
          
          formDataSend.forEach((value, key) => {objFileData[key] = value
           									   realSendFormDataWithFile.append('file',value);
          });
          
          formDataUploadTime.forEach((value, key) => {objFileData[key] = value
           									   realSendFormDataWithFile.append('uploadTime',value);
          });
          
            /*fileDataAdd*/
    	    
    	    
    	       $.ajax({             
    	        url: "/board/insertBoardDataWithFile",  
    	        type : "POST",
    	        data :  realSendFormDataWithFile,  
    	        contentType : false,
        		processData : false,
    	        success: function (result) { 
    	        
		        	if(result.code==200){
		        		alert("글 작성이 완료되었습니다.");
		        		location.href='/board/showAllList?pageNum=1';
		        	}else if(result.code==400){
		        		alert('입력값 항목을 확인해 주세요');
		        	}
    	        },          
    	        error: function (e) {  
    	        	console.log("ERROR : ", e);
    	        	alert('관리자에게 문의 해주세요 admin@mail.com')
    	         }     
    		});
    	    
    	});
    
    });