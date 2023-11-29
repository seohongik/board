package com.board.board.dao;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import com.board.board.dto.BoardAuthDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Component
public class BoardAuthDAO {
    

	private  SqlSessionTemplate  sqlSessionTemplate;
    //@Autowired
	public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}
   
    public List<BoardAuthDTO> readStatementList(String sql,BoardAuthDTO authDTO) {

    	return sqlSessionTemplate.selectList(sql, authDTO);
    }

    public Map<String ,BoardAuthDTO> readStatementMap(String sql,BoardAuthDTO authDTO,String userId) {

    	return sqlSessionTemplate.selectMap(sql, authDTO,userId);
    }

    public void createStatement(String sql, BoardAuthDTO authDTO) {

        sqlSessionTemplate.insert(sql, authDTO);
    }
    
    public int deleteStatement(String sql, BoardAuthDTO authDTO) {

        return sqlSessionTemplate.delete(sql,authDTO);
    }
    
    public int updateStatement(String sql,BoardAuthDTO authDTO) {

        return sqlSessionTemplate.update(sql,authDTO);
    }

}
