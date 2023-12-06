package com.board.board.configs;

import com.board.board.dto.BoardCrudDTO;
import com.board.board.dto.BoardResDTO;
import com.board.board.dto.BoardValidDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;
import java.util.regex.Matcher;

@ControllerAdvice
public class ValueValidConfig /*implements Validator*/ {


    /*
    @Override
    public boolean supports(Class<?> clazz) {
        return BoardValidDTO.class.isAssignableFrom(clazz);
    }*/

    //@Override
    @ExceptionHandler(ValueValidException.class)
    public void validate(Object target, BindingResult errors) {

        BoardValidDTO boardValidDTO= (BoardValidDTO)target;


        if(!Objects.isNull(boardValidDTO.getBoardCrudDTO())){

            //추후 정규식으로 바꿀예정
            if(Objects.isNull(boardValidDTO.getWriterNameBoard())|| "".equals(boardValidDTO.getWriterNameBoard().trim())){
                errors.rejectValue("writerName"
                        , "400"
                        , "작성자 값이 없습니다.");


            }

            if(Objects.isNull(boardValidDTO.getTitleBoard()) ||"".equals(boardValidDTO.getTitleBoard().trim())){
                errors.rejectValue("title"
                        , "400"
                        , "제목 값이 없습니다.");
            }

            if(Objects.isNull(boardValidDTO.getContentBoard())||"".equals(boardValidDTO.getContentBoard().trim())){
                errors.rejectValue("content"
                        , "400"
                        , "콘텐츠 값이 없습니다.");
            }
        }

        if(!Objects.isNull(boardValidDTO.getBoardReplyDTO())) {
            if (Objects.isNull(boardValidDTO.getWriterNameReply()) || "".equals(boardValidDTO.getWriterNameReply().trim())) {
                errors.rejectValue("writerName"
                        , "400"
                        , "작성자 값이 없습니다.");

            }

            if (Objects.isNull(boardValidDTO.getContentReply()) || "".equals(boardValidDTO.getContentReply().trim())) {
                errors.rejectValue("content"
                        , "400"
                        , "콘텐츠 값이 없습니다.");
            }
        }

    }

}
