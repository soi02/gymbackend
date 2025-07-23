package com.ca.gymbackend.portal.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ca.gymbackend.portal.dto.UserDto;

@Mapper
public interface PortalMapper {
    public void insertUser(UserDto userDto);

    public UserDto findByLogin(@Param("accountName") String accountName,@Param("password") String password);

    public UserDto findById(@Param("id") Integer id);
}
