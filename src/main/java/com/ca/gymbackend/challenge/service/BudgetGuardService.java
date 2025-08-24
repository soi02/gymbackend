package com.ca.gymbackend.challenge.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class BudgetGuardService {

    @Value("${rec.budget.daily-cap:2000}")   // 하루 임베딩 허용 호출 수 (예: 2000회)
    private long dailyCap;

    private final AtomicLong callsToday = new AtomicLong(0);
    private volatile LocalDate day = LocalDate.now();

    /** 임베딩 호출 전에 체크: true면 예산 초과 */
    public synchronized boolean overBudget() {
        // 날짜가 바뀌면 카운터 초기화
        LocalDate now = LocalDate.now();
        if (!now.equals(day)) {
            day = now;
            callsToday.set(0);
        }
        long used = callsToday.get();
        boolean over = used >= dailyCap;
        if (over) {
            log.warn("[BUDGET] Daily cap exceeded (used={}, cap={})", used, dailyCap);
        }
        return over;
    }

    /** 임베딩 호출 직전에 1 증가 (실제 호출할 때만) */
    public void inc() {
        long v = callsToday.incrementAndGet();
        if (v % 100 == 0) {
            log.info("[BUDGET] embed calls today = {}", v);
        }
    }

    /** 현재 사용량 조회 (옵션) */
    public long usedToday() {
        return callsToday.get();
    }
}
