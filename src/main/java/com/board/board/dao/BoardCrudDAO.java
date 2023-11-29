package com.board.board.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.board.board.dto.BoardCrudDTO;
import com.board.board.dto.BoardPageDTO;
import com.board.board.dto.BoardValidDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BoardCrudDAO {


	private  SqlSessionTemplate  sqlSessionTemplate;
    //@Autowired
	public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}
	
    public List<BoardCrudDTO> readStatementList(String sql,BoardCrudDTO boardDto) {

    	return sqlSessionTemplate.selectList(sql, boardDto);
    }
    
    public List<BoardCrudDTO> readStatementList(String sql,BoardPageDTO  boardPageDTO) {

    	return sqlSessionTemplate.selectList(sql, boardPageDTO);
    }
    
    public BoardCrudDTO readStatement(String sql,BoardCrudDTO boardDto) {

    	return sqlSessionTemplate.selectOne(sql, boardDto);
    }
    
    public int readStatementCount(String sql,BoardCrudDTO boardDto) {

    	return sqlSessionTemplate.selectOne(sql, boardDto);
    }
    
    public Object readStatementObject(String sql,BoardCrudDTO boardDto) {

    	return sqlSessionTemplate.selectOne(sql, boardDto);
    }

    public int createStatement(String sql, BoardCrudDTO boardDTO) {

       return sqlSessionTemplate.insert(sql,boardDTO);
    }
    
    public int deleteStatement(String sql, BoardCrudDTO boardDTO) {

        return sqlSessionTemplate.delete(sql,boardDTO);
    }
    
    public int updateStatement(String sql, BoardCrudDTO boardDTO) {

        return sqlSessionTemplate.update(sql,boardDTO);
    }
    
    public String readStatementString(String sql, String id) {

        return sqlSessionTemplate.selectOne(sql,id);
    }
    
	public List<BoardValidDTO> readStatementList(String sql, BoardValidDTO boardValidDTO){
		
		return sqlSessionTemplate.selectList(sql,boardValidDTO );
	}
    
    

}
