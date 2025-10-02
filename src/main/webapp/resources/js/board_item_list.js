/**
 * 
 */
 
 $(function(){
	let hasFile =document.querySelectorAll('.hasFile');
	for(let i=0; i<hasFile.length; i++){
		if(hasFile[i].textContent==='Y'){
			  hasFile[i].style.color ='red'
		}
	}
});
