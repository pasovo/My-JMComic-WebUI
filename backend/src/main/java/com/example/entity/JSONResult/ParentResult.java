package com.example.entity.JSONResult;

import com.example.entity.FileItem;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;

@RegisterForReflection
public class ParentResult{
    public List<FileItem> parent;
    public Integer index;
    public String path;
}
