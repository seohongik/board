package com.board.board.service;

import java.util.Map;

import com.board.board.dto.BoardAuthDTO;
import com.board.board.dto.BoardResDTO;
import org.springframework.stereotype.Service;

public interface BoardAuthService {

	public Map<String, BoardResDTO> boardAuthIdentify(BoardAuthDTO boardAuthDtoReqBody) throws RuntimeException;
	
	public void boardAuthInsertDataWithEncode(BoardAuthDTO boardAuthDtoWithOutMD5Encode);
	
}
