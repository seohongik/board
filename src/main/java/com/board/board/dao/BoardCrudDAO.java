package com.board.board.dao;

import com.board.board.dto.BoardCrudDTO;
import com.board.board.dto.BoardPageDTO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BoardCrudDAO {
    private SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
        this.sqlSessionTemplate = sqlSessionTemplate;
    }

    public List<BoardCrudDTO> readPreStatementList(String sql, BoardCrudDTO boardDto) {

        return sqlSessionTemplate.selectList(sql, boardDto);
    }

    public List<BoardCrudDTO> readPreStatementList(String sql, BoardPageDTO boardPageDTO) {

        return sqlSessionTemplate.selectList(sql, boardPageDTO);
    }

    public BoardCrudDTO readPreStatement(String sql, BoardCrudDTO boardDto) {

        return sqlSessionTemplate.selectOne(sql, boardDto);
    }

    public int readPreStatementCount(String sql, BoardCrudDTO boardDto) {

        return sqlSessionTemplate.selectOne(sql, boardDto);
    }

    public Object readPreStatementObject(String sql, BoardCrudDTO boardDto) {

        return sqlSessionTemplate.selectOne(sql, boardDto);
    }

    public void createPreStatement(String sql, BoardCrudDTO boardDTO) {

        sqlSessionTemplate.insert(sql, boardDTO);
    }

    public void deletePreStatement(String sql, BoardCrudDTO boardDTO) {

        sqlSessionTemplate.delete(sql, boardDTO);
    }

    public int updatePreStatement(String sql, BoardCrudDTO boardDTO) {

        return sqlSessionTemplate.update(sql, boardDTO);
    }

    public String readPreStatement(String sql, String id) {

        return sqlSessionTemplate.selectOne(sql, id);
    }


}
