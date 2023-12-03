package com.board.board.service;

import com.board.board.dto.BoardCrudDTO;
import com.board.board.dto.BoardPageDTO;
import com.board.board.dto.BoardReplyDTO;
import com.board.board.dto.BoardResDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public interface BoardCrudService {

	public String calcRn(String id);
	
	public List<BoardCrudDTO> showAllBoardDataWithPaging(String pageNumStr, String amountStr, Map<String, BoardPageDTO> pageMap) ;
	
	public List<BoardCrudDTO> showBoardDetail(String id,String userId, Map<String, String> map);
	
	public void insertBoardDataWithFile(MultipartFile[] file, String[] uploadTimes, BoardCrudDTO boardCrudDTOReq, HttpSession httpSession) throws UnsupportedEncodingException ;
	
	public ResponseEntity<BoardResDTO> deleteAllDataByID(BoardCrudDTO BoardCrudDTOdeleteParam);
	
	public  ResponseEntity<BoardResDTO> updateBoardWithFile(
			   MultipartFile[] updateFilesWithNull
			,  String[] updateTimesWithNull
			,  Map<String,String>  updatedFileMapsWithNull
			,  String[] deleteFilesContitionWithNull
			,  BoardCrudDTO boardCrudDTOReq
			,  HttpSession httpSession
			
			) throws Exception;
	public void downloadFile(HttpServletResponse response, String id, String userId, String fileName) throws UnsupportedEncodingException;

	public  void makeReply(BoardReplyDTO boardReplyDTO);

	public  List<BoardReplyDTO> showReplyMother(BoardReplyDTO boardReplyDTO);

	public  List<BoardReplyDTO> showReplyChild(BoardReplyDTO boardReplyDTO);

	void deleteReply(BoardReplyDTO boardReplyDTO, int id, int parentReplyId, int childReplyId,String div);

	public void deleteReplyAll(int id);

	ResponseEntity<BoardResDTO> updateReply(BoardReplyDTO boardReplyDTOParam);
}
