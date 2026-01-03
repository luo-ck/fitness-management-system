package com.fitness.system.service.impl;

import com.fitness.system.entity.Admin;
import com.fitness.system.mapper.AdminMapper;
import com.fitness.system.service.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 管理员Service实现类
 */
@Service
public class AdminServiceImpl implements IAdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public Admin selectAdminByAdminId(Long adminId) {
        return adminMapper.selectAdminByAdminId(adminId);
    }

    @Override
    public Admin selectAdminByUsername(String username) {
        return adminMapper.selectAdminByUsername(username);
    }

    @Override
    public List<Admin> selectAdminList() {
        return adminMapper.selectAdminList();
    }

    @Override
    public int insertAdmin(Admin admin) {
        return adminMapper.insertAdmin(admin);
    }

    @Override
    public int updateAdmin(Admin admin) {
        return adminMapper.updateAdmin(admin);
    }

    @Override
    public int deleteAdminByAdminId(Long adminId) {
        return adminMapper.deleteAdminByAdminId(adminId);
    }

    @Override
    public int deleteAdminByAdminIds(Long[] adminIds) {
        return adminMapper.deleteAdminByAdminIds(adminIds);
    }

    @Override
    public boolean checkUsernameUnique(String username) {
        Admin admin = adminMapper.selectAdminByUsername(username);
        return admin == null;
    }
}