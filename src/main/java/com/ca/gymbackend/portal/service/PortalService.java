package com.ca.gymbackend.portal.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ca.gymbackend.portal.dto.UserDto;
import com.ca.gymbackend.portal.mapper.PortalMapper;

import net.coobird.thumbnailator.Thumbnails;



@Service
public class PortalService {
    @Autowired
    @Qualifier("fileRootPath")
    private String fileRootPath;

    @Autowired
    private PortalMapper portalMapper;

    //user 등록
    public void register(UserDto userDto) {
        portalMapper.insertUser(userDto);
    }

    // 로그인 시 사용자의 계정명과 비밀번호로 사용자 정보 조회
    public UserDto findByAccountName(String accountName,String password){
        return portalMapper.findByLogin(accountName, password);
    }

    // 사용자 ID로 사용자 정보 조회
    public UserDto findById(Integer id){
        return portalMapper.findById(id);
    }

    // 이미지
    public UserDto saveImage(byte[] buffer,String originalFilename){
        String fileDirPathString = fileRootPath;    //d여기까진 경로 똑같이 해야함
        String uuid = UUID.randomUUID().toString(); // 랜덤한 문자열을 굉장히 큰 문자를 만들어줌
        long currentTime = System.currentTimeMillis(); // 현재 시간을

        String filename = uuid + "_" + currentTime;
        // 확장자 붙이기
        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        filename += ext;

        // 날짜별 폴더 생성
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd/");
        String todayPath = simpleDateFormat.format(new Date(currentTime));

        File fileTodayPath = new File(fileDirPathString + todayPath); // 이 파일 api는 폴더도 포함

        if (!fileTodayPath.exists()) {
            fileTodayPath.mkdirs();
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer);
        try {
            Thumbnails.of(inputStream)
                    .scale(1.0)
                    .toFile(fileDirPathString + todayPath + filename);
            ;
        } catch (Exception e) {
            e.printStackTrace();
        }

        UserDto userDto = new UserDto();
        userDto.setProfileImage(todayPath + filename);

        return userDto;
    }

    // 사용자 정보 수정
    public void updateUser(UserDto userDto) {
        portalMapper.updateUser(userDto);
    }
}
