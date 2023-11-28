package com.wings.board.serviceImpl;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.wings.board.dao.BoardCrudDAO;
import com.wings.board.dto.BoardCrudDTO;
import com.wings.board.dto.BoardPageDTO;
import com.wings.board.dto.BoardResDTO;
import com.wings.board.service.BoardCrudService;

@Slf4j
public class BoardCrudServiceImpl implements BoardCrudService{
	
	private BoardCrudDAO boardCrudDAO;

	public void setBoardCrudDAO(BoardCrudDAO boardCrudDAO) {
		this.boardCrudDAO = boardCrudDAO;
	}

	@Transactional(readOnly = true)
	public int readTotalRowNum() {
		return boardCrudDAO.readStatementCount("com.wings.board.mappers.selectCountAll", new BoardCrudDTO());
	}

	
	@Transactional(readOnly = true)
	public List<BoardCrudDTO> showBoardListWithPaging(String pageNumStr, String amountStr,Map<String, BoardPageDTO> pageMap) {

		int pageNumInt = 0;
		if ("".equals(pageNumStr)) {
			pageNumInt = 1;
		} else {
			pageNumInt = Integer.parseInt(pageNumStr);
		}
		int amount = Integer.parseInt(amountStr);
		int total = readTotalRowNum();

		BoardPageDTO boardPageDTO = new BoardPageDTO(pageNumInt, amount, total);

		int numMultiAmount = boardPageDTO.getPageNum() * boardPageDTO.getAmount();
		int numMinusOneMultiAmount = (boardPageDTO.getPageNum() - 1) * boardPageDTO.getAmount();

		boardPageDTO.setNumMutiAmount(numMultiAmount);
		boardPageDTO.setNumMinusOneMutiAmount(numMinusOneMultiAmount);

		List<BoardCrudDTO> list = boardCrudDAO
				.readStatementList("com.wings.board.mappers.selectBoardListDataWithPaging", boardPageDTO);

		pageMap.put("pageMaker", boardPageDTO);
		
		if(!list.isEmpty()) {

            for (BoardCrudDTO boardCrudDTO : list) {

                boardCrudDTO.setUpdatedWhen(boardCrudDTO.getUpdatedWhen().substring(0, 10));

                if (boardCrudDTO.getFileCnt() != null && Integer.parseInt(boardCrudDTO.getFileCnt()) > 0) {
                    boardCrudDTO.setHasFile("Y");
                } else {
                    boardCrudDTO.setHasFile("N");
                }
            }
		}
		return list;
	}
	
	public void showBoardDetail(String id, Map<String, String> textMap, List<BoardCrudDTO> multiFileNameList)
			throws RuntimeException {

		BoardCrudDTO boardCrudDTO = new BoardCrudDTO();
		boardCrudDTO.setId(id);
		
		List<BoardCrudDTO> list = boardCrudDAO
				.readStatementList("com.wings.board.mappers.selectBoardDetailById", boardCrudDTO);

		for (BoardCrudDTO infoAllDto : list) {
			textMap.put("id", infoAllDto.getId());
			textMap.put("userId", infoAllDto.getUserId());
			textMap.put("title", infoAllDto.getTitle());
			textMap.put("writerName", infoAllDto.getWriterName());
			textMap.put("content", infoAllDto.getContent());
			textMap.put("updatedWhen", infoAllDto.getUpdatedWhen());
			boardCrudDTO.setUserId(infoAllDto.getUserId());
			boardCrudDTO.setId(infoAllDto.getId());
		}
		
		multiFileNameList.addAll(fileControllerDetail(boardCrudDTO));

		Map<String,String> map =new HashMap<String,String>();
		retriveLoc( map, boardCrudDTO);

	}

	@Transactional(rollbackFor = Exception.class)
	public void insertBoardDataWithFile( MultipartFile[] files   ,Map<String,String> filesWithNull, BoardCrudDTO boardCrudDTOReq,
			HttpSession httpSession) throws UnsupportedEncodingException {

		BoardCrudDTO boardCrudDTOText = new BoardCrudDTO();

		
		Optional<MultipartFile[] > mapFilesWithNull = Optional.ofNullable(files);
		Optional<Map<String,String>> mapFilesWithUploadTime = Optional.ofNullable(filesWithNull);

		String writerName = new String(boardCrudDTOReq.getWriterName().getBytes("8859_1"), "utf-8");
		String updatedWhen = new String(boardCrudDTOReq.getUpdatedWhen().getBytes("8859_1"), "utf-8");
		String title = new String(boardCrudDTOReq.getTitle().getBytes("8859_1"), "utf-8");
		String content = new String(boardCrudDTOReq.getContent().getBytes("8859_1"), "utf-8");

		boardCrudDTOText.setContent(content);
		boardCrudDTOText.setUpdatedWhen(updatedWhen);
		boardCrudDTOText.setWriterName(writerName);
		boardCrudDTOText.setTitle(title);
		boardCrudDTOText.setUserId((String) httpSession.getAttribute("userIdSess"));

		boardCrudDAO.createStatement("com.wings.board.mappers.insertMasterTbl", boardCrudDTOText);

		BoardCrudDTO selectMaxIdWithDTO = boardCrudDAO.readStatement(
				"com.wings.board.mappers.selectMaxIdByUserIDAndWriterName", boardCrudDTOText);


		boardCrudDTOText.setId(selectMaxIdWithDTO.getId());

		Map<String,String> map =new HashMap<String, String>();
		retriveLoc(map, boardCrudDTOText);
		String locDrive = map.get("locDrive");
		boardCrudDTOText.setLocDrive(locDrive);
		String locParentFolder = map.get("locParentFolder");
		boardCrudDTOText.setLocParentFolder(locParentFolder);
		String locChildFolder =  map.get("locChildFolder");;
		boardCrudDTOText.setLocChildFolder(locChildFolder);

		File psycFolder = new File(locDrive + File.separatorChar + locParentFolder + File.separatorChar + locChildFolder);
		
		psycFolder.mkdirs();


		////////////////////////
		if (mapFilesWithUploadTime.isPresent()&& !mapFilesWithUploadTime.get().isEmpty()) {

			int multiFileId = 0;
					
			if ( boardCrudDAO.readStatementObject("com.wings.board.mappers.selectMaxMultiFileId", boardCrudDTOText) != null) {
				 multiFileId =(int) boardCrudDAO.readStatementObject("com.wings.board.mappers.selectMaxMultiFileId", boardCrudDTOText) + 1; 
			 
			}
			
			List<String> uploadTime  =mapFilesWithUploadTime.get().keySet().stream().collect(Collectors.toList());   
			List<String> createdFiles  =mapFilesWithUploadTime.get().values().stream().collect(Collectors.toList());   
			 
			for (int i = 0; i < createdFiles.size(); i++) {
				String fileNameWithExtension = new String(createdFiles.get(i));
				String fileName = fileNameWithExtension.substring(0, fileNameWithExtension.indexOf("."));
				String extension = fileNameWithExtension.substring(fileNameWithExtension.indexOf("."),fileNameWithExtension.length());
				String fileMeta = uploadTime.get(i);

				boardCrudDTOText.setFileMeta(fileMeta);
				boardCrudDTOText.setFileName(fileName);
				boardCrudDTOText.setFileExtension(extension);
				boardCrudDTOText.setMultiFileId(multiFileId);
				boardCrudDAO.createStatement("com.wings.board.mappers.insertDetailTbl",boardCrudDTOText);

				multiFileId++;
			}

			try {
				makePsycFile(mapFilesWithNull.get(), psycFolder);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}
	

	@Transactional(rollbackFor = RuntimeException.class)
	public ResponseEntity<BoardResDTO> deleteAllDataByID(BoardCrudDTO boardCrudDTODeleteParam) {


		Map<String,String> map =new HashMap<String, String>();
		retriveLoc(map, boardCrudDTODeleteParam);
		String locDrive = map.get("locDrive");
		boardCrudDTODeleteParam.setLocDrive(locDrive);
		String locParentFolder = map.get("locParentFolder");
		boardCrudDTODeleteParam.setLocParentFolder(locParentFolder);
		String locChildFolder =  map.get("locChildFolder");;
		boardCrudDTODeleteParam.setLocChildFolder(locChildFolder);

		String directoryPath = locDrive+File.separatorChar+  locParentFolder+File.separatorChar+ locChildFolder;
		List<Path> pathList = new ArrayList<Path>();
		try(Stream<Path> walk=Files.walk(Paths.get(directoryPath))){

			pathList = walk.filter(Files::isRegularFile).collect(Collectors.toList());

			for(int j=0; j<pathList.size(); j++) {

				File existFile = pathList.get(j).toFile();

				existFile.delete();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}

		boardCrudDAO.deleteStatement("com.wings.board.mappers.deleteMasterByIdAndUserId",	boardCrudDTODeleteParam);


		boardCrudDAO.deleteStatement("com.wings.board.mappers.deleteDetailByIdAndUserId",	boardCrudDTODeleteParam);


		List<BoardCrudDTO> selectByIdAfterDelete = boardCrudDAO.readStatementList(
				"com.wings.board.mappers.selectJoinDataByIdAndMultiFileId", boardCrudDTODeleteParam);

		if (selectByIdAfterDelete.isEmpty()) {
			BoardResDTO sucessBoardResDTO = new BoardResDTO();
			sucessBoardResDTO.setCode(200);
			sucessBoardResDTO.setResContent("success");
			return new ResponseEntity<BoardResDTO>(sucessBoardResDTO, HttpStatus.OK);
		} else {
			BoardResDTO failBoardResDTO = new BoardResDTO();
			failBoardResDTO.setCode(500);
			failBoardResDTO.setResContent("fail");
			return new ResponseEntity<BoardResDTO>(failBoardResDTO, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<BoardResDTO> updateBoard(
			   MultipartFile[] updateFilesWithNull
			,  String[] updateTimesWithNull
			,  Map<String,String>  updatedFileMapsWithNull
			,  String[] deleteFilesConditionWithNull
			,  BoardCrudDTO boardCrudDTOReq
			,  HttpSession httpSession
			
			) throws Exception {
		
		BoardResDTO boardResDTO = new BoardResDTO();
		BoardCrudDTO boardCrudDTOText = new BoardCrudDTO();
		
		Optional<MultipartFile[]> updatedFiles = Optional.ofNullable(updateFilesWithNull);
		Optional<String[]> updatedTimes = Optional.ofNullable(updateTimesWithNull);
		Optional<String[]> deleteFilesCondition = Optional.ofNullable(deleteFilesConditionWithNull);


		String id = new String(boardCrudDTOReq.getId().getBytes("8859_1"), "utf-8");
		String writerName = new String(boardCrudDTOReq.getWriterName().getBytes("8859_1"), "utf-8");
		String updatedWhen = new String(boardCrudDTOReq.getUpdatedWhen().getBytes("8859_1"), "utf-8");
		String title = new String(boardCrudDTOReq.getTitle().getBytes("8859_1"), "utf-8");
		String content = new String(boardCrudDTOReq.getContent().getBytes("8859_1"), "utf-8");

		String userId=(String) httpSession.getAttribute("userIdSess");
		
		boardCrudDTOText.setId(id);
		boardCrudDTOText.setContent(content);
		boardCrudDTOText.setUpdatedWhen(updatedWhen);
		boardCrudDTOText.setWriterName(writerName);
		boardCrudDTOText.setTitle(title);
		boardCrudDTOText.setUserId(userId);

		Map<String,String> map =new HashMap<String, String>();
		retriveLoc(map, boardCrudDTOText);
		String locDrive = map.get("locDrive");
		boardCrudDTOText.setLocDrive(locDrive);
		String locParentFolder = map.get("locParentFolder");
		boardCrudDTOText.setLocParentFolder(locParentFolder);
		String locChildFolder =  map.get("locChildFolder");;
		boardCrudDTOText.setLocChildFolder(locChildFolder);
		int multiFileId = 0;

		if ( boardCrudDAO.readStatementObject("com.wings.board.mappers.selectMaxMultiFileId", boardCrudDTOText) != null) {
			multiFileId =(int) boardCrudDAO.readStatementObject("com.wings.board.mappers.selectMaxMultiFileId", boardCrudDTOText) + 1;

		}

		File psycFolder = new File(locDrive+File.separatorChar+ locParentFolder+File.separatorChar+ locChildFolder);
		if (deleteFilesCondition.isPresent() && deleteFilesCondition.get().length!=0) {

			for(int i=0; i<deleteFilesCondition.get().length; i++) {

				boardCrudDTOText.setFileMeta(deleteFilesCondition.get()[i]);
				BoardCrudDTO boardDTOWithIdAndMultiFileId = boardCrudDAO.readStatement("com.wings.board.mappers.selectIdAndMultiFileIdByIdAndFileMeta", boardCrudDTOText);
				boardCrudDTOText.setMultiFileId(boardDTOWithIdAndMultiFileId.getMultiFileId());

				String fileName = boardCrudDAO.readStatement("com.wings.board.mappers.selectFileNameByUsingFileMetaById", boardCrudDTOText).getFileName();

				File delEachFile = new File(psycFolder.getPath()+File.separatorChar +fileName);
				delEachFile.delete();

				log.error("delEachFile:{}",delEachFile.getPath());

				boardCrudDAO.deleteStatement("com.wings.board.mappers.detailDeleteByMultiFileID",boardCrudDTOText);
			}

		}

		int updateCtn = boardCrudDAO.updateStatement("com.wings.board.mappers.updateBoardMasterByIdAndWriterName", boardCrudDTOText);

		if (updatedFiles.isPresent() && updatedFiles.get().length!=0) {

			for(int i=0; i<updatedFiles.get().length; i++) {
				String uploadTime = new String(updatedTimes.get()[i].getBytes("8859_1"), "utf-8");
				String fileName = new String(updatedFiles.get()[i].getOriginalFilename().getBytes("8859_1"), "utf-8");

				boardCrudDTOText.setFileMeta(uploadTime);
				boardCrudDTOText.setFileName(fileName.substring(0,fileName.indexOf(".")));
				boardCrudDTOText.setFileExtension(fileName.substring(fileName.indexOf("."),fileName.length()));
				boardCrudDTOText.setMultiFileId(multiFileId);

				boardCrudDAO.createStatement("com.wings.board.mappers.insertDetailTbl",boardCrudDTOText);

				makePsycFile(updatedFiles.get(),psycFolder );
			}

		}

		if (updateCtn != 1) {
			boardResDTO.setCode(500);
			boardResDTO.setResContent("fail");
			return new ResponseEntity<BoardResDTO>(boardResDTO, HttpStatus.INTERNAL_SERVER_ERROR);
		}
			
		boardResDTO.setCode(200);
		boardResDTO.setResContent("OK");
		return new ResponseEntity<BoardResDTO>(boardResDTO, HttpStatus.OK);
		
	}

	public void downloadFile(HttpServletResponse response, String id, String userId, String fileMeta) throws UnsupportedEncodingException {
		
		BoardCrudDTO boardCrudDTO = new BoardCrudDTO();

		boardCrudDTO.setId(id);
		boardCrudDTO.setUserId(userId);
		boardCrudDTO.setFileMeta(fileMeta);
		
		String downStr = "";
		Map<String,String> map = new HashMap<String, String>();
		retriveLoc(map, boardCrudDTO);
		
		BoardCrudDTO boardCrudDTOWithIdAndMultiFileId = boardCrudDAO.readStatement("com.wings.board.mappers.selectIdAndMultiFileIdByIdAndFileMeta", boardCrudDTO);
		boardCrudDTO.setUserId(userId);
		boardCrudDTO.setMultiFileId(boardCrudDTOWithIdAndMultiFileId.getMultiFileId());
		
		BoardCrudDTO  boardCrudDTOWithFileName = boardCrudDAO.readStatement("com.wings.board.mappers.selectFileNameByUsingFileMetaById", boardCrudDTO);
		downStr = (map.get("locDrive")+File.separatorChar   +map.get("locParentFolder")+File.separatorChar +map.get("locChildFolder")+File.separatorChar +(boardCrudDTOWithFileName.getFileName()));
		
		File file = new File(downStr);
		String fileNameOrg = new String(boardCrudDTOWithFileName.getFileName().getBytes(StandardCharsets.UTF_8),StandardCharsets.ISO_8859_1);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileNameOrg + "\";");
		response.setHeader("Content-Transfer-Encoding", "binary");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Length", "" + file.length());
		response.setHeader("Pragma", "no-cache;");
		response.setHeader("Expires", "-1;");

		try (FileInputStream fis = new FileInputStream(downStr);
				OutputStream out = response.getOutputStream();) {
			int readCount = 0;
			byte[] buffer = new byte[1024];
			while ((readCount = fis.read(buffer)) != -1) {
				out.write(buffer, 0, readCount);
			}
		} catch (Exception ex) {
			throw new RuntimeException("file Save Error");
		}
	}

	@Transactional(readOnly = true)
	public String calcRn(String id) {
		String rn = boardCrudDAO.readStatementString("com.wings.board.mappers.selectRowNumById", id);
		BoardPageDTO boardPageDTO = new BoardPageDTO();
		return String.valueOf(boardPageDTO.calcPageNum(Integer.parseInt(rn)));
	}
	
	private void makePsycFile(MultipartFile[] files, File existPysicfolder ) throws Exception {
		
		for (int i = 0; i < files.length; i++) {
			
			String fileNameWithExtention = new String(files[i].getOriginalFilename().getBytes("8859_1"), "utf-8");
			try (FileOutputStream fos = new FileOutputStream(existPysicfolder.getPath() +File.separatorChar + fileNameWithExtention);
					InputStream is = files[i].getInputStream();) {
				int readCount = 0;
				byte[] buffer = new byte[1024];
				while ((readCount = is.read(buffer)) != -1) {
					fos.write(buffer, 0, readCount);
				}
			} catch (Exception ex) {
				throw new RuntimeException("file Save Error");
			}
		}
	}
	
	public void retriveLoc(Map<String,String> map, BoardCrudDTO boardCrudDTO) {
		
		String locDrive = "/Users/seohong-ig/Desktop";
		map.put("locDrive", locDrive);
		String locParentFolder = "upload";
		map.put("locParentFolder", locParentFolder);
		String locChildFolder =  boardCrudDTO.getId() +boardCrudDTO.getUserId();
		map.put("locChildFolder", locChildFolder);
	}


	public List<BoardCrudDTO> fileControllerDetail(BoardCrudDTO boardCrudDTOWithOutFileData) {
		
		List<BoardCrudDTO> fileInfoList = boardCrudDAO.readStatementList("com.wings.board.mappers.selectFileDataById", boardCrudDTOWithOutFileData);

		for(int i=0; i<fileInfoList.size(); i++) {
			
			boardCrudDTOWithOutFileData.setMultiFileId(fileInfoList.get(i).getMultiFileId());
			boardCrudDTOWithOutFileData.setFileMeta(fileInfoList.get(i).getFileMeta());
			boardCrudDTOWithOutFileData.setLocDrive(fileInfoList.get(0).getLocDrive());
			boardCrudDTOWithOutFileData.setLocParentFolder(fileInfoList.get(0).getLocParentFolder());
			boardCrudDTOWithOutFileData.setLocChildFolder(fileInfoList.get(0).getLocChildFolder());

			BoardCrudDTO boardCrudDTOWithFileData=boardCrudDAO.readStatement("com.wings.board.mappers.selectFileNameByUsingFileMetaById", boardCrudDTOWithOutFileData);
			boardCrudDTOWithOutFileData.setFileName(boardCrudDTOWithFileData.getFileName());
			fileInfoList.get(i).setFileName(boardCrudDTOWithOutFileData.getFileName());
					
		}
		return fileInfoList;
	}
	
}
