package com.board.board.dataMaker;

import com.board.board.dto.BoardCrudDTO;
import com.board.board.dto.BoardPageDTO;
import com.board.board.sqlMaker.SqlMaker;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DataMakeBoardListWithPaging {
    public BoardPageDTO init(String pageNumStr, String amountStr, int total) {
        int pageNumInt = 0;
        if ("".equals(pageNumStr)) {
            pageNumInt = 1;
        } else {
            pageNumInt = Integer.parseInt(pageNumStr);
        }
        int amount = Integer.parseInt(amountStr);

        BoardPageDTO boardPageDTO  = new BoardPageDTO(pageNumInt, amount, total);

        int numMultiAmount = boardPageDTO.getPageNum() * boardPageDTO.getAmount();
        int numMinusOneMultiAmount = (boardPageDTO.getPageNum() - 1) * boardPageDTO.getAmount();

        boardPageDTO.setNumMutiAmount(numMultiAmount);
        boardPageDTO.setNumMinusOneMutiAmount(numMinusOneMultiAmount);

        return boardPageDTO;
    }


    public  void  paging(Map<String, BoardPageDTO> pageMap,BoardPageDTO boardPageDTO) {
        pageMap.put("pageMaker", boardPageDTO);

    }


    public void hasFileData(List<BoardCrudDTO> list) {
        if(!list.isEmpty()) {

            for (BoardCrudDTO boardCrudDTO : list) {

                boardCrudDTO.setUpdatedWhen(boardCrudDTO.getUpdatedWhen().substring(0, 10));

                if (boardCrudDTO.getFileCnt() != null && Integer.parseInt(boardCrudDTO.getFileCnt()) > 0) {
                    boardCrudDTO.setHasFile("Y");
                } else {
                    boardCrudDTO.setHasFile("N");
                }
            }
        }
    }
}
