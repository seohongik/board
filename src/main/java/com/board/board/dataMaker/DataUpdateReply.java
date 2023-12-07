package com.board.board.dataMaker;

import com.board.board.dto.BoardReplyDTO;
import org.springframework.stereotype.Component;

@Component
public class DataUpdateReply {
    public void init(BoardReplyDTO boardReplyDTO, BoardReplyDTO boardReplyDTOParam) {

        boardReplyDTO.setId(boardReplyDTOParam.getId());
        boardReplyDTO.setParentReplyId(boardReplyDTOParam.getParentReplyId());
        boardReplyDTO.setChildReplyId(boardReplyDTOParam.getChildReplyId());
        boardReplyDTO.setContent(boardReplyDTOParam.getContent());
        boardReplyDTO.setWriterName(boardReplyDTOParam.getWriterName());
    }
}
