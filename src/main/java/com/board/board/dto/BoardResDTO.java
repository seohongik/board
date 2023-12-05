package com.board.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BoardResDTO {
	
	private String resDescription;
	private int code;

}
