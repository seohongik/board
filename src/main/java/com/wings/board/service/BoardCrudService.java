package com.wings.board.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.wings.board.dto.BoardCrudDTO;
import com.wings.board.dto.BoardPageDTO;
import com.wings.board.dto.BoardResDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface BoardCrudService {

	public int readTotalRowNum() ;
	
	public List<BoardCrudDTO> showBoardListWithPaging(String pageNumStr, String amountStr, Map<String, BoardPageDTO> pageMap) ;
	
	public void showBoardDetail(String id, Map<String, String> map,List<BoardCrudDTO> multiFileNameList);
	
	public void insertBoardDataWithFile( MultipartFile[] files,Map<String,String> filesMap, BoardCrudDTO boardCrudDTOReq,HttpSession httpSession) throws UnsupportedEncodingException ;
	
	public ResponseEntity<BoardResDTO> deleteAllDataByID(BoardCrudDTO BoardCrudDTOdeleteParam);
	
	public  ResponseEntity<BoardResDTO> updateBoard(
			   MultipartFile[] updateFilesWithNull
			,  String[] updateTimesWithNull
			,  Map<String,String>  updatedFileMapsWithNull
			,  String[] deleteFilesContitionWithNull
			,  BoardCrudDTO boardCrudDTOReq
			,  HttpSession httpSession
			
			) throws Exception;
	public void downloadFile(HttpServletResponse response, String id, String userId, String fileName) throws UnsupportedEncodingException;
	
	public String calcRn(String id);
}
