package com.wings.board.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;


@Alias("boardCrudDTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BoardCrudDTO {
	private String id;
	private String loginId;
	private String userId;

	private String title;
	private String writerName;
	private String content;

	private long multiFileId;
	private String createdWhen;
	private String updatedWhen;
	private String createdBy;
	private String updatedBy;
	private String rowNum;
	
	private String locDrive;
	
	private String locParentFolder;

	private String locChildFolder;
	
	private String fileName;
	
	private String fileExtension;
	
	private String fileCnt;

	private String hasFile;
	
	private String fileMeta;
	
	

	
	
}
