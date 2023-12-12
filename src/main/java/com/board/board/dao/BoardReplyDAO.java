package com.board.board.dao;

import com.board.board.dto.BoardReplyDTO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BoardReplyDAO {
    private SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
        this.sqlSessionTemplate = sqlSessionTemplate;
    }

    public List<BoardReplyDTO> readPreStatementList(String sql, BoardReplyDTO boardReplyDTO) {

        return sqlSessionTemplate.selectList(sql, boardReplyDTO);
    }

    public BoardReplyDTO readPreStatement(String sql, BoardReplyDTO boardReplyDTO) {

        return sqlSessionTemplate.selectOne(sql, boardReplyDTO);
    }

    public int readPreStatementCount(String sql, BoardReplyDTO boardReplyDTO) {

        return sqlSessionTemplate.selectOne(sql, boardReplyDTO);
    }

    public Object readPreStatementObject(String sql, BoardReplyDTO boardReplyDTO) {

        return sqlSessionTemplate.selectOne(sql, boardReplyDTO);
    }

    public void createPreStatement(String sql, BoardReplyDTO boardReplyDTO) {

        sqlSessionTemplate.insert(sql, boardReplyDTO);
    }

    public void deletePreStatement(String sql, BoardReplyDTO boardReplyDTO) {

        sqlSessionTemplate.delete(sql, boardReplyDTO);
    }

    public void deletePreStatement(String sql, int id) {

        sqlSessionTemplate.delete(sql, id);
    }

    public void updatePreStatement(String sql, BoardReplyDTO boardReplyDTO) {

        sqlSessionTemplate.update(sql, boardReplyDTO);
    }

    public String readPreStatementString(String sql, String id) {

        return sqlSessionTemplate.selectOne(sql, id);
    }


    public List<String> readPreStatementStringList(String sql, BoardReplyDTO boardReplyDTO) {
        return sqlSessionTemplate.selectList(sql, boardReplyDTO);
    }
}
