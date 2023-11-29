package com.board.board.sqlMakerImpl;

import com.board.board.dao.BoardCrudDAO;
import com.board.board.dto.BoardCrudDTO;
import com.board.board.dto.BoardPageDTO;
import com.board.board.sqlMaker.SqlMaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


public class SqlMakerImpl implements SqlMaker {


    BoardCrudDAO boardCrudDAO;

    public void setBoardCrudDAO(BoardCrudDAO boardCrudDAO) {
        this.boardCrudDAO = boardCrudDAO;
    }

    @Override
    public List<BoardCrudDTO> readList(String sql, BoardCrudDTO boardCrudDTO) {
        return boardCrudDAO.readStatementList(sql,boardCrudDTO);
    }

    @Override
    public BoardCrudDTO read(String sql,BoardCrudDTO boardCrudDTO) {
        return boardCrudDAO.readStatement(sql,boardCrudDTO);
    }

    @Override
    public int create(String sql,BoardCrudDTO boardCrudDTO) {
        return boardCrudDAO.createStatement(sql,boardCrudDTO);
    }

    @Override
    public int update(String sql,BoardCrudDTO boardCrudDTO) {
        return boardCrudDAO.updateStatement(sql,boardCrudDTO);
    }

    @Override
    public int delete(String sql,BoardCrudDTO boardCrudDTO) {
        return boardCrudDAO.deleteStatement(sql,boardCrudDTO);
    }

    @Override
    public int readCount(String sql,BoardCrudDTO boardCrudDTO) {
        return  boardCrudDAO.readStatementCount(sql, new BoardCrudDTO());
    }

    @Override
    public List<BoardCrudDTO> readList(String sql, BoardPageDTO boardPageDTO) {
        return boardCrudDAO.readStatementList(sql,boardPageDTO);
    }

    @Override
    public String readString(String sql, String id) {
        return boardCrudDAO.readStatementString(sql,id);
    }

    @Override
    public Object readObject(String sql, BoardCrudDTO boardCrudDTO){

        return boardCrudDAO.readStatementObject(sql,boardCrudDTO);
    }
}
