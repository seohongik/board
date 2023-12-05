package com.board.board.dataMaker;

import com.board.board.dto.BoardCrudDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DataMakeWithFile {
    public void init(BoardCrudDTO boardCrudDTOText, BoardCrudDTO boardCrudDTOReq,
                     HttpSession httpSession) throws UnsupportedEncodingException {

        String writerName = new String(boardCrudDTOReq.getWriterName().getBytes("8859_1"), "utf-8");
        String updatedWhen = new String(boardCrudDTOReq.getUpdatedWhen().getBytes("8859_1"), "utf-8");
        String title = new String(boardCrudDTOReq.getTitle().getBytes("8859_1"), "utf-8");
        String content = new String(boardCrudDTOReq.getContent().getBytes("8859_1"), "utf-8");

        boardCrudDTOText.setContent(content);
        boardCrudDTOText.setUpdatedWhen(updatedWhen);
        boardCrudDTOText.setWriterName(writerName);
        boardCrudDTOText.setTitle(title);
        boardCrudDTOText.setUserId((String) httpSession.getAttribute("userIdSess"));

    }

    public File makeFolder(BoardCrudDTO boardCrudDTOText) {
        File psyFolder = new File(boardCrudDTOText.getLocDrive() + File.separatorChar + boardCrudDTOText.getLocParentFolder() + File.separatorChar + boardCrudDTOText.getLocChildFolder());
        psyFolder.mkdirs();
        return psyFolder;
    }

    public BoardCrudDTO fileDataInit(MultipartFile createdFiles, String uploadTime, BoardCrudDTO boardCrudDTOText) throws UnsupportedEncodingException {

        String fileNameWithExtension = new String(createdFiles.getOriginalFilename().getBytes("8859_1"), "utf-8");
        String fileMeta = new String(uploadTime.getBytes("8859_1"), "utf-8");
        String fileName = fileNameWithExtension.substring(0, fileNameWithExtension.indexOf("."));
        String extension = fileNameWithExtension.substring(fileNameWithExtension.indexOf("."), fileNameWithExtension.length());
        boardCrudDTOText.setFileMeta(fileMeta);
        boardCrudDTOText.setFileName(fileName);
        boardCrudDTOText.setFileExtension(extension);
        return boardCrudDTOText;
    }

    public void makePsyFile(String uploadTime, MultipartFile file, File psyFolder) {

        try (FileOutputStream fos = new FileOutputStream(psyFolder.getPath() + File.separatorChar + new String(file.getOriginalFilename().getBytes("8859_1"), "utf-8"));
             InputStream is = file.getInputStream();) {
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

