package com.board.board.serviceImpl;

import com.board.board.dao.BoardCrudDAO;
import com.board.board.dao.BoardReplyDAO;
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
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardCrudServiceImpl implements BoardCrudService {

    private final BoardCrudDAO boardCrudDAO;

    private final BoardReplyDAO boardReplyDAO;

    @Transactional(readOnly = true)
    public String calcRowNum(String id) {
        String rn = boardCrudDAO.readPreStatement("com.board.board.mappers.boardCrud.selectRowNumById", id);
        BoardPageDTO boardPageDTO = new BoardPageDTO();
        return String.valueOf(boardPageDTO.calcPageNum(Integer.parseInt(rn)));
    }

    @Transactional(readOnly = true)
    public List<BoardCrudDTO> showAllBoardDataWithPaging(String pageNumStr, String amountStr, Map<String, BoardPageDTO> pageMap) {
        int total = boardCrudDAO.readPreStatementCount("com.board.board.mappers.boardCrud.selectCountAll", new BoardCrudDTO());

        int pageNumInt = 0;
        if ("".equals(pageNumStr)) {
            pageNumInt = 1;
        } else {
            pageNumInt = Integer.parseInt(pageNumStr);
        }
        int amount = Integer.parseInt(amountStr);

        BoardPageDTO boardPageDTO = new BoardPageDTO(pageNumInt, amount, total);

        int numMultiAmount = boardPageDTO.getPageNum() * boardPageDTO.getAmount();
        int numMinusOneMultiAmount = (boardPageDTO.getPageNum() - 1) * boardPageDTO.getAmount();

        boardPageDTO.setNumMutiAmount(numMultiAmount);
        boardPageDTO.setNumMinusOneMutiAmount(numMinusOneMultiAmount);

        List<BoardCrudDTO> list = boardCrudDAO.readPreStatementList("com.board.board.mappers.boardCrud.selectBoardListDataWithPaging", boardPageDTO);
        pageMap.put("pageMaker", boardPageDTO);

        if (!list.isEmpty()) {

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

    @Transactional(readOnly = true)
    public List<BoardCrudDTO> showBoardDetail(String id, String userId, Map<String, String> textMap) throws RuntimeException {
        BoardCrudDTO boardCrudDTO = new BoardCrudDTO();
        boardCrudDTO.setId((id));
        boardCrudDTO.setUserId(userId);
        List<BoardCrudDTO> list = boardCrudDAO.readPreStatementList("com.board.board.mappers.boardCrud.selectBoardDetailById", boardCrudDTO);

        for (BoardCrudDTO infoAllDto : list) {
            textMap.put("id", String.valueOf(infoAllDto.getId()));
            textMap.put("userId", infoAllDto.getUserId());
            textMap.put("title", infoAllDto.getTitle());
            textMap.put("writerName", infoAllDto.getWriterName());
            textMap.put("content", infoAllDto.getContent());
            textMap.put("updatedWhen", infoAllDto.getUpdatedWhen());
        }

        List<BoardCrudDTO> multiFileNameList = new ArrayList<>();

        for (BoardCrudDTO infoAllDto : list) {

            if (infoAllDto.getFileMeta() != null) {
                BoardCrudDTO boardCrudDTOWithFileData = boardCrudDAO.readPreStatement("com.board.board.mappers.boardCrud.selectFileNameByUsingFileMetaById", infoAllDto);
                multiFileNameList.add(boardCrudDTOWithFileData);
            }
        }

        return multiFileNameList;

    }

    @Transactional(rollbackFor = Exception.class)
    public void insertBoardDataWithFile(MultipartFile[] files, String[] uploadTime, BoardCrudDTO boardCrudDTOReq, HttpSession httpSession) throws UnsupportedEncodingException {
        Optional<MultipartFile[]> mapFilesCouldNull = Optional.ofNullable(files);
        Optional<String[]> uploadTimeCouldNull = Optional.ofNullable(uploadTime);

        BoardCrudDTO boardCrudDTO = new BoardCrudDTO();

        String writerName = new String(boardCrudDTOReq.getWriterName().getBytes("8859_1"), "utf-8");
        String updatedWhen = new String(boardCrudDTOReq.getUpdatedWhen().getBytes("8859_1"), "utf-8");
        String title = new String(boardCrudDTOReq.getTitle().getBytes("8859_1"), "utf-8");
        String content = new String(boardCrudDTOReq.getContent().getBytes("8859_1"), "utf-8");

        boardCrudDTO.setContent(content);
        boardCrudDTO.setUpdatedWhen(updatedWhen);
        boardCrudDTO.setWriterName(writerName);
        boardCrudDTO.setTitle(title);
        boardCrudDTO.setUserId((String) httpSession.getAttribute("userIdSess"));


        boardCrudDAO.createPreStatement("com.board.board.mappers.boardCrud.insertMasterTbl", boardCrudDTO);
        BoardCrudDTO selectMaxIdWithDTO = boardCrudDAO.readPreStatement("com.board.board.mappers.boardCrud.selectMaxIdByUserIDAndWriterName", boardCrudDTO);
        boardCrudDTO.setId(selectMaxIdWithDTO.getId());


        Map<String, String> locmap = new HashMap<>();
        takeLocFileDrive(locmap, boardCrudDTO);
        File psyFolder = new File(boardCrudDTO.getLocDrive() + File.separatorChar + boardCrudDTO.getLocParentFolder() + File.separatorChar + boardCrudDTO.getLocChildFolder());
        psyFolder.mkdirs();

        if (uploadTimeCouldNull.isPresent() && uploadTimeCouldNull.get().length != 0 && mapFilesCouldNull.isPresent()) {

            for (int i = 0; i < uploadTimeCouldNull.get().length; i++) {

                MultipartFile createdFiles=mapFilesCouldNull.get()[i];
                String uploadTimData = uploadTimeCouldNull.get()[i];
                String fileNameWithExtension = new String(createdFiles.getOriginalFilename().getBytes("8859_1"), "utf-8");
                String fileMeta = new String(uploadTimData.getBytes("8859_1"), "utf-8");
                String fileName = fileNameWithExtension.substring(0, fileNameWithExtension.indexOf("."));
                String extension = fileNameWithExtension.substring(fileNameWithExtension.indexOf("."), fileNameWithExtension.length());
                boardCrudDTO.setFileMeta(fileMeta);
                boardCrudDTO.setFileName(fileName);
                boardCrudDTO.setFileExtension(extension);
                boardCrudDTO.setMultiFileId(i);
                boardCrudDAO.createPreStatement("com.board.board.mappers.boardCrud.insertDetailTbl", boardCrudDTO);


                try (FileOutputStream fos = new FileOutputStream(psyFolder.getPath() + File.separatorChar + new String(createdFiles.getOriginalFilename().getBytes("8859_1"), "utf-8"));
                     InputStream is = createdFiles.getInputStream()) {
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

    }


    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<BoardResDTO> deleteAllDataByID(BoardCrudDTO boardCrudDTODeleteParam) {

        Map<String, String> map = new HashMap<>();
        takeLocFileDrive(map, boardCrudDTODeleteParam);
        File psyFolder = new File(boardCrudDTODeleteParam.getLocDrive() + File.separatorChar + boardCrudDTODeleteParam.getLocParentFolder() + File.separatorChar + boardCrudDTODeleteParam.getLocChildFolder());

        File[] files = psyFolder.listFiles();

        if (files != null) {
            // 디렉토리 엔트리가 있으면 삭제
            for (File entry : files) {

                entry.delete();
            }

        }

        boardCrudDAO.deletePreStatement("com.board.board.mappers.boardCrud.deleteMasterByIdAndUserId", boardCrudDTODeleteParam);
        boardCrudDAO.deletePreStatement("com.board.board.mappers.boardCrud.deleteDetailByIdAndUserId", boardCrudDTODeleteParam);
        List<BoardCrudDTO> selectByIdAfterDelete = boardCrudDAO.readPreStatementList("com.board.board.mappers.boardCrud.selectJoinDataByIdAndMultiFileId", boardCrudDTODeleteParam);

        if (selectByIdAfterDelete.isEmpty()) {
            BoardResDTO sucessBoardResDTO = new BoardResDTO();
            sucessBoardResDTO.setCode(200);
            sucessBoardResDTO.setResDescription("success");
            return new ResponseEntity<BoardResDTO>(sucessBoardResDTO, HttpStatus.OK);
        } else {
            BoardResDTO failBoardResDTO = new BoardResDTO();
            failBoardResDTO.setCode(500);
            failBoardResDTO.setResDescription("update fail");
            return new ResponseEntity<BoardResDTO>(failBoardResDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<BoardResDTO> updateBoardWithFile(MultipartFile[] updateFilesWithNull, String[] updateTimesWithNull, Map<String, String> updatedFileMapsWithNull, String[] deleteFilesConditionWithNull, BoardCrudDTO boardCrudDTOReq, HttpSession httpSession) throws Exception {

        BoardResDTO boardResDTO = new BoardResDTO();
        BoardCrudDTO boardCrudDTO = new BoardCrudDTO();
        Optional<MultipartFile[]> updatedFilesCouldNull = Optional.ofNullable(updateFilesWithNull);
        Optional<String[]> updatedTimesCouldNull = Optional.ofNullable(updateTimesWithNull);
        Optional<String[]> deleteFilesCondition = Optional.ofNullable(deleteFilesConditionWithNull);

        //String id = new String(boardCrudDTOReq.getId().getBytes("8859_1"), "utf-8");
        //String writerName = new String(boardCrudDTOReq.getWriterName().getBytes("8859_1"), "utf-8");
        String updatedWhen = new String(boardCrudDTOReq.getUpdatedWhen().getBytes("8859_1"), "utf-8");
        String title = new String(boardCrudDTOReq.getTitle().getBytes("8859_1"), "utf-8");
        String content = new String(boardCrudDTOReq.getContent().getBytes("8859_1"), "utf-8");
        String userId = (String) httpSession.getAttribute("userIdSess");

        boardCrudDTO.setId(boardCrudDTOReq.getId());
        boardCrudDTO.setContent(content);
        boardCrudDTO.setUpdatedWhen(updatedWhen);
        //boardCrudDTO.setWriterName(writerName);
        boardCrudDTO.setTitle(title);
        boardCrudDTO.setUserId(userId);

        Map<String, String> map = new HashMap<>();
        takeLocFileDrive(map, boardCrudDTO);
        File psyFolder = new File(boardCrudDTO.getLocDrive() + File.separatorChar + boardCrudDTO.getLocParentFolder() + File.separatorChar + boardCrudDTO.getLocChildFolder());

        if (deleteFilesCondition.isPresent() && deleteFilesCondition.get().length != 0) {

            for (int i = 0; i < deleteFilesCondition.get().length; i++) {
                boardCrudDTO.setFileMeta(deleteFilesCondition.get()[i]);
                BoardCrudDTO boardDTOWithIdAndMultiFileId = boardCrudDAO.readPreStatement("com.board.board.mappers.boardCrud.selectIdAndMultiFileIdByIdAndFileMeta", boardCrudDTO);
                boardCrudDTO.setMultiFileId(boardDTOWithIdAndMultiFileId.getMultiFileId());
                BoardCrudDTO boardCrudDTOWithFileName = boardCrudDAO.readPreStatement("com.board.board.mappers.boardCrud.selectFileNameByUsingFileMetaById", boardCrudDTO);
                String fileName = boardCrudDTOWithFileName.getFileName();

                File delEachFile = new File(psyFolder.getPath() + File.separatorChar + fileName);

                delEachFile.delete();

                boardCrudDAO.deletePreStatement("com.board.board.mappers.boardCrud.detailDeleteByMultiFileID", boardCrudDTO);

            }

        }

        int updateCtn = boardCrudDAO.updatePreStatement("com.board.board.mappers.boardCrud.updateBoardMasterById", boardCrudDTO);

        if (updatedTimesCouldNull.isPresent() && updatedFilesCouldNull.isPresent() && updatedTimesCouldNull.get().length != 0) {

            int multiFileId = 0;
            if (boardCrudDAO.readPreStatementObject("com.board.board.mappers.boardCrud.selectMaxMultiFileId", boardCrudDTO) != null) {
                multiFileId = (int) boardCrudDAO.readPreStatementObject("com.board.board.mappers.boardCrud.selectMaxMultiFileId", boardCrudDTO) + 1;

            }

            for (int i = 0; i < updatedTimesCouldNull.get().length; i++) {

                MultipartFile createdFiles = updatedFilesCouldNull.get()[i];
                String uploadTimeData = updatedTimesCouldNull.get()[i];
                String fileNameWithExtension = new String(createdFiles.getOriginalFilename().getBytes("8859_1"), "utf-8");
                String fileMeta = new String(uploadTimeData.getBytes("8859_1"), "utf-8");
                String fileName = fileNameWithExtension.substring(0, fileNameWithExtension.indexOf("."));
                String extension = fileNameWithExtension.substring(fileNameWithExtension.indexOf("."), fileNameWithExtension.length());
                boardCrudDTO.setFileMeta(fileMeta);
                boardCrudDTO.setFileName(fileName);
                boardCrudDTO.setFileExtension(extension);
                boardCrudDTO.setMultiFileId(multiFileId);

                boardCrudDAO.createPreStatement("com.board.board.mappers.boardCrud.insertDetailTbl", boardCrudDTO);

                try (FileOutputStream fos = new FileOutputStream(psyFolder.getPath() + File.separatorChar + new String(createdFiles.getOriginalFilename().getBytes("8859_1"), "utf-8"));
                     InputStream is = createdFiles.getInputStream()) {
                    int readCount = 0;
                    byte[] buffer = new byte[1024];
                    while ((readCount = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, readCount);
                    }
                } catch (Exception ex) {
                    throw new RuntimeException("file Save Error");
                }
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
        boardCrudDTO.setId((id));
        boardCrudDTO.setUserId(userId);
        boardCrudDTO.setFileMeta(fileMeta);

        Map<String, String> map = new HashMap<>();
        takeLocFileDrive(map, boardCrudDTO);


        BoardCrudDTO boardCrudDTOWithIdAndMultiFileId = boardCrudDAO.readPreStatement("com.board.board.mappers.boardCrud.selectIdAndMultiFileIdByIdAndFileMeta", boardCrudDTO);
        boardCrudDTO.setMultiFileId(boardCrudDTOWithIdAndMultiFileId.getMultiFileId());

        BoardCrudDTO boardCrudDTOWithFileName = boardCrudDAO.readPreStatement("com.board.board.mappers.boardCrud.selectFileNameByUsingFileMetaById", boardCrudDTO);
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

        BoardReplyDTO boardReplyDTO = new BoardReplyDTO();

        boardReplyDTO.setId(boardReplyDTOParam.getId());
        boardReplyDTO.setWriterName(boardReplyDTOParam.getWriterName());
        boardReplyDTO.setCreatedBy(boardReplyDTOParam.getUserId());
        boardReplyDTO.setUpdatedBy(boardReplyDTOParam.getUserId());
        boardReplyDTO.setContent(boardReplyDTOParam.getContent());
        boardReplyDTO.setCreatedBy(boardReplyDTOParam.getUserId());
        boardReplyDTO.setUpdatedBy(boardReplyDTOParam.getUserId());

        if ("init".equals(boardReplyDTOParam.getWhichBtn())) {
            int parentReplyId = 0;

            if (boardReplyDAO.readPreStatementObject("com.board.board.mappers.boardReply.selectMaxParentReplyId", boardReplyDTO) != null) {
                parentReplyId = (int) boardReplyDAO.readPreStatementObject("com.board.board.mappers.boardReply.selectMaxParentReplyId", boardReplyDTO) + 1;
            }
            boardReplyDTO.setParentReplyId(parentReplyId);

        } else if ("reReply".equals(boardReplyDTOParam.getWhichBtn())) {
            boardReplyDTO.setParentReplyId(boardReplyDTOParam.getParentReplyId());
            int childReplyId = 0;
            if (boardReplyDAO.readPreStatementObject("com.board.board.mappers.boardReply.selectMaxReplyChildId", boardReplyDTO) != null) {
                childReplyId = (int) boardReplyDAO.readPreStatementObject("com.board.board.mappers.boardReply.selectMaxReplyChildId", boardReplyDTO) + 1;
            }

            boardReplyDTO.setChildReplyId(childReplyId);
        }
        boardReplyDAO.createPreStatement("com.board.board.mappers.boardReply.insertReply", boardReplyDTO);
    }

    public List<BoardReplyDTO> showReplyMother(BoardReplyDTO boardReplyDTO) {

        return boardReplyDAO.readPreStatementList("com.board.board.mappers.boardReply.selectListReplyMother", boardReplyDTO);
    }

    public List<BoardReplyDTO> showReplyChild(BoardReplyDTO boardReplyDTO) {

        return boardReplyDAO.readPreStatementList("com.board.board.mappers.boardReply.selectListReplyChild", boardReplyDTO);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteReply(BoardReplyDTO boardReplyDTO, int id, int parentReplyId, int childReplyId) {

        boardReplyDTO.setId(id);
        boardReplyDTO.setParentReplyId(parentReplyId);
        boardReplyDTO.setChildReplyId(childReplyId);

        boardReplyDAO.deletePreStatement("com.board.board.mappers.boardReply.deleteReply", boardReplyDTO);
    }

    public void deleteReplyAll(int id) {

        boardReplyDAO.deletePreStatement("com.board.board.mappers.boardReply.deleteAllReplyCuzPageRemove", id);
    }

    @Override
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<BoardResDTO> updateReply(BoardReplyDTO boardReplyDTOParam) {

        BoardReplyDTO boardReplyDTO = new BoardReplyDTO();

        boardReplyDTO.setId(boardReplyDTOParam.getId());
        boardReplyDTO.setParentReplyId(boardReplyDTOParam.getParentReplyId());
        boardReplyDTO.setChildReplyId(boardReplyDTOParam.getChildReplyId());
        boardReplyDTO.setContent(boardReplyDTOParam.getContent());
        boardReplyDTO.setWriterName(boardReplyDTOParam.getWriterName());

        boardReplyDAO.updatePreStatement("com.board.board.mappers.boardReply.updateReply", boardReplyDTO);
        BoardResDTO boardResDTO = new BoardResDTO();
        boardResDTO.setCode(200);
        return new ResponseEntity<>(boardResDTO, HttpStatus.OK);
    }
}
