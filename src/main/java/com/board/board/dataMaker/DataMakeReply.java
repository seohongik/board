package com.board.board.dataMaker;

import com.board.board.dto.BoardReplyDTO;
import org.springframework.stereotype.Component;

@Component
public class DataMakeReply {

    public void init(BoardReplyDTO boardReplyDTOParam, BoardReplyDTO boardReplyDTOText) {

        boardReplyDTOText.setId(boardReplyDTOParam.getId());
        boardReplyDTOText.setWriterName(boardReplyDTOParam.getWriterName());
        boardReplyDTOText.setCreatedBy(boardReplyDTOParam.getUserId());
        boardReplyDTOText.setUpdatedBy(boardReplyDTOParam.getUserId());
        boardReplyDTOText.setContent(boardReplyDTOParam.getContent());
        boardReplyDTOText.setCreatedBy(boardReplyDTOParam.getUserId());
        boardReplyDTOText.setUpdatedBy(boardReplyDTOParam.getUserId());
    }

}
