package com.ca.gymbackend.market.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ca.gymbackend.market.dto.MarketArticleDto;
import com.ca.gymbackend.market.dto.MarketCommentOnArticleDto;
import com.ca.gymbackend.market.dto.MarketDealedLogDto;
import com.ca.gymbackend.market.dto.MarketProductInterestedLogDto;
import com.ca.gymbackend.market.dto.MarketReviewOnUserDto;
import com.ca.gymbackend.market.dto.MarketUserInfoDto;

@Mapper
public interface MarketMapper {
    
    // [ 테이블 당 기본 crud ]
    
    public MarketUserInfoDto selectMarketUserInfo(@Param("userId") Integer userId);
    
    public void insertMarketArticle(MarketArticleDto marketArticleDto);
    public MarketArticleDto selectMarketArticle(@Param("id") Integer id);
    public void updateMarketArticle(MarketArticleDto marketArticleDto);
    // 장터 게시글 내용 정보 바꾸기, 장터 게시글 판매 여부 바꾸기 분할 예정
    public void deleteMarketArticle(@Param("id") Integer id);
    
    public void insertMarketCommentOnArticle(MarketCommentOnArticleDto marketCommentOnArticleDto);
    public MarketCommentOnArticleDto selectMarketCommentOnArticle(@Param("articleId") Integer articleId);
    public void updateMarketCommentOnArticle(MarketCommentOnArticleDto marketCommentOnArticleDto);
    public void deleteMarketCommentOnArticle(@Param("id") Integer id);
    
    public void insertMarketProductInterestedLog(MarketProductInterestedLogDto marketProductInterestedLogDto);
    public MarketProductInterestedLogDto selectMarketProductInterestedLogWhenUserInfo(@Param("marketUserId") Integer marketUserId);
    public MarketProductInterestedLogDto selectMarketProductInterestedLogWhenArticleInfo(@Param("specificArticleId") Integer specificArticleId);
    public void deleteMarketProductInterestedLog(@Param("specificArticleId") Integer specificArticleId);
    
    public void insertMarketDealedLog(MarketDealedLogDto marketDealedLogDto);
    public MarketDealedLogDto selectMarketDealedLogWhenBuyer(@Param("buyerId") Integer buyerId);
    public MarketDealedLogDto selectMarketDealedLogWhenSeller(@Param("sellerId") Integer sellerId);
    
    public void insertMarketReviewToUser(MarketReviewOnUserDto marketReviewOnUserDto);
    public MarketReviewOnUserDto selectMarketReviewToUser(@Param("evaluatedUserId") Integer evaluatedUserId);
    public void updateMarketReviewToUser(MarketReviewOnUserDto marketReviewOnUserDto);
    public void deleteMarketReviewToUser(@Param("evaluatedUserId") Integer evaluatedUserId);
    
    // [ 페이지에 따른 추가 crud ]
    
    public void selectMarketArticleByTitleSearchWord(@Param("searchWord") String searchWord);
    public void selectMarketArticleByContentSearchWord(@Param("searchWord") String searchWord);
    
}
