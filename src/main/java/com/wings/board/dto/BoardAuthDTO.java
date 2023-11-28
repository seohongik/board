package com.wings.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Alias("boardAuthDTO")
public class BoardAuthDTO {

    protected String id;
    protected String userId;
    protected String userPw;
    protected String createdBy;


    
}
