package com.board.board.dataMaker;

import com.board.board.dto.BoardReplyDTO;

public class DataMakeReply {

    public void init(BoardReplyDTO boardReplyDTOParam, BoardReplyDTO boardReplyDTOText){

        boardReplyDTOText.setId(boardReplyDTOParam.getId());
        boardReplyDTOText.setParentReplyId(boardReplyDTOParam.getId());
        boardReplyDTOText.setWriterName(boardReplyDTOParam.getWriterName());
        boardReplyDTOText.setContent(boardReplyDTOParam.getContent());
        boardReplyDTOText.setCreatedBy(boardReplyDTOParam.getUserId());
        boardReplyDTOText.setUpdatedBy(boardReplyDTOParam.getUserId());
    }

}