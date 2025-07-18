package com.example.entity;

//import javax.persistence.Entity;
//import javax.persistence.Id;
import com.example.util.FileUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Entity
@RegisterForReflection
public class FileItem extends PanacheEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    public Long id;
    public String name;
    @Column(name="path", unique = true)
    public String path;
    public boolean isFile;
    //public Time time;
    public LocalDateTime time;
    public String image;
    public String mark;
    public Boolean isSeen;
    public Byte rate;
    public String aid;
    public Integer subdirectories;

    private boolean checkPath(File file){
        if(file==null){
            log.error("文件不存在: [{}]", path);
            return false;
        }
        if (!file.exists()){
            log.error("文件不存在: [{}]", path);
            return false;
        }
        return true;
    }
    public void updateTime(){
        updateTime(new File(path));
    }
//    private static final ZoneOffset defZoneOffset = ZoneOffset.of("+8");
    private static final ZoneId defZone = ZoneId.of("Asia/Shanghai");
    public void updateTime(File file){
        if (!checkPath(file)){return;}
//        if(time==null){
//            //time = new Time(file.lastModified());
//        }else{
//            //time.setTime(file.lastModified());
//        }
//        time = LocalDateTime.ofEpochSecond(file.lastModified(), 0, defZoneOffset);
        time = Instant.ofEpochMilli(file.lastModified()).atZone(defZone).toLocalDateTime();
    }
    public void updateImage(){updateImage(new File(path));}
    public void updateImage(File file){
        if (!checkPath(file)){return;}
        if(file.isFile()){
            if(FileUtil.isFileImage(file)){
                image = path;
            }
        }else{
            image = FileUtil.findDirImage(file);
        }
    }
    public void updateIsFile(){
        updateIsFile(new File(path));
    }
    public void updateIsFile(File file){
        isFile = file.isFile();
    }
    public void updateSubdirectories(File file){
        String[] tmp = file.list();
        if(tmp!=null && !isFile){
            subdirectories = tmp.length;
        }
    }
    public void updateSubdirectories(){
        updateSubdirectories(new File(path));
    }

    /**
     * 更新数据
     * @param fileItem
     */
    public void updateData(FileItem fileItem){
        name = fileItem.name;
        path = fileItem.path;
        isFile = fileItem.isFile;
        time = fileItem.time;
        mark = fileItem.mark;
        isSeen = fileItem.isSeen;
        rate = fileItem.rate;
        aid = fileItem.aid;
        subdirectories = fileItem.subdirectories;
    }

    @Transactional
    public static FileItem create(File file){
        FileItem item = new FileItem();
        item.name = file.getName();
        item.path = file.getAbsolutePath();
        item.isFile = file.isFile();
        item.mark = "";
        item.isSeen = false;
        item.rate = 0;
        item.subdirectories = 0;

        item.updateTime(file);
        item.updateImage(file);
        item.updateIsFile(file);
        item.updateSubdirectories(file);

        // 保存对象
        item.persistAndFlush();
        return item;
    }

    @Transactional
    public static FileItem getFormPath(File file){
        String path_ = file.getAbsolutePath();
        FileItem result = FileItem.find("path", path_).firstResult();
        if(result==null){
            return create(file);
        }
        result.updateTime(file);
        result.updateImage(file);
        result.updateIsFile(file);
        result.updateSubdirectories(file);
        return result;
    }
    @Transactional
    @ActivateRequestContext
    public void save(){
        this.persistAndFlush();
    }
    public static FileItem getFormPath(String path){
        return getFormPath(new File(path));
    }
    public static String AutomaticProcessingPath(String path){
        Path path_ = Paths.get(path);
        path_ = path_.normalize();
        try {
            path_ = path_.toRealPath(LinkOption.NOFOLLOW_LINKS);
        } catch (IOException e) {
            //throw new RuntimeException(e);
            log.warn("路径转换出现异常, {} <> {}", path, path_.toString());
        }
        return path_.toString();
    }

    public void addMark(String tag){
        Set<String> markSet = initMarkSet();
        markSet.add(tag);
        try {
            saveMarkSet(markSet);
        } catch (JsonProcessingException e) {
            //throw new RuntimeException(e);
            log.error("保存Mark失败！{}", e.getMessage());
        }
    }
    public void addMark(Set<String> tags){
        Set<String> markSet = initMarkSet();
        markSet.addAll(tags);
        try {
            saveMarkSet(markSet);
        } catch (JsonProcessingException e) {
            //throw new RuntimeException(e);
            log.error("保存Mark失败！{}", e.getMessage());
        }
    }
    public void addMark(String[] tags){
        Set<String> markSet = initMarkSet();
        markSet.addAll(List.of(tags));
        try {
            saveMarkSet(markSet);
        } catch (JsonProcessingException e) {
            //throw new RuntimeException(e);
            log.error("保存Mark失败！{}", e.getMessage());
        }
    }
    private Set<String> initMarkSet(){
        ObjectMapper mapper = new ObjectMapper();
        Set<String> markSet = new LinkedHashSet<>();
        try {
            markSet = mapper.readValue(mark, markSet.getClass());
        } catch (JsonProcessingException e) {
            String tmp = mark;
            if (tmp!=null && !tmp.isEmpty()){
                if(tmp.startsWith("[")){
                    tmp = tmp.substring(1);
                }
                if(tmp.startsWith("]")){
                    tmp = tmp.substring(0, tmp.length()-2);
                }
                String[] arr = tmp.split(",");
                markSet.addAll(List.of(arr));
            }
            //throw new RuntimeException(e);
        }
        mapper.clearProblemHandlers();
        mapper = null;
        return markSet;
    }
    private void saveMarkSet(Set<String> data) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mark = mapper.writeValueAsString(data);
    }
}