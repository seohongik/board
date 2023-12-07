package com.board.board.service;

import com.board.board.dto.BoardAuthDTO;
import com.board.board.dto.BoardResDTO;
import org.springframework.stereotype.Component;

@Component
public interface BoardAuthService {

     BoardResDTO boardAuthIdentify(BoardAuthDTO boardAuthDtoReqBody) throws RuntimeException;

     void boardAuthInsertDataWithEncode(BoardAuthDTO boardAuthDtoWithOutMD5Encode);

}
