package com.board.board.dataMaker;

import com.board.board.dto.BoardCrudDTO;
import com.board.board.dto.BoardResDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataDeleteAllById {

    public File getFolder(BoardCrudDTO boardCrudDTOText){
        File psyFolder = new File(boardCrudDTOText.getLocDrive() + File.separatorChar + boardCrudDTOText.getLocParentFolder() + File.separatorChar + boardCrudDTOText.getLocChildFolder());
        return psyFolder;
    }

    public ResponseEntity<BoardResDTO> isStatus(List<BoardCrudDTO> selectByIdAfterDelete){

        if (selectByIdAfterDelete.isEmpty()) {
            BoardResDTO sucessBoardResDTO = new BoardResDTO();
            sucessBoardResDTO.setCode(200);
            sucessBoardResDTO.setResDescription("success");
            return new ResponseEntity<BoardResDTO>(sucessBoardResDTO, HttpStatus.OK);
        } else {
            BoardResDTO failBoardResDTO = new BoardResDTO();
            failBoardResDTO.setCode(500);
            failBoardResDTO.setResDescription("update fail");
            return new ResponseEntity<BoardResDTO>(failBoardResDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
