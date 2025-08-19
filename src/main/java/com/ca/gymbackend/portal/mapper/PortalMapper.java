package com.ca.gymbackend.portal.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ca.gymbackend.portal.dto.UserDto;

@Mapper
public interface PortalMapper {

    // 회원가입
    public void insertUser(UserDto userDto);

    // 로그인
    public UserDto findByLogin(@Param("accountName") String accountName,@Param("password") String password);

    public UserDto findById(@Param("id") Integer id);

    // 사용자 정보 수정 메서드 추가
    public void updateUser(UserDto userDto);

}
