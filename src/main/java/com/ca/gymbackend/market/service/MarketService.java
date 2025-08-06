package com.ca.gymbackend.market.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ca.gymbackend.market.dto.MarketArticleDto;
import com.ca.gymbackend.market.dto.MarketCommentOnArticleDto;
import com.ca.gymbackend.market.dto.MarketDealedLogDto;
import com.ca.gymbackend.market.dto.MarketProductInterestedLogDto;
import com.ca.gymbackend.market.dto.MarketReviewOnUserDto;
import com.ca.gymbackend.market.mapper.MarketMapper;

@Service
public class MarketService {
    
    @Autowired
    private MarketMapper marketMapper;
    
    public void insertMarketArticle(MarketArticleDto marketArticleDto) {
        marketMapper.insertMarketArticle(marketArticleDto);
    }
    public MarketArticleDto selectMarketArticle(Integer id) {
        marketMapper.selectMarketArticle(id);
        return null;
    } // 2차 백엔드 코드 작성 시 보완 및 수정
    public void updateMarketArticle(MarketArticleDto marketArticleDto) {
        marketMapper.updateMarketArticle(marketArticleDto);
    }
    public void deleteMarketArticle(Integer id) {
        marketMapper.deleteMarketArticle(id);
    }
    
    public void insertMarketCommentOnArticle(MarketCommentOnArticleDto marketCommentOnArticleDto) {
        marketMapper.insertMarketCommentOnArticle(marketCommentOnArticleDto);
    }
    public MarketCommentOnArticleDto selectMarketCommentOnArticle(Integer articleId) {
        marketMapper.selectMarketCommentOnArticle(articleId);
        return null;
    } // 2차 백엔드 코드 작성 시 보완 및 수정
    public void updateMarketCommentOnArticle(MarketCommentOnArticleDto marketCommentOnArticleDto) {
        marketMapper.updateMarketCommentOnArticle(marketCommentOnArticleDto);
    }
    public void deleteMarketCommentOnArticle(Integer id) {
        marketMapper.deleteMarketCommentOnArticle(id);
    }
    
    public void insertMarketProductInterestedLog(MarketProductInterestedLogDto marketProductInterestedLogDto) {
        marketMapper.insertMarketProductInterestedLog(marketProductInterestedLogDto);
    }
    public MarketProductInterestedLogDto selectMarketProductInterestedLogWhenUserInfo(Integer marketUserId) {
        marketMapper.selectMarketProductInterestedLogWhenUserInfo(marketUserId);
        return null;
    } // 2차 백엔드 코드 작성 시 보완 및 수정
    public MarketProductInterestedLogDto selectMarketProductInterestedLogWhenArticleInfo(Integer specificArticleId) {
        marketMapper.selectMarketProductInterestedLogWhenArticleInfo(specificArticleId);
        return null;
    } // 2차 백엔드 코드 작성 시 보완 및 수정
    public void deleteMarketProductInterestedLog(Integer specificArticleId) {
        marketMapper.deleteMarketProductInterestedLog(specificArticleId);
    }
    
    public void insertMarketDealedLog(MarketDealedLogDto marketDealedLogDto) {
        marketMapper.insertMarketDealedLog(marketDealedLogDto);
    }
    public MarketDealedLogDto selectMarketDealedLogWhenBuyer(Integer buyerId) {
        marketMapper.selectMarketDealedLogWhenBuyer(buyerId);
        return null;
    } // 2차 백엔드 코드 작성 시 보완 및 수정
    public MarketDealedLogDto selectMarketDealedLogWhenSeller(Integer sellerId) {
        marketMapper.selectMarketDealedLogWhenSeller(sellerId);
        return null;
    } // 2차 백엔드 코드 작성 시 보완 및 수정
    
    public void insertMarketReviewToUser(MarketReviewOnUserDto marketReviewOnUserDto) {
        marketMapper.insertMarketReviewToUser(marketReviewOnUserDto);
    }
    public MarketReviewOnUserDto selectMarketReviewToUser(Integer evaluatedUserId) {
        marketMapper.selectMarketReviewToUser(evaluatedUserId);
        return null;
    } // 2차 백엔드 코드 작성 시 보완 및 수정
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
