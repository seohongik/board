package com.board.board.dataMaker;

import com.board.board.dto.BoardReplyDTO;

public class DataDeleteReply {

    public void init(BoardReplyDTO boardReplyDTO, int id , int parentReplyId, int childReplyId,String div) {

        boardReplyDTO.setId(id);
        boardReplyDTO.setParentReplyId(parentReplyId);
        boardReplyDTO.setChildReplyId(childReplyId);
    }
}
