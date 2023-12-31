package com.board.board.serviceImpl;

import com.board.board.dao.BoardAuthDAO;
import com.board.board.dto.BoardAuthDTO;
import com.board.board.dto.BoardResDTO;
import com.board.board.service.BoardAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public class BoardAuthServiceImpl implements BoardAuthService {

    private BoardAuthDAO boardAuthDAO;

    @Autowired
    public void setBoardAuthDAO(BoardAuthDAO boardAuthDAO) {
        this.boardAuthDAO = boardAuthDAO;
    }

    public BoardResDTO boardAuthIdentify(BoardAuthDTO boardAuthDtoReqBody) throws RuntimeException {

        try {
            md5AndHexEncode(boardAuthDtoReqBody);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        List<BoardAuthDTO> returnList = boardAuthDAO.readStatementList("com.board.board.mappers.boardAuth.selectAuthIdentify", boardAuthDtoReqBody);


        if (returnList.isEmpty() || !returnList.get(0).getUserId().equals(boardAuthDtoReqBody.getUserId())) {

            BoardResDTO failAuthDTO = new BoardResDTO();
            failAuthDTO.setResDescription("it not present or you are wrong");
            failAuthDTO.setCode(400);
            return failAuthDTO;
        }
        BoardResDTO successAuthDTO = new BoardResDTO();
        successAuthDTO.setResDescription("you are correct");
        successAuthDTO.setCode(200);
        return successAuthDTO;
    }

    public void boardAuthInsertDataWithEncode(BoardAuthDTO boardAuthDtoWithMd5) {
        try {
            md5AndHexEncode(boardAuthDtoWithMd5);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        boardAuthDAO.createStatement("com.board.board.mappers.boardAuth.insertAuthDateWithEncode", boardAuthDtoWithMd5);
    }


    private void md5AndHexEncode(BoardAuthDTO authDtoWithMd5) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("MD5");
        String pwdReqPram = authDtoWithMd5.getUserPw();
        md.update(pwdReqPram.getBytes());
        String encodePw = DatatypeConverter.printHexBinary(md.digest());
        authDtoWithMd5.setUserId(authDtoWithMd5.getUserId());
        authDtoWithMd5.setUserPw(encodePw);
    }

}
