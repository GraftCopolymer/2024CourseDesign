package org.fatmansoft.teach.service;

import com.openhtmltopdf.extend.FSSupplier;
import com.openhtmltopdf.extend.impl.FSDefaultCacheStore;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.extern.slf4j.Slf4j;
import org.fatmansoft.teach.models.DictionaryInfo;
import org.fatmansoft.teach.models.MenuInfo;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.MyTreeNode;
import org.fatmansoft.teach.repository.DictionaryInfoRepository;
import org.fatmansoft.teach.repository.MenuInfoRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * BaseService 系统菜单、数据字典等处理的功能和方法
 */
@Service
@Slf4j
public class BaseService {
    @Autowired
    private DictionaryInfoRepository dictionaryInfoRepository;  //数据数据操作自动注入
    @Autowired
    private MenuInfoRepository menuInfoRepository;  //菜单数据操作自动注入
    @Autowired
    private ResourceLoader resourceLoader;  //资源装在服务对象自动注入
    @Autowired
    private StudentRepository studentRepository;

    private FSDefaultCacheStore fSDefaultCacheStore = new FSDefaultCacheStore();

    /**
     *  getDictionaryTreeNode 获取数据字典节点树根节点
     * @return MyTreeNode 数据字典树根节点
     */
    public List<MyTreeNode> getDictionaryTreeNodeList() {
        List<MyTreeNode> childList = new ArrayList<MyTreeNode>();
        List<DictionaryInfo> sList = dictionaryInfoRepository.findRootList();
        if(sList == null)
            return childList;
        for(int i = 0; i<sList.size();i++) {
            childList.add(getDictionaryTreeNode(null,sList.get(i),null));
        }
        return childList;
    }

    /**
     * 获得数据字典的MyTreeNode
     * @param pid  父节点
     * @param d   数据字典数据
     * @return  树节点
     */
    public MyTreeNode getDictionaryTreeNode( Integer pid, DictionaryInfo d,String parentTitle) {
        MyTreeNode  node = new MyTreeNode(d.getId(),d.getValue(),d.getLabel(),null);
        node.setLabel(d.getValue()+"-"+d.getLabel());
        node.setParentTitle(parentTitle);
        node.setPid(pid);
        List<MyTreeNode> childList = new ArrayList<MyTreeNode>();
        node.setChildren(childList);
        List<DictionaryInfo> sList = dictionaryInfoRepository.findByPid(d.getId());
        if(sList == null)
            return node;
        for(int i = 0; i<sList.size();i++) {
            childList.add(getDictionaryTreeNode(node.getId(),sList.get(i),node.getValue()));
        }
        return node;
    }

    /**
     * MyTreeNode getMenuTreeNode(Integer userTypeId) 获得角色的菜单树根节点
     * @param userTypeId 用户类型ID
     * @return MyTreeNode 根节点对象
     */
    public List<MyTreeNode> getMenuTreeNodeList() {
        List<MyTreeNode> childList = new ArrayList<MyTreeNode>();
        List<MenuInfo> sList = menuInfoRepository.findByUserTypeIds("");
        if(sList == null)
            return childList;
        for(int i = 0; i<sList.size();i++) {
            childList.add(getMenuTreeNode(null,sList.get(i),""));
        }
        return childList;
    }
    /**
     * MyTreeNode getMenuTreeNode(Integer userTypeId) 获得角色的某个菜单的菜单树根节点
     * @param parentTitle 用户类型ID Integer pid 父节点ID MenuInfo d 菜单信息
     *
     * @return MyTreeNode 当前菜单的MyTreeNode对象
     */
    public MyTreeNode getMenuTreeNode(Integer pid, MenuInfo d,String parentTitle) {
        MyTreeNode  node = new MyTreeNode(d.getId(),d.getName(),d.getTitle(),null);
        node.setLabel(d.getId()+"-"+d.getTitle());
        node.setUserTypeIds(d.getUserTypeIds());
        node.setParentTitle(parentTitle);
        node.setPid(pid);
        //System.out.println("当前菜单的名称: " + node.getLabel());
        List<MyTreeNode> childList = new ArrayList<MyTreeNode>();
        node.setChildren(childList);
        List<MenuInfo> sList = menuInfoRepository.findByUserTypeIds("",d.getId());
        if(sList == null)
            return node;
        for(int i = 0; i<sList.size();i++) {
            childList.add(getMenuTreeNode(node.getId(),sList.get(i),node.getTitle()));
        }
        return node;
    }

    /**
     * 将HTML的字符串转换成PDF文件，返回前端的PDF文件二进制数据流数据流
     * @param htmlContent  HTML 字符流
     * @return PDF数据流兑现
     */
    public ResponseEntity<StreamingResponseBody> getPdfDataFromHtml(String htmlContent) {
        try {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(htmlContent, null);
            builder.useFastMode();
            builder.useCacheStore(PdfRendererBuilder.CacheStore.PDF_FONT_METRICS, fSDefaultCacheStore);
            Resource resource = resourceLoader.getResource("classpath:font/SourceHanSansSC-Regular.ttf"); 
            InputStream fontInput = resource.getInputStream();
            builder.useFont(new FSSupplier<InputStream>() {
                @Override
                public InputStream supply() {
                    return fontInput;
                }
            }, "SourceHanSansSC");
            StreamingResponseBody stream = outputStream -> {
                builder.toStream(outputStream);
                builder.run();
            };

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(stream);

        }
        catch (Exception e) {
            return  ResponseEntity.internalServerError().build();
        }
    }

    /*
     * 根据当前登录的用户来查询学生
     * */
    public Student getStudent(){
        Integer userId = CommonMethod.getUserId();
        if(userId == null){
            return null;
        }
        Optional<Student> studentOptional = studentRepository.findByUserId(userId);
        if(!studentOptional.isPresent()){
            return null;
        }
        return studentOptional.get();
    }

    public DataResponse uploadMultiFiles(MultipartFile[] files, String remotePath){
        //将所有文件储存在指定目录
        List<String> fileNames = Arrays.stream(files)
            .map(file->{
                try {
                    // 生成具有递增数字的唯一文件名
                    String uniqueFileName = CommonMethod.generateUniqueFileName(file.getOriginalFilename(), remotePath);
                    // 保存文件
                    Path path = Paths.get(remotePath + uniqueFileName);
                    Files.createDirectories(path.getParent());
                    Files.write(path, file.getBytes());
                    return uniqueFileName;
                } catch (IOException e) {
                    e.printStackTrace();
                    return "Failed to upload " + file.getOriginalFilename();
                }
        }).toList();
        if(fileNames.isEmpty()){
            return CommonMethod.getReturnMessageError("上传文件失败");
        }
        return CommonMethod.getReturnData(fileNames);
    }

}
