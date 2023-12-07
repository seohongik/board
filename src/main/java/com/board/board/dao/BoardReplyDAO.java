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

    public List<BoardReplyDTO> readStatementList(String sql, BoardReplyDTO boardReplyDTO) {

        return sqlSessionTemplate.selectList(sql, boardReplyDTO);
    }

    public BoardReplyDTO readStatement(String sql, BoardReplyDTO boardReplyDTO) {

        return sqlSessionTemplate.selectOne(sql, boardReplyDTO);
    }

    public int readStatementCount(String sql, BoardReplyDTO boardReplyDTO) {

        return sqlSessionTemplate.selectOne(sql, boardReplyDTO);
    }

    public Object readStatementObject(String sql, BoardReplyDTO boardReplyDTO) {

        return sqlSessionTemplate.selectOne(sql, boardReplyDTO);
    }

    public void createStatement(String sql, BoardReplyDTO boardReplyDTO) {

        sqlSessionTemplate.insert(sql, boardReplyDTO);
    }

    public void deleteStatement(String sql, BoardReplyDTO boardReplyDTO) {

        sqlSessionTemplate.delete(sql, boardReplyDTO);
    }

    public void deleteStatement(String sql, int id) {

        sqlSessionTemplate.delete(sql, id);
    }

    public void updateStatement(String sql, BoardReplyDTO boardReplyDTO) {

        sqlSessionTemplate.update(sql, boardReplyDTO);
    }

    public String readStatementString(String sql, String id) {

        return sqlSessionTemplate.selectOne(sql, id);
    }


    public List<String> readStatementStringList(String sql, BoardReplyDTO boardReplyDTO) {
        return sqlSessionTemplate.selectList(sql, boardReplyDTO);
    }
}
