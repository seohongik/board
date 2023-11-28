

package com.wings.board.controller;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.wings.board.dto.BoardCrudDTO;
import com.wings.board.dto.BoardPageDTO;
import com.wings.board.dto.BoardResDTO;
import com.wings.board.dto.BoardValidDTO;
import com.wings.board.service.BoardCrudService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/board")
@Slf4j
public class BoardCrudController {

    private BoardCrudService boardCrudService;

	@Autowired
	public void setBoardCrudService(BoardCrudService boardCrudService) {
		this.boardCrudService = boardCrudService;
	}

	@RequestMapping(value = "/showAllList", method = RequestMethod.GET)
    public ModelAndView showBoardListWithPaging(HttpSession httpSession,@RequestParam("pageNum") String pageNumStr,@RequestParam(value="amount",defaultValue = "10")String amountStr) {

    	ModelAndView mnv = new ModelAndView();
        
    	String session =(String) httpSession.getAttribute("userIdSess");
        if(session==null){
        	  mnv.setViewName("board_auth_page");
            return mnv;
        }
    	
    	Map<String, BoardPageDTO> pageMap = new HashMap<String,BoardPageDTO>();
        List<BoardCrudDTO> boardCrudDTOList = boardCrudService.showBoardListWithPaging(pageNumStr,amountStr ,pageMap);
        mnv.addObject(boardCrudDTOList);
        mnv.addObject("pagelist", pageMap);
        mnv.setViewName("board_item_list_page");
        
        return mnv;
    }
    
    
    
    @RequestMapping(value = "/toWriteFromAllList", method = RequestMethod.GET)
    public ModelAndView toWriteFromAllList(HttpSession httpSession, ModelAndView mnv) {

        String session =(String) httpSession.getAttribute("userIdSess");

		if (session == null) {
			mnv.setViewName("board_auth_page");
			return mnv;
		}
		
		LocalDateTime loc =LocalDateTime.now();
		
		String localDateTimeChangeFormat = loc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		mnv.addObject("updatedWhen", localDateTimeChangeFormat);
		mnv.setViewName("board_item_writer_page");
		return mnv;
    }
    
    
    @RequestMapping(value = "/toUpdateFromDetail/{id}", method = RequestMethod.GET)
    public ModelAndView toUpdateFromDetail( @PathVariable String id, HttpSession httpSession, ModelAndView mnv) {
    	
    	String session =(String) httpSession.getAttribute("userIdSess");
        if(session==null){
        	  mnv.setViewName("board_auth_page");
            return mnv;
        }
    	
    	Map<String ,String> map = new HashMap<String, String>();
    	
    	List<BoardCrudDTO> multiFileNameList = new ArrayList<BoardCrudDTO>();
    	
    	boardCrudService.showBoardDetail(id, map,multiFileNameList);
    	String pageNum=boardCrudService.calcRn(id);
        map.put("pageNum",pageNum);
    	mnv.addObject("multiFileNameList",multiFileNameList);
    	mnv.addObject("map",map);
		mnv.setViewName("board_item_update_page");
		return mnv;
    }
    
   
    @RequestMapping(value = "/insertBoardDataWithFile", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE ,  produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public BoardResDTO insertBoardDataWithFile(@RequestParam(value = "file" ,required = false ) MultipartFile[] files , @RequestParam(value = "uploadTime" , required = false) String[] uploadTimes, BoardCrudDTO boardCrudDTOReq, HttpSession httpSession  ) throws  UnsupportedEncodingException {
    	
    	Map<String,String> fileNameMap = new LinkedHashMap<String, String>();

    	  	
    	for(int i=0; i<files.length; i++) {
    		String uploadTime = new String(uploadTimes[i].getBytes("8859_1"), "utf-8");
    		String fileName = new String(files[i].getOriginalFilename().getBytes("8859_1"), "utf-8");
    		fileNameMap.put(uploadTime ,fileName);
    		
    	}
    	
    	BoardResDTO boardResDTO = new BoardResDTO(); 
    	BoardValidDTO validDto = new BoardValidDTO();

		validDto.setBoardCrudDTO(boardCrudDTOReq);
    	
    	
    	String session =(String) httpSession.getAttribute("userIdSess");
        if(session==null){
        	boardResDTO.setResContent("disconnect");
        	boardResDTO.setCode(500);
        }
    	
    	if(validDto.getBoardCrudDTO().getWriterName()==null || validDto.getBoardCrudDTO().getUpdatedWhen()==null ||validDto.getBoardCrudDTO().getTitle()==null || validDto.getBoardCrudDTO().getContent()==null) {
        	boardResDTO.setResContent("bad request");
        	boardResDTO.setCode(400);
        	return boardResDTO;
        } else if(validDto.getBoardCrudDTO().getWriterName().trim().equals("") || validDto.getBoardCrudDTO().getUpdatedWhen().trim().equals("") || validDto.getBoardCrudDTO().getTitle().trim().equals("") ||validDto.getBoardCrudDTO().getContent().trim().equals("") ) {
        	boardResDTO.setResContent("bad request");
        	boardResDTO.setCode(400);
        	return boardResDTO;
        }
    	
        boardCrudService.insertBoardDataWithFile(files,fileNameMap,boardCrudDTOReq,httpSession);
        
        boardResDTO.setResContent("OK");
    	boardResDTO.setCode(200);
        return boardResDTO;
    }
    
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public ModelAndView showBoardJoinItemRead( 
    		@PathVariable  String id, 
    		HttpSession httpSession) {

        ModelAndView mnv = new ModelAndView();

        String session = (String) httpSession.getAttribute("userIdSess");
      
        if (session == null) {
            mnv.setViewName("board_auth_page");
            return mnv;
        }

        Map<String, String> map = new LinkedHashMap<String, String>();
        
        List<BoardCrudDTO> multiFileNameList = new ArrayList<>();
        boardCrudService.showBoardDetail(id, map, multiFileNameList);
        String pageNum=boardCrudService.calcRn(id);
        map.put("pageNum",pageNum);
        
        mnv.addObject("multiFileNameList",multiFileNameList);
        
        mnv.addObject("userIdSess",(String) httpSession.getAttribute("userIdSess"));
        mnv.addObject("map", map);
        mnv.setViewName("board_item_detail_page");
        return mnv;

    }

    @RequestMapping(value = "/updateBoardDataWithFile", method = RequestMethod.POST,consumes = MediaType.MULTIPART_FORM_DATA_VALUE , produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<BoardResDTO>  updateBoardByIdAndWriterNameWithFile(
    		@RequestParam(value = "updateFiles" ,required = false ) MultipartFile[] updateFiles ,
    		@RequestParam(value = "uploadTime" ,required = false ) String[] updateTimes ,
    		@RequestParam(value = "deleteFileCondition" ,required = false )  String[] deleteFileCondition ,
    		BoardCrudDTO boardCrudDTOReqParam,
    		HttpSession httpSession,
    		Model model) throws Exception {
    	
    	System.out.println(updateFiles.length);
    	//System.out.println(deleteFileCondition.length);
    	
    	BoardValidDTO vaildDto = new BoardValidDTO();
    	BoardResDTO boardResDTO = new BoardResDTO();
    	vaildDto.setBoardCrudDTO(boardCrudDTOReqParam);
    	
    	Map<String,String> updateMap = new LinkedHashMap<String, String>();
    	
    	if(vaildDto.getBoardCrudDTO().getWriterName()==null || vaildDto.getBoardCrudDTO().getUpdatedWhen()==null ||vaildDto.getBoardCrudDTO().getTitle()==null || vaildDto.getBoardCrudDTO().getContent()==null) {
        	boardResDTO.setResContent("bad request");
        	boardResDTO.setCode(400);
        	return new ResponseEntity<BoardResDTO>(boardResDTO, HttpStatus.BAD_REQUEST);
        } else if(vaildDto.getBoardCrudDTO().getWriterName().trim().equals("") || vaildDto.getBoardCrudDTO().getUpdatedWhen().trim().equals("") || vaildDto.getBoardCrudDTO().getTitle().trim().equals("") ||vaildDto.getBoardCrudDTO().getContent().trim().equals("") ) {
        	boardResDTO.setResContent("bad request");
        	boardResDTO.setCode(400);
        	return new ResponseEntity<BoardResDTO>(boardResDTO, HttpStatus.BAD_REQUEST);
        }
    	
    	ResponseEntity<BoardResDTO> updateResDtoWithResponseEntity=boardCrudService.updateBoard(
																				    			updateFiles
																				    			,updateTimes
																				    			,updateMap
																				    			,deleteFileCondition
																				    			,boardCrudDTOReqParam
																				    			,httpSession);


		log.error("updateBoard:{}",boardCrudDTOReqParam);
		return updateResDtoWithResponseEntity ;
    }

    @RequestMapping(value = "/deleteBoardData", method = RequestMethod.POST ,consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<BoardResDTO> deleteDataByID(@RequestBody BoardCrudDTO boardCrudDTOReqParam,HttpSession httpSession) {
		log.error("BoardCrudDTOReqParam:{}",boardCrudDTOReqParam);
		String userId=(String) httpSession.getAttribute("userIdSess");
		boardCrudDTOReqParam.setUserId(userId);
       return  boardCrudService.deleteAllDataByID(boardCrudDTOReqParam);
    }
    
    @RequestMapping(value = "/downloadFile/{id}", method = RequestMethod.GET )
    public void downloadFile(HttpServletResponse response, @RequestParam("userId") String userId, @PathVariable("id") String id, @RequestParam("fileMeta") String fileMeta )  {
        
    	try {
    		boardCrudService.downloadFile(response, id, userId,fileMeta);
    	}catch(UnsupportedEncodingException e) {
    		e.printStackTrace();
    	}
    }
}