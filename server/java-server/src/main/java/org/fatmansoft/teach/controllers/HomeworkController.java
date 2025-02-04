package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.AnswerFileRepository;
import org.fatmansoft.teach.repository.AnswerRepository;
import org.fatmansoft.teach.service.HomeworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.validation.Valid;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/homework")
public class HomeworkController {
    @Autowired
    private HomeworkService homeworkService;

    //教师端获取作业列表
    @PostMapping("/getHomeworkList")
    public DataResponse getHomeworkList(@Valid @RequestBody DataRequest req){
        return homeworkService.getHomeworkList(req);
    }

    //学生端获取作业列表
    @PostMapping("/getStudentHomeworkList")
    public DataResponse getStudentHomeworkList(@Valid @RequestBody DataRequest req){
        return homeworkService.getStudentHomeworkList(req);
    }

    //教师端保存作业信息
    @PostMapping("/saveHomework")
    public DataResponse saveHomework(@RequestPart(value = "file", required = false) MultipartFile[] files, @RequestPart("dataRequest") DataRequest req){
        return homeworkService.saveHomework(files, req);
    }

    //教师端删除作业
    @PostMapping("/deleteHomework")
    public DataResponse deleteHomework(@Valid @RequestBody DataRequest req){
        return homeworkService.deleteHomework(req);
    }

    //接收参数：files: 文件名和文件内容映射
    @PostMapping("/uploadHomeworkFiles")
    public DataResponse uploadHomeworkFiles(@RequestParam("file") MultipartFile[] files,
                                            @RequestParam("homeworkId") Integer homeworkId){
        return homeworkService.uploadHomeworkFiles(files, homeworkId);
    }

    //教师端获取作业文件列表
    @PostMapping("/getFileList")
    public DataResponse getFileList(@Valid @RequestBody DataRequest req){
        return homeworkService.getFileList(req);
    }

    //教师端删除文件
    @PostMapping("/deleteFile")
    public DataResponse deleteFile(@Valid @RequestBody DataRequest req){
        return homeworkService.deleteFile(req);
    }

    //下载文件
    @PostMapping("/downloadFile")
    public ResponseEntity<StreamingResponseBody> downloadFile(@Valid @RequestBody DataRequest req){
        return homeworkService.downloadFile(req);
    }

    //学生端获取答案列表
    @PostMapping("/getStudentAnswerFileList")
    public DataResponse getStudentAnswerFileList(@Valid @RequestBody DataRequest req){
        return homeworkService.getStudentAnswerFileList(req);
    }

    //学生端删除答案文件
    @PostMapping("/deleteAnswerFile")
    public DataResponse deleteAnswerFile(@Valid @RequestBody DataRequest req){
        return homeworkService.deleteAnswerFile(req);
    }

    @PostMapping("/saveAnswer")
    public DataResponse saveAnswer(@RequestPart(value = "file", required = false) MultipartFile[] files, @RequestPart("dataRequest") DataRequest req){
        return homeworkService.saveAnswer(files, req);
    }

}
