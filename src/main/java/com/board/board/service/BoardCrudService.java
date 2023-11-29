package com.board.board.service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.board.board.dto.BoardCrudDTO;
import com.board.board.dto.BoardPageDTO;
import com.board.board.dto.BoardResDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

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

}
