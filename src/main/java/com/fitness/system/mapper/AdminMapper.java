package com.fitness.system.mapper;

import com.fitness.system.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminMapper {
    /**
     * 根据管理员ID查询管理员
     * @param adminId 管理员ID
     * @return 管理员信息
     */
    Admin selectAdminByAdminId(Long adminId);

    /**
     * 根据用户名查询管理员
     * @param username 用户名
     * @return 管理员信息
     */
    Admin selectAdminByUsername(String username);

    /**
     * 查询所有管理员
     * @return 管理员列表
     */
    List<Admin> selectAdminList();

    /**
     * 新增管理员
     * @param admin 管理员信息
     * @return 结果
     */
    int insertAdmin(Admin admin);

    /**
     * 修改管理员
     * @param admin 管理员信息
     * @return 结果
     */
    int updateAdmin(Admin admin);

    /**
     * 删除管理员
     * @param adminId 管理员ID
     * @return 结果
     */
    int deleteAdminByAdminId(Long adminId);

    /**
     * 批量删除管理员
     * @param adminIds 需要删除的管理员ID列表
     * @return 结果
     */
    int deleteAdminByAdminIds(Long[] adminIds);

    /**
     * 检查用户名是否唯一
     * @param username 用户名
     * @return 结果
     */
    Admin checkUsernameUnique(String username);
}