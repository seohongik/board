package com.wings.board.service;

import java.util.Map;

import com.wings.board.dto.BoardAuthDTO;
import com.wings.board.dto.BoardResDTO;


public interface BoardAuthService {

	public Map<String, BoardResDTO> boardAuthIdentify(BoardAuthDTO boardAuthDtoReqBody) throws RuntimeException;
	
	public void boardAuthInsertDataWithEndcode(BoardAuthDTO boardAuthDtoWithOutMD5Encode);
	
}
