package com.board.board.controller;


import com.board.board.dto.BoardAuthDTO;
import com.board.board.dto.BoardResDTO;
import com.board.board.service.BoardAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class BoardAuthController {


    public BoardAuthService boardAuthService;

    @Autowired
    public void setBoardAuthService(BoardAuthService boardAuthService) {
        this.boardAuthService = boardAuthService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {

        return "board_auth_page";
    }

    @RequestMapping(value = "/authExecute", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<BoardResDTO> authExecute(@RequestBody BoardAuthDTO boardAuthDtoReqBody, HttpServletRequest request) {

        BoardResDTO authResDTO = boardAuthService.boardAuthIdentify(boardAuthDtoReqBody);

        if (400 == authResDTO.getCode()) {
            return new ResponseEntity<BoardResDTO>(authResDTO, HttpStatus.BAD_REQUEST);
        } else {
            HttpSession session = request.getSession();
            //유지시간(초단위) -1-> 무한대 설정
            session.isNew();
            session.setMaxInactiveInterval(-1);
            session.setAttribute("userIdSess", boardAuthDtoReqBody.getUserId());
            return new ResponseEntity<BoardResDTO>(authResDTO, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/authCreate", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<BoardResDTO> authCreate(@RequestBody BoardAuthDTO boardAuthDTO) {

        boardAuthService.boardAuthInsertDataWithEncode(boardAuthDTO);

        BoardResDTO boardResDTO = new BoardResDTO();
        boardResDTO.setCode(200);
        return new ResponseEntity<>(boardResDTO, HttpStatus.OK);
    }

}
