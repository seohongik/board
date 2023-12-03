

package com.board.board.controller;

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

import com.board.board.dto.*;
import com.board.board.service.BoardCrudService;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
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
        List<BoardCrudDTO> boardCrudDTOList = boardCrudService.showAllBoardDataWithPaging(pageNumStr,amountStr ,pageMap);
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
    	
    	String userId =(String) httpSession.getAttribute("userIdSess");
        if(userId==null){
        	  mnv.setViewName("board_auth_page");
            return mnv;
        }
		BoardReplyDTO boardReplyDTOMother = new BoardReplyDTO();
		boardReplyDTOMother.setId(Integer.parseInt(id));
		BoardReplyDTO boardReplyDTOChild = new BoardReplyDTO();
		BoardReplyDTO boardReplyDTO = new BoardReplyDTO();
		boardReplyDTO.setId(Integer.parseInt(id));
		Map<String, String>  map = new LinkedHashMap<String, String>();
		List<BoardCrudDTO> multiFileNameList=boardCrudService.showBoardDetail(id,userId, map);
		List<BoardReplyDTO> replyListMother = new ArrayList<>(boardCrudService.showReplyMother(boardReplyDTOMother));
		List<BoardReplyDTO> replyListChildList=boardCrudService.showReplyChild(boardReplyDTOChild);
		List<BoardReplyDTO> replyListChildResultList = new ArrayList<>();

		for(BoardReplyDTO boardReplyDTOM:replyListMother) {


			for(BoardReplyDTO boardReplyDTOD:replyListChildList)
				if(boardReplyDTOD.getId()==boardReplyDTOM.getId()&& boardReplyDTOM.getParentReplyId() ==boardReplyDTOD.getParentReplyId()) {
					replyListChildResultList.add(boardReplyDTOD);
				}
		};

		String pageNum=boardCrudService.calcRn(id);


		map.put("pageNum",pageNum);
		mnv.addObject("replyListMother", replyListMother);
		mnv.addObject("replyListChild", replyListChildResultList);
		mnv.addObject("multiFileNameList",multiFileNameList);
		mnv.addObject("userIdSess",userId);
		mnv.addObject("map", map);
		//mnv.addObject("replyList", replyList);
		mnv.setViewName("board_item_update_page");
		return mnv;

    }
    
   
    @RequestMapping(value = "/insertBoardDataWithFile", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE ,  produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public BoardResDTO insertBoardDataWithFile(@RequestParam(value = "file" ,required = false ) MultipartFile[] files , @RequestParam(value = "uploadTime" , required = false) String[] uploadTimes, BoardCrudDTO boardCrudDTOReq, HttpSession httpSession  ) throws  UnsupportedEncodingException {

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
    	
        boardCrudService.insertBoardDataWithFile(files,uploadTimes,boardCrudDTOReq,httpSession);
        
        boardResDTO.setResContent("OK");
    	boardResDTO.setCode(200);
        return boardResDTO;
    }
    
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public ModelAndView showBoardJoinItemRead( 
    		@PathVariable  String id,
    		HttpSession httpSession) {

        ModelAndView mnv = new ModelAndView();

        String userId = (String) httpSession.getAttribute("userIdSess");
      
        if (userId == null) {
            mnv.setViewName("board_auth_page");
            return mnv;
        }
		BoardReplyDTO boardReplyDTOMother = new BoardReplyDTO();
		boardReplyDTOMother.setId(Integer.parseInt(id));
		BoardReplyDTO boardReplyDTOChild = new BoardReplyDTO();
		boardReplyDTOChild.setId(Integer.parseInt(id));
        Map<String, String> textMap = new LinkedHashMap<String, String>();
		List<BoardCrudDTO> multiFileNameList=boardCrudService.showBoardDetail(id,userId, textMap);
		List<BoardReplyDTO> replyListMother = new ArrayList<>(boardCrudService.showReplyMother(boardReplyDTOMother));
		List<BoardReplyDTO> replyListChildList=boardCrudService.showReplyChild(boardReplyDTOChild);
		List<BoardReplyDTO> replyListChildResultList = new ArrayList<>();
		for(BoardReplyDTO boardReplyDTOM:replyListMother) {


			for(BoardReplyDTO boardReplyDTOD:replyListChildList)
				if(boardReplyDTOD.getId()==boardReplyDTOM.getId()&& boardReplyDTOM.getParentReplyId() ==boardReplyDTOD.getParentReplyId()) {
					replyListChildResultList.add(boardReplyDTOD);
				}
		}

        String pageNum=boardCrudService.calcRn(id);
		textMap.put("pageNum",pageNum);
        mnv.addObject("multiFileNameList",multiFileNameList);
        mnv.addObject("userIdSess",userId);
        mnv.addObject("map", textMap);
		mnv.addObject("replyListMother", replyListMother);
		mnv.addObject("replyListChild", replyListChildResultList);
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
    	
    	BoardValidDTO validDto = new BoardValidDTO();
    	BoardResDTO boardResDTO = new BoardResDTO();
		validDto.setBoardCrudDTO(boardCrudDTOReqParam);
    	
    	Map<String,String> updateMap = new LinkedHashMap<String, String>();
    	
    	if(validDto.getBoardCrudDTO().getWriterName()==null || validDto.getBoardCrudDTO().getUpdatedWhen()==null ||validDto.getBoardCrudDTO().getTitle()==null || validDto.getBoardCrudDTO().getContent()==null) {
        	boardResDTO.setResContent("bad request");
        	boardResDTO.setCode(400);
        	return new ResponseEntity<BoardResDTO>(boardResDTO, HttpStatus.BAD_REQUEST);
        } else if(validDto.getBoardCrudDTO().getWriterName().trim().equals("") || validDto.getBoardCrudDTO().getUpdatedWhen().trim().equals("") || validDto.getBoardCrudDTO().getTitle().trim().equals("") ||validDto.getBoardCrudDTO().getContent().trim().equals("") ) {
        	boardResDTO.setResContent("bad request");
        	boardResDTO.setCode(400);
        	return new ResponseEntity<BoardResDTO>(boardResDTO, HttpStatus.BAD_REQUEST);
        }
    	
    	ResponseEntity<BoardResDTO> updateResDtoWithResponseEntity=boardCrudService.updateBoardWithFile(
																				    			updateFiles
																				    			,updateTimes
																				    			,updateMap
																				    			,deleteFileCondition
																				    			,boardCrudDTOReqParam
																				    			,httpSession);
		return updateResDtoWithResponseEntity ;
    }

    @RequestMapping(value = "/deleteBoardData", method = RequestMethod.POST ,consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<BoardResDTO> deleteDataByID(@RequestBody BoardCrudDTO boardCrudDTOReqParam,HttpSession httpSession) {
		log.error("BoardCrudDTOReqParam:{}",boardCrudDTOReqParam);
		String userId=(String) httpSession.getAttribute("userIdSess");
		boardCrudDTOReqParam.setUserId(userId);
		boardCrudService.deleteReplyAll(Integer.parseInt(boardCrudDTOReqParam.getId()));
       return  boardCrudService.deleteAllDataByID(boardCrudDTOReqParam);
    }
    
    @RequestMapping(value = "/downloadFile/{id}", method = RequestMethod.GET )
    public void downloadFile(HttpServletResponse response,  @PathVariable("id") String id,@RequestParam("userId") String userId, @RequestParam("fileMeta") String fileMeta )  {
		
    	try {
    		boardCrudService.downloadFile(response, id, userId,fileMeta);
    	}catch(UnsupportedEncodingException e) {
    		e.printStackTrace();
    	}
    }

	@RequestMapping(value = "/reply", method = RequestMethod.POST ,consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<BoardResDTO> reply(@RequestBody BoardReplyDTO boardReplyDTO){

        boardCrudService.makeReply(boardReplyDTO);

		BoardResDTO boardResDTO = new BoardResDTO();
		boardResDTO.setCode(200);
		return new ResponseEntity<>(boardResDTO, HttpStatus.OK);

	}

	@RequestMapping(value = "/removeReply" , method = RequestMethod.GET)
	public String removeReply(@RequestParam int id , @RequestParam String parentReplyId , @RequestParam(defaultValue = "-1") String childReplyId ){

		BoardReplyDTO boardReplyDTO = new BoardReplyDTO();
		String div = "";
		if(childReplyId==null){
			div="each";
		}else {
			div="all";
		}
		boardCrudService.deleteReply(boardReplyDTO,id,Integer.parseInt(parentReplyId),Integer.parseInt(childReplyId),div);

		return "redirect:/board/detail/"+id;
	}

	@RequestMapping(value = "/updateReply" , method = RequestMethod.POST)
	public ResponseEntity<BoardResDTO> updateReply(@RequestBody BoardReplyDTO boardReplyDTOParam){

		return boardCrudService.updateReply(boardReplyDTOParam);
	}
}