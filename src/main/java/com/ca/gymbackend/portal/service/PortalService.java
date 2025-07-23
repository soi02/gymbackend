package com.ca.gymbackend.portal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ca.gymbackend.portal.dto.UserDto;
import com.ca.gymbackend.portal.mapper.PortalMapper;

@Service
public class PortalService {
    @Autowired
    private PortalMapper portalMapper;

    public UserDto findByAccountName(String accountName,String password){
        return portalMapper.findByLogin(accountName, password);
    }

}
