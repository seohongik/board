package com.board.board.sqlMaker;

import com.board.board.dto.BoardCrudDTO;
import com.board.board.dto.BoardPageDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SqlMaker {

    public List<BoardCrudDTO> readList(String sql, BoardCrudDTO boardCrudDTO);

    public BoardCrudDTO read(String sql,BoardCrudDTO boardCrudDTO);

    public int create(String sql,BoardCrudDTO boardCrudDTO);

    public int update(String sql,BoardCrudDTO boardCrudDTO);

    public int delete(String sql,BoardCrudDTO boardCrudDTO);
    @Transactional(readOnly = true)
    public int readCount(String sql,BoardCrudDTO boardCrudDTO);

    @Transactional(readOnly = true)
    public  List<BoardCrudDTO>  readList(String sql, BoardPageDTO boardPageDTO);

    String readString(String sql, String id);

    Object readObject(String sql, BoardCrudDTO boardCrudDTOText);
}
