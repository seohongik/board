package com.board.board.dataMaker;

import com.board.board.dto.BoardReplyDTO;
import org.springframework.stereotype.Component;

@Component
public class DataDeleteReply {

    public void init(BoardReplyDTO boardReplyDTO, int id, int parentReplyId, int childReplyId) {

        boardReplyDTO.setId(id);
        boardReplyDTO.setParentReplyId(parentReplyId);
        boardReplyDTO.setChildReplyId(childReplyId);
    }
}
