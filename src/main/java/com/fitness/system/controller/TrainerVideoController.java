package com.fitness.system.controller;

import com.fitness.system.entity.TrainerVideo;
import com.fitness.system.service.ITrainerVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;

/**
 * 教练教学视频Controller
 */
@RestController
@RequestMapping("/trainer/videos")
public class TrainerVideoController {

    @Autowired
    private ITrainerVideoService trainerVideoService;

    // 文件上传目录
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    // 初始化时确保uploadDir是绝对路径
    @PostConstruct
    public void init() {
        // 如果是相对路径，转换为应用根目录的绝对路径
        File dir = new File(uploadDir);
        if (!dir.isAbsolute()) {
            // 获取应用根目录
            String appRoot = System.getProperty("user.dir");
            uploadDir = appRoot + File.separator + uploadDir;
            System.out.println("转换相对路径为绝对路径: " + uploadDir);
        }
    }

    /**
     * 根据视频ID查询教练教学视频
     * @param videoId 视频ID
     * @return 教练教学视频信息
     */
    @GetMapping("/{videoId}")
    public ResponseEntity<TrainerVideo> getTrainerVideoById(@PathVariable Long videoId) {
        TrainerVideo video = trainerVideoService.selectTrainerVideoById(videoId);
        if (video == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(video);
    }

    /**
     * 根据教练ID查询教练教学视频列表
     * @param coachId 教练ID
     * @return 教练教学视频列表
     */
    @GetMapping("/coach/{coachId}")
    public ResponseEntity<List<TrainerVideo>> getTrainerVideosByCoachId(@PathVariable Long coachId) {
        List<TrainerVideo> videos = trainerVideoService.selectTrainerVideosByCoachId(coachId);
        return ResponseEntity.ok(videos);
    }

    /**
     * 新增教练教学视频
     * @param trainerVideo 教练教学视频信息
     * @return 结果
     */
    @PostMapping
    public ResponseEntity<Integer> createTrainerVideo(@RequestBody TrainerVideo trainerVideo) {
        int result = trainerVideoService.insertTrainerVideo(trainerVideo);
        return ResponseEntity.status(201).body(result);
    }

    /**
     * 上传教练教学视频
     * @param title 视频标题
     * @param description 视频描述
     * @param coachId 教练ID
     * @param videoFile 视频文件
     * @return 结果
     */
    @PostMapping("/upload")
    public ResponseEntity<TrainerVideo> uploadVideo(
            @RequestParam(required = false, name = "title") String title,
            @RequestParam(required = false, name = "description") String description,
            @RequestParam(required = false, name = "coachId") String coachIdStr,
            @RequestParam(required = false, name = "videoFile") MultipartFile videoFile) {
        
        System.out.println("=== 收到视频上传请求 ===");
        System.out.println("title: " + title);
        System.out.println("description: " + description);
        System.out.println("coachIdStr: " + coachIdStr);
        System.out.println("videoFile: " + (videoFile != null ? videoFile.getOriginalFilename() : "null"));
        
        // 验证必填参数
        if (title == null || title.isEmpty()) {
            System.err.println("title不能为空");
            return ResponseEntity.badRequest().body(null);
        }
        if (coachIdStr == null || coachIdStr.isEmpty()) {
            System.err.println("coachId不能为空");
            return ResponseEntity.badRequest().body(null);
        }
        if (videoFile == null || videoFile.isEmpty()) {
            System.err.println("videoFile不能为空");
            return ResponseEntity.badRequest().body(null);
        }
        
        // 转换coachId类型
        Long coachId = null;
        try {
            coachId = Long.parseLong(coachIdStr);
        } catch (NumberFormatException e) {
            System.err.println("coachId格式错误: " + coachIdStr);
            return ResponseEntity.badRequest().body(null);
        }
        
        try {
            // 创建上传目录（如果不存在）
            File dir = new File(uploadDir);
            // 使用mkdirs()确保所有父目录都被创建
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                System.out.println("创建上传目录: " + dir.getAbsolutePath() + "，结果: " + created);
            }
            System.out.println("上传目录绝对路径: " + dir.getAbsolutePath());

            // 生成唯一文件名
            String originalFilename = videoFile.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            String uniqueFilename = UUID.randomUUID().toString() + "." + fileExtension;

            // 保存文件，使用绝对路径确保正确性
            File dest = new File(dir, uniqueFilename);
            System.out.println("保存文件路径: " + dest.getAbsolutePath());
            videoFile.transferTo(dest);

            // 创建视频记录
            TrainerVideo trainerVideo = new TrainerVideo();
            trainerVideo.setCoachId(coachId);
            trainerVideo.setTitle(title);
            trainerVideo.setDescription(description);
            // 生成访问URL，格式为：/uploads/filename
            // 静态资源映射由Spring Boot处理，不需要包含context-path
            trainerVideo.setVideoUrl("/uploads/" + uniqueFilename);
            trainerVideo.setUploadDate(new Date());

            // 保存到数据库
            trainerVideoService.insertTrainerVideo(trainerVideo);

            return ResponseEntity.status(201).body(trainerVideo);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * 修改教练教学视频
     * @param videoId 视频ID
     * @param trainerVideo 教练教学视频信息
     * @return 结果
     */
    @PutMapping("/{videoId}")
    public ResponseEntity<Integer> updateTrainerVideo(@PathVariable Long videoId, @RequestBody TrainerVideo trainerVideo) {
        trainerVideo.setVideoId(videoId);
        int result = trainerVideoService.updateTrainerVideo(trainerVideo);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除教练教学视频
     * @param videoId 视频ID
     * @return 结果
     */
    @DeleteMapping("/{videoId}")
    public ResponseEntity<Integer> deleteTrainerVideo(@PathVariable Long videoId) {
        int result = trainerVideoService.deleteTrainerVideoById(videoId);
        return ResponseEntity.ok(result);
    }
}
