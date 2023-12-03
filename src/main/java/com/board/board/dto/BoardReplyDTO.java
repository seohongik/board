package com.board.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Alias("boardReplyDTO")
public class BoardReplyDTO {

    private int id ;
    private int parentReplyId;
    private int childReplyId  ;
    private String writerName ;
    private String content ;
    private String createdBy ;
    private String updatedBy ;
    private String userId;

    private String whichBtn;
    private String rowNum;

}
