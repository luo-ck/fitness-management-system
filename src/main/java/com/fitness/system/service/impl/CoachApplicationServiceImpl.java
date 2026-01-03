package com.fitness.system.service.impl;

import com.fitness.system.entity.Coach;
import com.fitness.system.entity.CoachApplication;
import com.fitness.system.entity.Message;
import com.fitness.system.entity.User;
import com.fitness.system.mapper.CoachApplicationMapper;
import com.fitness.system.mapper.CoachMapper;
import com.fitness.system.mapper.UserMapper;
import com.fitness.system.service.ICoachApplicationService;
import com.fitness.system.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class CoachApplicationServiceImpl implements ICoachApplicationService {

    @Autowired
    private CoachApplicationMapper coachApplicationMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CoachMapper coachMapper;

    @Autowired
    private IMessageService messageService;

    @Override
    @Transactional
    public boolean submitApplication(CoachApplication application) {
        // 设置申请时间和状态
        application.setApplyTime(new Date());
        application.setStatus("pending");
        
        // 插入申请记录
        int result = coachApplicationMapper.insertApplication(application);
        return result > 0;
    }

    @Override
    public CoachApplication getApplicationByUserId(Long userId) {
        return coachApplicationMapper.selectByUserId(userId);
    }

    @Override
    public List<CoachApplication> getAllApplications() {
        return coachApplicationMapper.selectAllApplications();
    }

    @Override
    public CoachApplication getApplicationById(Long id) {
        return coachApplicationMapper.selectById(id);
    }

    @Override
    @Transactional
    public boolean approveApplication(CoachApplication application) {
        // 先查询完整的申请信息，确保userId存在
        CoachApplication fullApplication = coachApplicationMapper.selectById(application.getId());
        if (fullApplication == null) {
            return false;
        }
        
        // 更新申请状态
        fullApplication.setStatus("approved");
        fullApplication.setApproveTime(new Date());
        fullApplication.setAdminId(application.getAdminId());
        fullApplication.setAdminName(application.getAdminName());
        int updateResult = coachApplicationMapper.updateStatusToApproved(fullApplication);
        
        if (updateResult > 0) {
            // 查询用户信息
            User user = userMapper.selectUserByUserId(fullApplication.getUserId());
            if (user != null) {
                // 创建教练记录
                Coach coach = new Coach();
                coach.setUsername(user.getUsername());
                coach.setName(fullApplication.getName());
                coach.setSpecialty(fullApplication.getSpecialty());
                coach.setContact(fullApplication.getContact());
                coach.setIntro(fullApplication.getIntro());
                coach.setPasswordHash(user.getPasswordHash()); // 复制用户密码
                coach.setIsVerified(true);
                coach.setIsDeleted(false);
                coach.setCreatedAt(new Date());
                
                coachMapper.insertCoach(coach);
                
                // 发送消息通知用户申请通过
                Message message = new Message();
                message.setSenderId(fullApplication.getAdminId());
                message.setSenderName(fullApplication.getAdminName());
                message.setReceiverId(fullApplication.getUserId());
                message.setReceiverType("user");
                message.setTitle("教练申请通过通知");
                message.setContent("您的教练申请已通过审核，现在您可以以教练身份登录系统。");
                message.setIsRead(false);
                message.setSendTime(new Date());
                
                messageService.sendMessage(message);
            }
        }
        
        return updateResult > 0;
    }

    @Override
    @Transactional
    public boolean rejectApplication(CoachApplication application) {
        // 先查询完整的申请信息，确保userId存在
        CoachApplication fullApplication = coachApplicationMapper.selectById(application.getId());
        if (fullApplication == null) {
            return false;
        }
        
        // 更新申请状态
        fullApplication.setStatus("rejected");
        fullApplication.setApproveTime(new Date());
        fullApplication.setAdminId(application.getAdminId());
        fullApplication.setAdminName(application.getAdminName());
        fullApplication.setFeedback(application.getFeedback());
        int updateResult = coachApplicationMapper.updateStatusToRejected(fullApplication);
        
        if (updateResult > 0) {
            // 发送消息通知用户申请被拒绝
            Message message = new Message();
            message.setSenderId(fullApplication.getAdminId());
            message.setSenderName(fullApplication.getAdminName());
            message.setReceiverId(fullApplication.getUserId());
            message.setReceiverType("user");
            message.setTitle("教练申请拒绝通知");
            message.setContent("您的教练申请未通过审核，原因：" + fullApplication.getFeedback());
            message.setIsRead(false);
            message.setSendTime(new Date());
            
            messageService.sendMessage(message);
        }
        
        return updateResult > 0;
    }
}