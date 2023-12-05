

package com.board.board.controller;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.board.board.configs.ValueValidConfig;
import com.board.board.dto.*;
import com.board.board.service.BoardCrudService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.ognl.BooleanExpression;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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

	private ValueValidConfig valueValidConfig;

	@Autowired
	public void setBoardCrudService(BoardCrudService boardCrudService) {
		this.boardCrudService = boardCrudService;
	}

	@Autowired
	public void setValueValidConfig(ValueValidConfig valueValidConfig) {
		this.valueValidConfig = valueValidConfig;
	}

	@RequestMapping(value = "/showAllList", method = RequestMethod.GET)
	public ModelAndView showBoardListWithPaging(HttpSession httpSession, @RequestParam("pageNum") String pageNumStr, @RequestParam(value = "amount", defaultValue = "10") String amountStr) {

		ModelAndView mnv = new ModelAndView();

		String session = (String) httpSession.getAttribute("userIdSess");
		if (session == null) {
			mnv.setViewName("board_auth_page");
			return mnv;
		}

		Map<String, BoardPageDTO> pageMap = new HashMap<String, BoardPageDTO>();
		List<BoardCrudDTO> boardCrudDTOList = boardCrudService.showAllBoardDataWithPaging(pageNumStr, amountStr, pageMap);
		mnv.addObject(boardCrudDTOList);
		mnv.addObject("pagelist", pageMap);
		mnv.setViewName("board_item_list_page");

		return mnv;
	}

	@RequestMapping(value = "/toWriteFromAllList", method = RequestMethod.GET)
	public ModelAndView toWriteFromAllList(HttpSession httpSession, ModelAndView mnv) {

		String session = (String) httpSession.getAttribute("userIdSess");

		if (session == null) {
			mnv.setViewName("board_auth_page");
			return mnv;
		}

		LocalDateTime loc = LocalDateTime.now();

		String localDateTimeChangeFormat = loc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		mnv.addObject("updatedWhen", localDateTimeChangeFormat);
		mnv.setViewName("board_item_writer_page");
		return mnv;
	}


	@RequestMapping(value = "/toUpdateFromDetail/{id}", method = RequestMethod.GET)
	public ModelAndView toUpdateFromDetail(@PathVariable String id, HttpSession httpSession, ModelAndView mnv) {

		String userId = (String) httpSession.getAttribute("userIdSess");
		if (userId == null) {
			mnv.setViewName("board_auth_page");
			return mnv;
		}
		BoardReplyDTO boardReplyDTOMother = new BoardReplyDTO();
		boardReplyDTOMother.setId(Integer.parseInt(id));
		BoardReplyDTO boardReplyDTOChild = new BoardReplyDTO();
		BoardReplyDTO boardReplyDTO = new BoardReplyDTO();
		boardReplyDTO.setId(Integer.parseInt(id));
		Map<String, String> map = new LinkedHashMap<String, String>();
		List<BoardCrudDTO> multiFileNameList = boardCrudService.showBoardDetail(id, userId, map);
		List<BoardReplyDTO> replyListMother = new ArrayList<>(boardCrudService.showReplyMother(boardReplyDTOMother));
		List<BoardReplyDTO> replyListChildList = boardCrudService.showReplyChild(boardReplyDTOChild);
		List<BoardReplyDTO> replyListChildResultList = new ArrayList<>();

		for (BoardReplyDTO boardReplyDTOM : replyListMother) {


			for (BoardReplyDTO boardReplyDTOD : replyListChildList)
				if (boardReplyDTOD.getId() == boardReplyDTOM.getId() && boardReplyDTOM.getParentReplyId() == boardReplyDTOD.getParentReplyId()) {
					replyListChildResultList.add(boardReplyDTOD);
				}
		}
		;

		String pageNum = boardCrudService.calcRn(id);


		map.put("pageNum", pageNum);
		mnv.addObject("replyListMother", replyListMother);
		mnv.addObject("replyListChild", replyListChildResultList);
		mnv.addObject("multiFileNameList", multiFileNameList);
		mnv.addObject("userIdSess", userId);
		mnv.addObject("map", map);
		//mnv.addObject("replyList", replyList);
		mnv.setViewName("board_item_update_page");
		return mnv;

	}


	@RequestMapping(value = "/insertBoardDataWithFile", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<BoardResDTO> insertBoardDataWithFile(@RequestParam(value = "file", required = false) MultipartFile[] files, @RequestParam(value = "uploadTime", required = false) String[] uploadTimes, BoardCrudDTO boardCrudDTOReq, HttpSession httpSession, BindingResult bindingResult) throws UnsupportedEncodingException {
		String session = (String) httpSession.getAttribute("userIdSess");
		BoardResDTO boardResDTO = new BoardResDTO();
		if (session == null) {
			boardResDTO.setCode(203);
		}

		BoardValidDTO validDto = new BoardValidDTO(boardCrudDTOReq);

		StringBuilder sb = new StringBuilder();
		Map<Boolean,BoardResDTO>  validResultMap =determineValid(validDto, bindingResult, boardResDTO, sb);

		if(validResultMap.containsKey(false)){

			return new ResponseEntity<>(validResultMap.get(false),HttpStatus.BAD_REQUEST);
		}

		boardCrudService.insertBoardDataWithFile(files, uploadTimes, boardCrudDTOReq, httpSession);
		boardResDTO.setCode(200);
		return new ResponseEntity<BoardResDTO>(boardResDTO, HttpStatus.OK);
	}

	@RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
	public ModelAndView showBoardJoinItemRead(
			@PathVariable String id,
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
		List<BoardCrudDTO> multiFileNameList = boardCrudService.showBoardDetail(id, userId, textMap);
		List<BoardReplyDTO> replyListMother = new ArrayList<>(boardCrudService.showReplyMother(boardReplyDTOMother));
		List<BoardReplyDTO> replyListChildList = boardCrudService.showReplyChild(boardReplyDTOChild);
		List<BoardReplyDTO> replyListChildResultList = new ArrayList<>();
		for (BoardReplyDTO boardReplyDTOM : replyListMother) {

			for (BoardReplyDTO boardReplyDTOD : replyListChildList) {
				if (boardReplyDTOD.getId() == boardReplyDTOM.getId() && boardReplyDTOM.getParentReplyId() == boardReplyDTOD.getParentReplyId()) {
					replyListChildResultList.add(boardReplyDTOD);
				}
			}
		}

		String pageNum = boardCrudService.calcRn(id);
		textMap.put("pageNum", pageNum);
		mnv.addObject("multiFileNameList", multiFileNameList);
		mnv.addObject("userIdSess", userId);
		mnv.addObject("map", textMap);
		mnv.addObject("replyListMother", replyListMother);
		mnv.addObject("replyListChild", replyListChildResultList);
		mnv.setViewName("board_item_detail_page");
		return mnv;

	}

	@RequestMapping(value = "/updateBoardDataWithFile", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<BoardResDTO> updateBoardByIdAndWriterNameWithFile(
			@RequestParam(value = "updateFiles", required = false) MultipartFile[] updateFiles,
			@RequestParam(value = "uploadTime", required = false) String[] updateTimes,
			@RequestParam(value = "deleteFileCondition", required = false) String[] deleteFileCondition,
			BoardCrudDTO boardCrudDTOReqParam,
			HttpSession httpSession,
			BindingResult bindingResult) throws Exception {

		System.out.println(updateFiles.length);
		//System.out.println(deleteFileCondition.length);

		BoardValidDTO validDto = new BoardValidDTO(boardCrudDTOReqParam);
		BoardResDTO boardResDTO = new BoardResDTO();
		validDto.setBoardCrudDTO(boardCrudDTOReqParam);


		StringBuilder sb = new StringBuilder();
		Map<Boolean,BoardResDTO>  validResultMap =determineValid(validDto, bindingResult, boardResDTO, sb);

		if(validResultMap.containsKey(false)){

			return new ResponseEntity<>(validResultMap.get(false),HttpStatus.BAD_REQUEST);
		}



		Map<String, String> updateMap = new LinkedHashMap<String, String>();


		ResponseEntity<BoardResDTO> updateResDtoWithResponseEntity = boardCrudService.updateBoardWithFile(
				updateFiles
				, updateTimes
				, updateMap
				, deleteFileCondition
				, boardCrudDTOReqParam
				, httpSession);
		return updateResDtoWithResponseEntity;
	}

	@RequestMapping(value = "/deleteBoardData", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<BoardResDTO> deleteDataByID(@RequestBody BoardCrudDTO boardCrudDTOReqParam, HttpSession httpSession) {
		log.error("BoardCrudDTOReqParam:{}", boardCrudDTOReqParam);
		String userId = (String) httpSession.getAttribute("userIdSess");
		boardCrudDTOReqParam.setUserId(userId);
		boardCrudService.deleteReplyAll(Integer.parseInt(boardCrudDTOReqParam.getId()));
		return boardCrudService.deleteAllDataByID(boardCrudDTOReqParam);
	}

	@RequestMapping(value = "/downloadFile/{id}", method = RequestMethod.GET)
	public void downloadFile(HttpServletResponse response, @PathVariable("id") String id, @RequestParam("userId") String userId, @RequestParam("fileMeta") String fileMeta) {

		try {
			boardCrudService.downloadFile(response, id, userId, fileMeta);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/makeReply", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<BoardResDTO> reply(@RequestBody BoardReplyDTO boardReplyDTO) {

		boardCrudService.makeReply(boardReplyDTO);

		BoardResDTO boardResDTO = new BoardResDTO();
		boardResDTO.setCode(200);
		return new ResponseEntity<>(boardResDTO, HttpStatus.OK);

	}

	@RequestMapping(value = "/removeReply", method = RequestMethod.GET)
	public String removeReply(@RequestParam int id, @RequestParam String parentReplyId, @RequestParam(defaultValue = "-1") String childReplyId) {

		BoardReplyDTO boardReplyDTO = new BoardReplyDTO();
		String div = "";
		if (childReplyId == null) {
			div = "each";
		} else {
			div = "all";
		}
		boardCrudService.deleteReply(boardReplyDTO, id, Integer.parseInt(parentReplyId), Integer.parseInt(childReplyId), div);

		return "redirect:/board/detail/" + id;
	}

	@RequestMapping(value = "/updateReply", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<BoardResDTO> updateReply(@RequestBody BoardReplyDTO boardReplyDTOParam) {

		return boardCrudService.updateReply(boardReplyDTOParam);
	}

	private Map<Boolean,BoardResDTO> determineValid(BoardValidDTO validDto, BindingResult bindingResult, BoardResDTO boardResDTO, StringBuilder sb ) {

		Map<Boolean,BoardResDTO> map = new HashMap<>();
		valueValidConfig.validate(validDto, bindingResult);
		if (bindingResult.hasErrors()) {

			List<FieldError> errs = bindingResult.getFieldErrors();

			for (FieldError fieldError : errs) {

				String errMsg = fieldError.getDefaultMessage();
				sb.append("message :  ").append(errMsg);
				boardResDTO.setCode(400);
				boardResDTO.setResDescription(sb.toString());
			}
			map.put(false,boardResDTO);

		}
		return  map;
	}
}