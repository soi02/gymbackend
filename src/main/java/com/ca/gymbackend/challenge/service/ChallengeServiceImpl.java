package com.ca.gymbackend.challenge.service;

import com.ca.gymbackend.challenge.dto.ChallengeCreateRequest;
import com.ca.gymbackend.challenge.mapper.ChallengeMapper;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChallengeServiceImpl {

    private final ChallengeMapper challengeMapper;

    @Autowired
    @Qualifier("fileRootPath") // application.yml 또는 @Bean에서 설정한 경로 주입
    private String rootPath;

    // 챌린지 생성
    // 1. 이미지 저장
    public String saveChallengeThumbnailImage(byte[] buffer, String originalFilename) {
        try {
            String uuid = UUID.randomUUID().toString();
            long currentTime = System.currentTimeMillis();

            String filename = uuid + "_" + currentTime;
            String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            filename += ext;

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd/");
            String todayPath = simpleDateFormat.format(new Date(currentTime));
            Path dirPath = Paths.get(rootPath, "challengeImages", todayPath);
            Files.createDirectories(dirPath);

            ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer);
            Path filePath = dirPath.resolve(filename);
            System.out.println("✅ 저장 경로: " + filePath.toString());


            Thumbnails.of(inputStream)
                      .scale(1.0)
                      .toFile(filePath.toFile());

            return "/challengeImages/" + todayPath + filename;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    // 2. 챌린지 정보 DB에 저장 insert
    public void saveChallengeData(ChallengeCreateRequest challengeCreateRequest) {
        challengeMapper.createChallenge(challengeCreateRequest);
    }

    // 3. 방금 생성된 챌린지 ID 가져오기 조회
    public int getGeneratedChallengeId() {
        return challengeMapper.findLastInsertedChallengeId();
    }

    // 4. 챌린지-키워드 연결 insert
    public void saveChallengekeywordMapping(int challengeId, List<Integer> challengeKeywordIds) {
        if (challengeKeywordIds != null) {
            for (Integer challengeKeywordId : challengeKeywordIds) {
                challengeMapper.createChallengeKeyword(challengeId, challengeKeywordId);
            }
        }
    }
    

    // 챌린지 전체 목록 조회
    public List<ChallengeCreateRequest> getAllChallengeList() {
        return challengeMapper.findAllChallengeList();
    }
}
