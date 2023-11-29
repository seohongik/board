package com.board.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Alias("boardValidDTO")
public class BoardValidDTO {

	
	private BoardCrudDTO boardCrudDTO;
	private String result;
	

	
	public String getWriterName() {
		return getBoardCrudDTO().getWriterName();
	}
	
	public String getContent() {
		return getBoardCrudDTO().getContent();
	}
	public String getTitle() {
		return getBoardCrudDTO().getTitle();
	}
	
	
}
