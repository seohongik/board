package com.wings.board.serviceImpl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.wings.board.dao.BoardAuthDAO;
import com.wings.board.dto.BoardAuthDTO;
import com.wings.board.dto.BoardResDTO;
import com.wings.board.service.BoardAuthService;
import org.springframework.beans.factory.annotation.Autowired;

public class BoardAuthServiceImpl implements BoardAuthService {


	private BoardAuthDAO boardAuthDAO;

	public void setBoardAuthDAO(BoardAuthDAO boardAuthDAO) {
		this.boardAuthDAO=boardAuthDAO;
	}

	public Map<String, BoardResDTO> boardAuthIdentify(BoardAuthDTO boardAuthDtoReqBody) throws RuntimeException{

		BoardAuthDTO boardAuthDtoWithMd5 = boardAuthDtoReqBody;

		boardAuthDtoWithMd5.setUserId(boardAuthDtoReqBody.getUserId());
		boardAuthDtoWithMd5.setUserPw(boardAuthDtoReqBody.getUserPw());


        try {
        	md5AndHexEncode(boardAuthDtoWithMd5);
        }catch(Exception e) {
        	e.printStackTrace();
        	throw new RuntimeException(e);
        }
        List<BoardAuthDTO> returnList=boardAuthDAO.readStatementList("com.wings.board.mappers.selectAuthIdentify", boardAuthDtoWithMd5);
       
        Map<String,BoardResDTO> resultMap = new HashMap<String,BoardResDTO>();
		
		if(returnList.isEmpty() || !returnList.get(0).getUserId().equals(boardAuthDtoReqBody.getUserId())) {
	            
	            BoardResDTO failAuthDTO = new BoardResDTO();
	            failAuthDTO.setResContent("it not present or you are wrong");
	            failAuthDTO.setCode(400);
	            resultMap.put("result", failAuthDTO);
	            return resultMap;
	    }
		BoardResDTO successAuthDTO = new BoardResDTO();
		successAuthDTO.setResContent(" you are correct ");
		successAuthDTO.setCode(200);
        resultMap.put("result", successAuthDTO);
		return resultMap;
	}

	public void boardAuthInsertDataWithEndcode(BoardAuthDTO boardAuthDtoWithMd5) {
		try {
        	md5AndHexEncode(boardAuthDtoWithMd5);
        }catch(Exception e) {
        	e.printStackTrace();
        	throw new RuntimeException(e);
        }
		
		boardAuthDAO.createStatement("com.wings.board.mappers.insertAuthDateWithEncode",boardAuthDtoWithMd5);
	}

	
	private void md5AndHexEncode(BoardAuthDTO authDtoWithMd5) throws NoSuchAlgorithmException {
		
		MessageDigest md = MessageDigest.getInstance("MD5");
        String pwdReqPram = authDtoWithMd5.getUserPw();
        md.update(pwdReqPram.getBytes());
        String encodePw  =DatatypeConverter.printHexBinary(md.digest());
        authDtoWithMd5.setUserId(authDtoWithMd5.getUserId());
        authDtoWithMd5.setUserPw(encodePw);
    }
    
}
