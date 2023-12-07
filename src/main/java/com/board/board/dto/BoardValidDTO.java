package com.board.board.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

@Data
@NoArgsConstructor
@Alias("boardValidDTO")
public class BoardValidDTO {


    private BoardCrudDTO boardCrudDTO;

    private BoardReplyDTO boardReplyDTO;

    public BoardValidDTO(BoardCrudDTO boardCrudDTO) {
        this.boardCrudDTO = boardCrudDTO;
    }

    public BoardValidDTO(BoardReplyDTO boardReplyDTO) {
        this.boardReplyDTO = boardReplyDTO;
    }

    public String getWriterNameBoard() {
        return boardCrudDTO.getWriterName();
    }

    public String getContentBoard() {
        return boardCrudDTO.getContent();
    }

    public String getTitleBoard() {
        return boardCrudDTO.getTitle();
    }

    public String getWriterNameReply() {
        return boardReplyDTO.getWriterName();
    }

    public String getContentReply() {
        return boardReplyDTO.getContent();
    }


}
