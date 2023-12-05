package com.board.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

@Data
@NoArgsConstructor
@Alias("boardValidDTO")
public class BoardValidDTO {

	
	private BoardCrudDTO boardCrudDTO;

	public BoardValidDTO( BoardCrudDTO boardCrudDTO){

		this.boardCrudDTO = boardCrudDTO;

	}
	
	public String getWriterName() {
		return boardCrudDTO.getWriterName();
	}
	
	public String getContent() {
		return boardCrudDTO.getContent();
	}
	public String getTitle() {return boardCrudDTO.getTitle();
	}
	
	
}
