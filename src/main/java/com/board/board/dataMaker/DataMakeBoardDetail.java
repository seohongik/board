package com.board.board.dataMaker;

import com.board.board.dto.BoardCrudDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DataMakeBoardDetail {


    public void  init(List<BoardCrudDTO> list , Map<String, String> textMap){

        for (BoardCrudDTO infoAllDto:list) {
            textMap.put("id", infoAllDto.getId());
            textMap.put("userId", infoAllDto.getUserId());
            textMap.put("title", infoAllDto.getTitle());
            textMap.put("writerName", infoAllDto.getWriterName());
            textMap.put("content", infoAllDto.getContent());
            textMap.put("updatedWhen", infoAllDto.getUpdatedWhen());
        }

    }
}
