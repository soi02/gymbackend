package com.ca.gymbackend.challenge.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ca.gymbackend.challenge.mapper.ChallengeMapper;

@Service
@Profile("dev")
public class TestChallengeServiceImpl {

    @Autowired
    private ChallengeMapper challengeMapper;

    @Transactional
    public void updateTestAttendanceCount(int userId, int challengeId, int testCount) {
        // 1. 기존 기록 삭제 (출석 기록 및 노리개 기록)
        challengeMapper.deleteAttendanceRecords(userId, challengeId);
        challengeMapper.deleteUserChallengeNorigae(userId, challengeId); // <-- 노리개 기록 삭제 추가

        // 2. 테스트용으로 원하는 횟수만큼 출석 기록 삽입
        for (int i = 0; i < testCount; i++) {
            LocalDate testDate = LocalDate.now().minusDays(i);
            String photoUrl = "test_url_" + i;
            challengeMapper.insertAttendanceRecord(userId, challengeId, testDate, photoUrl);
        }

        // 3. 노리개 지급 로직 실행 <-- 이 부분이 핵심!
        updateNorigaeForTest(userId, challengeId, testCount);

        System.out.println("INFO: 유저 " + userId + "의 챌린지 " + challengeId + " 출석 횟수가 " + testCount + "회로 설정되었습니다.");
    }
    
    // 테스트용 노리개 지급 로직
    private void updateNorigaeForTest(int userId, int challengeId, int testCount) {
        int totalPeriod = challengeMapper.findChallengeTotalDays(challengeId);
        int achievementRate = (int) Math.round((double) testCount / totalPeriod * 100);

        // 달성률에 맞는 노리개 등급 ID 조회
        Integer tierId = challengeMapper.findTierIdByAchievementRate(achievementRate);

        if (tierId != null) {
            // 해당 등급의 노리개 지급 (이미 지급했는지 확인 후 INSERT 또는 UPDATE)
            Integer existingTierId = challengeMapper.findUserNorigaeTierId(userId, challengeId);
            if (existingTierId == null) {
                challengeMapper.insertUserNorigae(userId, challengeId, tierId);
            } else if (existingTierId < tierId) {
                challengeMapper.updateUserNorigae(userId, challengeId, tierId);
            }
        }
    }
}
