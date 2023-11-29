package com.board.board.serviceImpl;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.board.board.dataMaker.*;
import com.board.board.dto.BoardCrudDTO;
import com.board.board.dto.BoardPageDTO;
import com.board.board.dto.BoardResDTO;
import com.board.board.service.BoardCrudService;
import com.board.board.sqlMaker.SqlMaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class BoardCrudServiceImpl implements BoardCrudService {


    @Autowired
    private  SqlMaker sqlMaker;
    @Autowired
    private  DataMakeBoardListWithPaging dataMakeBoardListWithPaging;
    @Autowired
    private  DataMakeBoardDetail dataMakeBoardDetail;
    @Autowired
    private  DataMakeWithFile dataMakeWithFile;
    @Autowired
    private  DataDeleteAllById dataDeleteAllById;
    @Autowired
    private  DataUpdateWithFile dataUpdateWithFile;



    public BoardCrudServiceImpl(SqlMaker sqlMaker,DataMakeBoardListWithPaging dataMakeBoardListWithPaging, DataMakeBoardDetail dataMakeBoardDetail, DataMakeWithFile dataMakeWithFile, DataDeleteAllById dataDeleteAllById, DataUpdateWithFile dataUpdateWithFile) {
        this.sqlMaker = sqlMaker;
        this.dataMakeBoardListWithPaging = dataMakeBoardListWithPaging;
        this.dataMakeBoardDetail = dataMakeBoardDetail;
        this.dataMakeWithFile = dataMakeWithFile;
        this.dataDeleteAllById = dataDeleteAllById;
        this.dataUpdateWithFile = dataUpdateWithFile;

    }

    @Transactional(readOnly = true)
    public String calcRn(String id) {
        String rn = sqlMaker.readString("com.board.board.mappers.boardCrud.selectRowNumById", id);
        BoardPageDTO boardPageDTO = new BoardPageDTO();
        return String.valueOf(boardPageDTO.calcPageNum(Integer.parseInt(rn)));
    }


    public List<BoardCrudDTO> showAllBoardDataWithPaging(String pageNumStr, String amountStr, Map<String, BoardPageDTO> pageMap) {
        int total = sqlMaker.readCount("com.board.board.mappers.boardCrud.selectCountAll",new BoardCrudDTO());
        BoardPageDTO boardPageDTO=dataMakeBoardListWithPaging.init(pageNumStr, amountStr,total);
        List<BoardCrudDTO> list = sqlMaker.readList("com.board.board.mappers.boardCrud.selectBoardListDataWithPaging", boardPageDTO);
        dataMakeBoardListWithPaging.paging(pageMap, boardPageDTO);
        dataMakeBoardListWithPaging.hasFileData(list);

        return list;

    }

    public List<BoardCrudDTO> showBoardDetail(String id, String userId, Map<String, String> textMap) throws RuntimeException {
        BoardCrudDTO boardCrudDTO = new BoardCrudDTO();
        boardCrudDTO.setId(id);
        boardCrudDTO.setUserId(userId);
        List<BoardCrudDTO> list = sqlMaker.readList("com.board.board.mappers.boardCrud.selectBoardDetailById", boardCrudDTO);
        dataMakeBoardDetail.init(list, textMap);
        List<BoardCrudDTO> multiFileNameList = new ArrayList<>();

        for (BoardCrudDTO infoAllDto : list) {

            if(infoAllDto.getFileMeta()!=null) {
                BoardCrudDTO boardCrudDTOWithFileData = sqlMaker.read("com.board.board.mappers.boardCrud.selectFileNameByUsingFileMetaById", infoAllDto);
                multiFileNameList.add(boardCrudDTOWithFileData);
            }
        }

        return multiFileNameList;

    }

    @Transactional(rollbackFor = Exception.class)
    public void insertBoardDataWithFile(MultipartFile[] files,String[] uploadTime, BoardCrudDTO boardCrudDTOReq, HttpSession httpSession) throws UnsupportedEncodingException {
        Optional<MultipartFile[]> mapFilesCouldNull = Optional.ofNullable(files);
        Optional<String[]> uploadTimeCouldNull = Optional.ofNullable(uploadTime);

        BoardCrudDTO boardCrudDTOText = new BoardCrudDTO();
        dataMakeWithFile.init(boardCrudDTOText, boardCrudDTOReq, httpSession);


        sqlMaker.create("com.board.board.mappers.boardCrud.insertMasterTbl", boardCrudDTOText);
        BoardCrudDTO selectMaxIdWithDTO = sqlMaker.read("com.board.board.mappers.boardCrud.selectMaxIdByUserIDAndWriterName", boardCrudDTOText);
        boardCrudDTOText.setId(selectMaxIdWithDTO.getId());


        Map<String, String> locmap = new HashMap<String, String>();
        takeLocFileDrive(locmap, boardCrudDTOText);
        File psyFolder = dataMakeWithFile.makeFolder(boardCrudDTOText);

        if (uploadTimeCouldNull.isPresent() && uploadTimeCouldNull.get().length != 0) {


            for(int i=0; i<uploadTimeCouldNull.get().length; i++) {
                BoardCrudDTO insertFile = dataMakeWithFile.fileDataInit(mapFilesCouldNull.get()[i],uploadTimeCouldNull.get()[i], boardCrudDTOText);
                insertFile.setMultiFileId(i);
                sqlMaker.create("com.board.board.mappers.boardCrud.insertDetailTbl", insertFile);
                dataMakeWithFile.makePsyFile(uploadTimeCouldNull.get()[i], mapFilesCouldNull.get()[i], psyFolder);
            }

        }

    }


    @Transactional(rollbackFor = RuntimeException.class)
    public ResponseEntity<BoardResDTO> deleteAllDataByID(BoardCrudDTO boardCrudDTODeleteParam) {

        Map<String, String> map = new HashMap<String, String>();
        takeLocFileDrive(map, boardCrudDTODeleteParam);
        File psyFolder = dataDeleteAllById.getFolder(boardCrudDTODeleteParam);

        log.error("psyFolder:{}",psyFolder.getPath());
        File[] files = psyFolder.listFiles();

        // 디렉토리 엔트리가 있으면 삭제
        for (File entry : files) {
            entry.delete();
        }


        sqlMaker.delete("com.board.board.mappers.boardCrud.deleteMasterByIdAndUserId", boardCrudDTODeleteParam);
        sqlMaker.delete("com.board.board.mappers.boardCrud.deleteDetailByIdAndUserId", boardCrudDTODeleteParam);
        List<BoardCrudDTO> selectByIdAfterDelete = sqlMaker.readList("com.board.board.mappers.boardCrud.selectJoinDataByIdAndMultiFileId", boardCrudDTODeleteParam);
        return dataDeleteAllById.isStatus(selectByIdAfterDelete);
    }


    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<BoardResDTO> updateBoardWithFile(MultipartFile[] updateFilesWithNull, String[] updateTimesWithNull, Map<String, String> updatedFileMapsWithNull, String[] deleteFilesConditionWithNull, BoardCrudDTO boardCrudDTOReq, HttpSession httpSession) throws Exception {

        BoardResDTO boardResDTO = new BoardResDTO();
        BoardCrudDTO boardCrudDTOText = new BoardCrudDTO();
        Optional<MultipartFile[]> updatedFilesCouldNull = Optional.ofNullable(updateFilesWithNull);
        Optional<String[]> updatedTimesCouldNull = Optional.ofNullable(updateTimesWithNull);
        Optional<String[]> deleteFilesCondition = Optional.ofNullable(deleteFilesConditionWithNull);
        dataUpdateWithFile.init(boardCrudDTOReq, boardCrudDTOText, httpSession);


        Map<String, String> map = new HashMap<String, String>();
        takeLocFileDrive(map, boardCrudDTOText);
        File psyFolder = dataUpdateWithFile.getFolder(boardCrudDTOText);

        if (deleteFilesCondition.isPresent() && deleteFilesCondition.get().length != 0) {

            for (int i = 0; i < deleteFilesCondition.get().length; i++) {
                boardCrudDTOText.setFileMeta(deleteFilesCondition.get()[i]);
                BoardCrudDTO boardDTOWithIdAndMultiFileId = sqlMaker.read("com.board.board.mappers.boardCrud.selectIdAndMultiFileIdByIdAndFileMeta", boardCrudDTOText);
                boardCrudDTOText.setMultiFileId(boardDTOWithIdAndMultiFileId.getMultiFileId());
                BoardCrudDTO boardCrudDTOWithFileName = sqlMaker.read("com.board.board.mappers.boardCrud.selectFileNameByUsingFileMetaById", boardCrudDTOText);
                String fileName =boardCrudDTOWithFileName.getFileName();
                dataUpdateWithFile.deleteCurrentPysFile(psyFolder, fileName);
                sqlMaker.delete("com.board.board.mappers.boardCrud.detailDeleteByMultiFileID", boardCrudDTOText);

            }

        }

        int updateCtn = sqlMaker.update("com.board.board.mappers.boardCrud.updateBoardMasterByIdAndWriterName", boardCrudDTOText);

        if (updatedTimesCouldNull.isPresent()&& updatedFilesCouldNull.isPresent() && updatedTimesCouldNull.get().length != 0) {


            int multiFileId = 0;
            if (sqlMaker.readObject("com.board.board.mappers.boardCrud.selectMaxMultiFileId", boardCrudDTOText) != null) {
                multiFileId = (int) sqlMaker.readObject("com.board.board.mappers.boardCrud.selectMaxMultiFileId", boardCrudDTOText) + 1;

            }

            for(int i=0; i<updatedTimesCouldNull.get().length; i++) {
                BoardCrudDTO insertFile = dataMakeWithFile.fileDataInit(updatedFilesCouldNull.get()[i],updatedTimesCouldNull.get()[i], boardCrudDTOText);
                insertFile.setMultiFileId(multiFileId);
                sqlMaker.create("com.board.board.mappers.boardCrud.insertDetailTbl", insertFile);
                dataMakeWithFile.makePsyFile(updatedTimesCouldNull.get()[i], updatedFilesCouldNull.get()[i], psyFolder);
                multiFileId++;
            }

        }

        return dataUpdateWithFile.isStatus(updateCtn, boardResDTO);

    }

    public void downloadFile(HttpServletResponse response, String id, String userId, String fileMeta) throws UnsupportedEncodingException {


        BoardCrudDTO boardCrudDTO = new BoardCrudDTO();
        boardCrudDTO.setId(id);
        boardCrudDTO.setUserId(userId);
        boardCrudDTO.setFileMeta(fileMeta);

        Map<String, String> map = new HashMap<String, String>();
        takeLocFileDrive(map, boardCrudDTO);



        BoardCrudDTO boardCrudDTOWithIdAndMultiFileId = sqlMaker.read("com.board.board.mappers.boardCrud.selectIdAndMultiFileIdByIdAndFileMeta", boardCrudDTO);
        boardCrudDTO.setMultiFileId(boardCrudDTOWithIdAndMultiFileId.getMultiFileId());

        BoardCrudDTO boardCrudDTOWithFileName = sqlMaker.read("com.board.board.mappers.boardCrud.selectFileNameByUsingFileMetaById", boardCrudDTO);
        boardCrudDTO.setFileName(boardCrudDTOWithFileName.getFileName());

        String downStr = (boardCrudDTO.getLocDrive() + File.separatorChar + boardCrudDTO.getLocParentFolder() + File.separatorChar + boardCrudDTO.getLocChildFolder()+File.separatorChar+boardCrudDTO.getFileName());

        File file = new File(downStr);
        String fileNameOrg = new String(boardCrudDTO.getFileName().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
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



    public void takeLocFileDrive(Map<String, String> map, BoardCrudDTO boardCrudDTO) {

        String locDrive = "/Users/seohong-ig/Desktop";
        map.put("locDrive", locDrive);
        String locParentFolder = "upload";
        map.put("locParentFolder", locParentFolder);
        String locChildFolder = boardCrudDTO.getId() + boardCrudDTO.getUserId();
        map.put("locChildFolder", locChildFolder);
        boardCrudDTO.setLocDrive(locDrive);
        boardCrudDTO.setLocParentFolder(locParentFolder);
        boardCrudDTO.setLocChildFolder(locChildFolder);
    }




}
