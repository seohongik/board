package com.board.board.dataMaker;

import com.board.board.dto.BoardCrudDTO;
import com.board.board.dto.BoardResDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataUpdateWithFile {
    public void init(BoardCrudDTO boardCrudDTOReq, BoardCrudDTO boardCrudDTOText , HttpSession httpSession) throws UnsupportedEncodingException {

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
    }

    public void deleteCurrentPysFile(File psyFolder,String filName){

        File delEachFile = new File(psyFolder.getPath() + File.separatorChar + filName);
        delEachFile.delete();

    }

   /* 업데이트 서비스 단에서 DataMakeWithFile 과 같은 매서드 사용


   public void fileDataInit(List<BoardCrudDTO> insertFileList, Optional<Map<String,String>> mapFilesWithUploadTime,BoardCrudDTO boardCrudDTOText,long  multiFileId){

        List<String> uploadTime  =mapFilesWithUploadTime.get().keySet().stream().collect(Collectors.toList());
        List<String> createdFiles  =mapFilesWithUploadTime.get().values().stream().collect(Collectors.toList());

        for (int i = 0; i < createdFiles.size(); i++) {
            String fileNameWithExtension = new String(createdFiles.get(i));
            String fileMeta = uploadTime.get(i);
            String fileName = fileNameWithExtension.substring(0, fileNameWithExtension.indexOf("."));
            String extension = fileNameWithExtension.substring(fileNameWithExtension.indexOf("."),fileNameWithExtension.length());
            boardCrudDTOText.setFileMeta(fileMeta);
            boardCrudDTOText.setFileName(fileName);
            boardCrudDTOText.setFileExtension(extension);
            boardCrudDTOText.setMultiFileId(multiFileId);

            insertFileList.add(boardCrudDTOText);

            multiFileId++;
        }
    }

    public void makePsyFile(Optional<Map<String,String>> mapFilesWithUploadTime, MultipartFile[] files , File psyFolder){
        try {
            List<String> uploadTime = mapFilesWithUploadTime.get().keySet().stream().collect(Collectors.toList());
            //List<String> createdFiles = mapFilesWithUploadTime.get().values().stream().collect(Collectors.toList());
            for (int i = 0; i < uploadTime.size(); i++) {
                try (FileOutputStream fos = new FileOutputStream(psyFolder.getPath() + File.separatorChar + uploadTime);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/


    public File getFolder(BoardCrudDTO boardCrudDTOText){
        File psyFolder = new File(boardCrudDTOText.getLocDrive() + File.separatorChar + boardCrudDTOText.getLocParentFolder() + File.separatorChar + boardCrudDTOText.getLocChildFolder());
        return psyFolder;
    }
}
