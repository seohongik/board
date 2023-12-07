package com.board.board.serviceImpl;

import com.board.board.dao.BoardCrudDAO;
import com.board.board.dao.BoardReplyDAO;
import com.board.board.dataMaker.*;
import com.board.board.dto.BoardCrudDTO;
import com.board.board.dto.BoardPageDTO;
import com.board.board.dto.BoardReplyDTO;
import com.board.board.dto.BoardResDTO;
import com.board.board.service.BoardCrudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardCrudServiceImpl implements BoardCrudService {

    private final BoardCrudDAO boardCrudDAO;

    private final BoardReplyDAO boardReplyDAO;

    private final DataMakeBoardListWithPaging dataMakeBoardListWithPaging;

    private final DataMakeBoardDetail dataMakeBoardDetail;

    private final DataMakeWithFile dataMakeWithFile;

    private final DataDeleteAllById dataDeleteAllById;

    private final DataUpdateWithFile dataUpdateWithFile;

    private final DataMakeReply dataMakeReply;

    private final DataDeleteReply dataDeleteReply;

    private final DataUpdateReply dataUpdateReply;


    @Transactional(readOnly = true)
    public String calcRowNum(String id) {
        String rn = boardCrudDAO.readStatementString("com.board.board.mappers.boardCrud.selectRowNumById", id);
        BoardPageDTO boardPageDTO = new BoardPageDTO();
        return String.valueOf(boardPageDTO.calcPageNum(Integer.parseInt(rn)));
    }

    @Transactional(readOnly = true)
    public List<BoardCrudDTO> showAllBoardDataWithPaging(String pageNumStr, String amountStr, Map<String, BoardPageDTO> pageMap) {
        int total = boardCrudDAO.readStatementCount("com.board.board.mappers.boardCrud.selectCountAll", new BoardCrudDTO());
        BoardPageDTO boardPageDTO = dataMakeBoardListWithPaging.init(pageNumStr, amountStr, total);
        List<BoardCrudDTO> list = boardCrudDAO.readStatementList("com.board.board.mappers.boardCrud.selectBoardListDataWithPaging", boardPageDTO);
        dataMakeBoardListWithPaging.paging(pageMap, boardPageDTO);
        dataMakeBoardListWithPaging.hasFileData(list);

        return list;

    }

    @Transactional(readOnly = true)
    public List<BoardCrudDTO> showBoardDetail(String id, String userId, Map<String, String> textMap) throws RuntimeException {
        BoardCrudDTO boardCrudDTO = new BoardCrudDTO();
        boardCrudDTO.setId(id);
        boardCrudDTO.setUserId(userId);
        List<BoardCrudDTO> list = boardCrudDAO.readStatementList("com.board.board.mappers.boardCrud.selectBoardDetailById", boardCrudDTO);
        dataMakeBoardDetail.init(list, textMap);
        List<BoardCrudDTO> multiFileNameList = new ArrayList<>();

        for (BoardCrudDTO infoAllDto : list) {

            if (infoAllDto.getFileMeta() != null) {
                BoardCrudDTO boardCrudDTOWithFileData = boardCrudDAO.readStatement("com.board.board.mappers.boardCrud.selectFileNameByUsingFileMetaById", infoAllDto);
                multiFileNameList.add(boardCrudDTOWithFileData);
            }
        }

        return multiFileNameList;

    }

    @Transactional(rollbackFor = Exception.class)
    public void insertBoardDataWithFile(MultipartFile[] files, String[] uploadTime, BoardCrudDTO boardCrudDTOReq, HttpSession httpSession) throws UnsupportedEncodingException {
        Optional<MultipartFile[]> mapFilesCouldNull = Optional.ofNullable(files);
        Optional<String[]> uploadTimeCouldNull = Optional.ofNullable(uploadTime);

        BoardCrudDTO boardCrudDTOText = new BoardCrudDTO();
        dataMakeWithFile.init(boardCrudDTOText, boardCrudDTOReq, httpSession);


        boardCrudDAO.createStatement("com.board.board.mappers.boardCrud.insertMasterTbl", boardCrudDTOText);
        BoardCrudDTO selectMaxIdWithDTO = boardCrudDAO.readStatement("com.board.board.mappers.boardCrud.selectMaxIdByUserIDAndWriterName", boardCrudDTOText);
        boardCrudDTOText.setId(selectMaxIdWithDTO.getId());


        Map<String, String> locmap = new HashMap<>();
        takeLocFileDrive(locmap, boardCrudDTOText);
        File psyFolder = dataMakeWithFile.makeFolder(boardCrudDTOText);

        if (uploadTimeCouldNull.isPresent() && uploadTimeCouldNull.get().length != 0 && mapFilesCouldNull.isPresent()) {


            for (int i = 0; i < uploadTimeCouldNull.get().length; i++) {
                BoardCrudDTO insertFile = dataMakeWithFile.fileDataInit(mapFilesCouldNull.get()[i], uploadTimeCouldNull.get()[i], boardCrudDTOText);
                insertFile.setMultiFileId(i);
                boardCrudDAO.createStatement("com.board.board.mappers.boardCrud.insertDetailTbl", insertFile);
                dataMakeWithFile.makePsyFile(uploadTimeCouldNull.get()[i], mapFilesCouldNull.get()[i], psyFolder);
            }

        }

    }


    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<BoardResDTO> deleteAllDataByID(BoardCrudDTO boardCrudDTODeleteParam) {

        Map<String, String> map = new HashMap<>();
        takeLocFileDrive(map, boardCrudDTODeleteParam);
        File psyFolder = dataDeleteAllById.getFolder(boardCrudDTODeleteParam);

        File[] files = psyFolder.listFiles();


        if (files != null) {
            // 디렉토리 엔트리가 있으면 삭제
            for (File entry : files) {

                entry.delete();
            }

        }

        boardCrudDAO.deleteStatement("com.board.board.mappers.boardCrud.deleteMasterByIdAndUserId", boardCrudDTODeleteParam);
        boardCrudDAO.deleteStatement("com.board.board.mappers.boardCrud.deleteDetailByIdAndUserId", boardCrudDTODeleteParam);
        List<BoardCrudDTO> selectByIdAfterDelete = boardCrudDAO.readStatementList("com.board.board.mappers.boardCrud.selectJoinDataByIdAndMultiFileId", boardCrudDTODeleteParam);
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


        Map<String, String> map = new HashMap<>();
        takeLocFileDrive(map, boardCrudDTOText);
        File psyFolder = dataUpdateWithFile.getFolder(boardCrudDTOText);

        if (deleteFilesCondition.isPresent() && deleteFilesCondition.get().length != 0) {

            for (int i = 0; i < deleteFilesCondition.get().length; i++) {
                boardCrudDTOText.setFileMeta(deleteFilesCondition.get()[i]);
                BoardCrudDTO boardDTOWithIdAndMultiFileId = boardCrudDAO.readStatement("com.board.board.mappers.boardCrud.selectIdAndMultiFileIdByIdAndFileMeta", boardCrudDTOText);
                boardCrudDTOText.setMultiFileId(boardDTOWithIdAndMultiFileId.getMultiFileId());
                BoardCrudDTO boardCrudDTOWithFileName = boardCrudDAO.readStatement("com.board.board.mappers.boardCrud.selectFileNameByUsingFileMetaById", boardCrudDTOText);
                String fileName = boardCrudDTOWithFileName.getFileName();
                dataUpdateWithFile.deleteCurrentPysFile(psyFolder, fileName);
                boardCrudDAO.deleteStatement("com.board.board.mappers.boardCrud.detailDeleteByMultiFileID", boardCrudDTOText);

            }

        }

        int updateCtn = boardCrudDAO.updateStatement("com.board.board.mappers.boardCrud.updateBoardMasterByIdAndWriterName", boardCrudDTOText);

        if (updatedTimesCouldNull.isPresent() && updatedFilesCouldNull.isPresent() && updatedTimesCouldNull.get().length != 0) {

            int multiFileId = 0;
            if (boardCrudDAO.readStatementObject("com.board.board.mappers.boardCrud.selectMaxMultiFileId", boardCrudDTOText) != null) {
                multiFileId = (int) boardCrudDAO.readStatementObject("com.board.board.mappers.boardCrud.selectMaxMultiFileId", boardCrudDTOText) + 1;

            }

            for (int i = 0; i < updatedTimesCouldNull.get().length; i++) {
                BoardCrudDTO insertFile = dataMakeWithFile.fileDataInit(updatedFilesCouldNull.get()[i], updatedTimesCouldNull.get()[i], boardCrudDTOText);
                insertFile.setMultiFileId(multiFileId);
                boardCrudDAO.createStatement("com.board.board.mappers.boardCrud.insertDetailTbl", insertFile);
                dataMakeWithFile.makePsyFile(updatedTimesCouldNull.get()[i], updatedFilesCouldNull.get()[i], psyFolder);
                multiFileId++;
            }

        }

        if (updateCtn != 1) {
            boardResDTO.setCode(500);
            boardResDTO.setResDescription("fail");
            return new ResponseEntity<BoardResDTO>(boardResDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        boardResDTO.setCode(200);
        boardResDTO.setResDescription("OK");
        return new ResponseEntity<BoardResDTO>(boardResDTO, HttpStatus.OK);


    }

    public void downloadFile(HttpServletResponse response, String id, String userId, String fileMeta) {


        BoardCrudDTO boardCrudDTO = new BoardCrudDTO();
        boardCrudDTO.setId(id);
        boardCrudDTO.setUserId(userId);
        boardCrudDTO.setFileMeta(fileMeta);

        Map<String, String> map = new HashMap<>();
        takeLocFileDrive(map, boardCrudDTO);


        BoardCrudDTO boardCrudDTOWithIdAndMultiFileId = boardCrudDAO.readStatement("com.board.board.mappers.boardCrud.selectIdAndMultiFileIdByIdAndFileMeta", boardCrudDTO);
        boardCrudDTO.setMultiFileId(boardCrudDTOWithIdAndMultiFileId.getMultiFileId());

        BoardCrudDTO boardCrudDTOWithFileName = boardCrudDAO.readStatement("com.board.board.mappers.boardCrud.selectFileNameByUsingFileMetaById", boardCrudDTO);
        boardCrudDTO.setFileName(boardCrudDTOWithFileName.getFileName());

        String downStr = (boardCrudDTO.getLocDrive() + File.separatorChar + boardCrudDTO.getLocParentFolder() + File.separatorChar + boardCrudDTO.getLocChildFolder() + File.separatorChar + boardCrudDTO.getFileName());

        File file = new File(downStr);
        String fileNameOrg = new String(boardCrudDTO.getFileName().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileNameOrg + "\";");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Length", "" + file.length());
        response.setHeader("Pragma", "no-cache;");
        response.setHeader("Expires", "-1;");

        try (FileInputStream fis = new FileInputStream(downStr);
             OutputStream out = response.getOutputStream()) {
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

    @Transactional(rollbackFor = Exception.class)
    public void makeReply(BoardReplyDTO boardReplyDTOParam) {

        BoardReplyDTO boardReplyDTOText = new BoardReplyDTO();

        dataMakeReply.init(boardReplyDTOParam, boardReplyDTOText);

        if ("init".equals(boardReplyDTOParam.getWhichBtn())) {
            int parentReplyId = 0;

            if (boardReplyDAO.readStatementObject("com.board.board.mappers.boardReply.selectMaxParentReplyId", boardReplyDTOText) != null) {
                parentReplyId = (int) boardReplyDAO.readStatementObject("com.board.board.mappers.boardReply.selectMaxParentReplyId", boardReplyDTOText) + 1;
            }
            boardReplyDTOText.setParentReplyId(parentReplyId);

        } else if ("reReply".equals(boardReplyDTOParam.getWhichBtn())) {
            boardReplyDTOText.setParentReplyId(boardReplyDTOParam.getParentReplyId());
            int childReplyId = 0;
            if (boardReplyDAO.readStatementObject("com.board.board.mappers.boardReply.selectMaxReplyChildId", boardReplyDTOText) != null) {
                childReplyId = (int) boardReplyDAO.readStatementObject("com.board.board.mappers.boardReply.selectMaxReplyChildId", boardReplyDTOText) + 1;
            }

            boardReplyDTOText.setChildReplyId(childReplyId);
        }
        boardReplyDAO.createStatement("com.board.board.mappers.boardReply.insertReply", boardReplyDTOText);
    }

    public List<BoardReplyDTO> showReplyMother(BoardReplyDTO boardReplyDTO) {

        return boardReplyDAO.readStatementList("com.board.board.mappers.boardReply.selectListReplyMother", boardReplyDTO);
    }

    public List<BoardReplyDTO> showReplyChild(BoardReplyDTO boardReplyDTO) {

        return boardReplyDAO.readStatementList("com.board.board.mappers.boardReply.selectListReplyChild", boardReplyDTO);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteReply(BoardReplyDTO boardReplyDTO, int id, int parentReplyId, int childReplyId) {

        dataDeleteReply.init(boardReplyDTO, id, parentReplyId, childReplyId);

        boardReplyDAO.deleteStatement("com.board.board.mappers.boardReply.deleteReply", boardReplyDTO);
    }

    public void deleteReplyAll(int id) {

        boardReplyDAO.deleteStatement("com.board.board.mappers.boardReply.deleteAllReplyCuzPageRemove", id);
    }

    @Override
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<BoardResDTO> updateReply(BoardReplyDTO boardReplyDTOParam) {

        BoardReplyDTO boardReplyDTO = new BoardReplyDTO();
        dataUpdateReply.init(boardReplyDTO, boardReplyDTOParam);

        boardReplyDAO.updateStatement("com.board.board.mappers.boardReply.updateReply", boardReplyDTO);
        BoardResDTO boardResDTO = new BoardResDTO();
        boardResDTO.setCode(200);
        return new ResponseEntity<>(boardResDTO, HttpStatus.OK);
    }
}
