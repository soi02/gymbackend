package com.ca.gymbackend.market.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ca.gymbackend.market.dto.MarketArticleDto;
import com.ca.gymbackend.market.dto.MarketCommentOnArticleDto;
import com.ca.gymbackend.market.dto.MarketDealedLogDto;
import com.ca.gymbackend.market.dto.MarketProductInterestedLogDto;
import com.ca.gymbackend.market.dto.MarketReviewOnUserDto;
import com.ca.gymbackend.market.dto.MarketUserInfoDto;
import com.ca.gymbackend.market.mapper.MarketMapper;

@Service
public class MarketService {
    
    @Autowired
    private MarketMapper marketMapper;

    public MarketUserInfoDto selectMarketUserInfo(Integer userId) {
        return marketMapper.selectMarketUserInfo(userId);
    }
    
    public void insertMarketArticle(MarketArticleDto marketArticleDto) {
        marketMapper.insertMarketArticle(marketArticleDto);
    }
    public List<Map<String, Object>> selectMarketArticle() {
        List<Map<String, Object>> mapListSelectMarketArticle = new ArrayList<>();
        List<MarketArticleDto> listMarketArticleDto = marketMapper.selectMarketArticle();
        for (MarketArticleDto marketArticleDto : listMarketArticleDto) {
            Map<String, Object> map = new HashMap<>();
            MarketUserInfoDto marketUserInfoDto = marketMapper.selectMarketUserInfo(marketArticleDto.getMarketUserId());
            map.put("marketArticleDto", marketArticleDto);
            map.put("marketUserInfoDto", marketUserInfoDto);
            mapListSelectMarketArticle.add(map);
        }
        return mapListSelectMarketArticle;
    }
    public MarketArticleDto selectSpecificMarketArticle(Integer id) {
        return marketMapper.selectSpecificMarketArticle(id);
    }
    public void updateMarketArticle(MarketArticleDto marketArticleDto) {
        marketMapper.updateMarketArticle(marketArticleDto);
    }
    public void deleteMarketArticle(Integer id) {
        marketMapper.deleteMarketArticle(id);
    }
    
    public void insertMarketCommentOnArticle(MarketCommentOnArticleDto marketCommentOnArticleDto) {
        marketMapper.insertMarketCommentOnArticle(marketCommentOnArticleDto);
    }
    public List<Map<String, Object>> selectMarketCommentOnArticle(Integer articleId) {
        List<Map<String, Object>> mapListSelectMarketCommentOnArticle = new ArrayList<>();
        List<MarketCommentOnArticleDto> listMarketCommentOnArticleDto = marketMapper.selectMarketCommentOnArticle(articleId);
        for (MarketCommentOnArticleDto marketCommentOnArticleDto : listMarketCommentOnArticleDto) {
            Map<String, Object> map = new HashMap<>();
            MarketUserInfoDto marketUserInfoDto = marketMapper.selectMarketUserInfo(marketCommentOnArticleDto.getMarketUserId());
            map.put("marketCommentOnArticleDto", marketCommentOnArticleDto);
            map.put("marketUserInfoDto", marketUserInfoDto);
            mapListSelectMarketCommentOnArticle.add(map);
        }
        return mapListSelectMarketCommentOnArticle;
    }
    public void updateMarketCommentOnArticle(MarketCommentOnArticleDto marketCommentOnArticleDto) {
        marketMapper.updateMarketCommentOnArticle(marketCommentOnArticleDto);
    }
    public void deleteMarketCommentOnArticle(Integer id) {
        marketMapper.deleteMarketCommentOnArticle(id);
    }
    
    public void insertMarketProductInterestedLog(MarketProductInterestedLogDto marketProductInterestedLogDto) {
        marketMapper.insertMarketProductInterestedLog(marketProductInterestedLogDto);
    }
    public List<Map<String, Object>> selectMarketProductInterestedLogWhenUserInfo(Integer marketUserId) {
        List<Map<String, Object>> mapListSelectMarketProductInterestedLogWhenUserInfo = new ArrayList<>();
        List<MarketProductInterestedLogDto> listMarketProductInterestedLogDto = marketMapper.selectMarketProductInterestedLogWhenUserInfo(marketUserId);
        for (MarketProductInterestedLogDto marketProductInterestedLogDto : listMarketProductInterestedLogDto) {
            Map<String, Object> map = new HashMap<>();
            MarketUserInfoDto marketUserInfoDto = marketMapper.selectMarketUserInfo(marketProductInterestedLogDto.getMarketUserId());
            map.put("marketProductInterestedLogDto", marketProductInterestedLogDto);
            map.put("marketUserInfoDto", marketUserInfoDto);
            mapListSelectMarketProductInterestedLogWhenUserInfo.add(map);
        }
        return mapListSelectMarketProductInterestedLogWhenUserInfo;
    }
    public List<Map<String, Object>> selectMarketProductInterestedLogWhenArticleInfo(Integer specificArticleId) {
        List<Map<String, Object>> mapListSelectMarketProductInterestedLogWhenArticleInfo = new ArrayList<>();
        List<MarketProductInterestedLogDto> listMarketProductInterestedLogDto = marketMapper.selectMarketProductInterestedLogWhenArticleInfo(specificArticleId);
        for (MarketProductInterestedLogDto marketProductInterestedLogDto : listMarketProductInterestedLogDto) {
            Map<String, Object> map = new HashMap<>();
            MarketUserInfoDto marketUserInfoDto = marketMapper.selectMarketUserInfo(marketProductInterestedLogDto.getMarketUserId());
            map.put("marketProductInterestedLogDto", marketProductInterestedLogDto);
            map.put("marketUserInfoDto", marketUserInfoDto);
            mapListSelectMarketProductInterestedLogWhenArticleInfo.add(map);
        }
        return mapListSelectMarketProductInterestedLogWhenArticleInfo;
    }
    public MarketProductInterestedLogDto selectMarketProductInterestedLogWhenUserAndArticleInfo(Integer marketUserId, Integer specificArticleId) {
        return marketMapper.selectMarketProductInterestedLogWhenUserAndArticleInfo(marketUserId, specificArticleId);
    }
    public void deleteMarketProductInterestedLog(Integer specificArticleId) {
        marketMapper.deleteMarketProductInterestedLog(specificArticleId);
    }
    
    public void insertMarketDealedLog(MarketDealedLogDto marketDealedLogDto) {
        marketMapper.insertMarketDealedLog(marketDealedLogDto);
    }
    public List<Map<String, Object>> selectMarketDealedLogWhenBuyer(Integer buyerId) {
        List<Map<String, Object>> mapListSelectMarketDealedLogWhenBuyer = new ArrayList<>();
        List<MarketDealedLogDto> listMarketDealedLogDto = marketMapper.selectMarketDealedLogWhenBuyer(buyerId);
        for (MarketDealedLogDto marketDealedLogDto : listMarketDealedLogDto) {
            Map<String, Object> map = new HashMap<>();
            MarketUserInfoDto marketUserInfoDto = marketMapper.selectMarketUserInfo(marketDealedLogDto.getBuyerId());
            map.put("marketDealedLogDto", marketDealedLogDto);
            map.put("marketUserInfoDto", marketUserInfoDto);
            mapListSelectMarketDealedLogWhenBuyer.add(map);
        }
        return mapListSelectMarketDealedLogWhenBuyer;
    }
    public List<Map<String, Object>> selectMarketDealedLogWhenSeller(Integer sellerId) {
        List<Map<String, Object>> mapListSelectMarketDealedLogWhenSeller = new ArrayList<>();
        List<MarketDealedLogDto> listMarketDealedLogDto = marketMapper.selectMarketDealedLogWhenSeller(sellerId);
        for (MarketDealedLogDto marketDealedLogDto : listMarketDealedLogDto) {
            Map<String, Object> map = new HashMap<>();
            MarketUserInfoDto marketUserInfoDto = marketMapper.selectMarketUserInfo(marketDealedLogDto.getSellerId());
            map.put("marketDealedLogDto", marketDealedLogDto);
            map.put("marketUserInfoDto", marketUserInfoDto);
            mapListSelectMarketDealedLogWhenSeller.add(map);
        }
        return mapListSelectMarketDealedLogWhenSeller;
    }
    
    public void insertMarketReviewToUser(MarketReviewOnUserDto marketReviewOnUserDto) {
        marketMapper.insertMarketReviewToUser(marketReviewOnUserDto);
    }
    public MarketReviewOnUserDto selectMarketReviewToUserdd(Integer evaluatedUserId) {
        marketMapper.selectMarketReviewToUser(evaluatedUserId);
        return null;
    } // 2차 백엔드 코드 작성 시 보완 및 수정
    
    public List<Map<String, Object>> selectMarketReviewToUser(Integer evaluatedUserId) {
        List<Map<String, Object>> mapListSelectMarketReviewToUser = new ArrayList<>();
        List<MarketReviewOnUserDto> listMarketReviewOnUserDto = marketMapper.selectMarketReviewToUser(evaluatedUserId);
        for (MarketReviewOnUserDto marketReviewOnUserDto : listMarketReviewOnUserDto) {
            Map<String, Object> map = new HashMap<>();
            MarketUserInfoDto marketUserInfoDto = marketMapper.selectMarketUserInfo(marketReviewOnUserDto.getEvaluatedUserId());
            map.put("marketReviewOnUserDto", marketReviewOnUserDto);
            map.put("marketUserInfoDto", marketUserInfoDto);
            mapListSelectMarketReviewToUser.add(map);
        }
        return mapListSelectMarketReviewToUser;
    }
    
    public void updateMarketReviewToUser(MarketReviewOnUserDto marketReviewOnUserDto) {
        marketMapper.updateMarketReviewToUser(marketReviewOnUserDto);
    }
    public void deleteMarketReviewToUser(Integer evaluatedUserId) {
        marketMapper.deleteMarketReviewToUser(evaluatedUserId);
    }

    // [ additional crud ]
    
    public void selectMarketArticleByTitleSearchWord(String searchWord) {
        marketMapper.selectMarketArticleByTitleSearchWord(searchWord);
    }
    public void selectMarketArticleByContentSearchWord(String searchWord) {
        marketMapper.selectMarketArticleByContentSearchWord(searchWord);
    }
    
}
