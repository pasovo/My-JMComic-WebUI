package com.example.service;

import com.example.entity.FileItem;
import com.example.util.FileUtil;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@ApplicationScoped // 或者 @Singleton
public class LibraryService {

    public List<FileItem> listFile(String path){
        File[] files = new File(path).listFiles();
        if(files==null){
            return null;
        }
        List<FileItem> fileItems = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            FileItem fileItem = FileItem.getFormPath(file);
            fileItems.add(fileItem);
        }

        Collections.sort(fileItems, new Comparator<FileItem>() {
            @Override
            public int compare(FileItem o1, FileItem o2) {
//                System.out.println(o1.name + " -- " + o2.name + o1.name.compareTo(o2.name));
//                return o1.name.compareTo(o2.name);
                return FileUtil.compare(o1.name, o2.name);
            }
        });

        return fileItems;
    }
}
