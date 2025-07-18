package com.example.service.BackendBashTask;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
@Getter
@RegisterForReflection
public class DelFileFormRM extends BaseBackendBashTask{
    private final String delFilepath;

    public DelFileFormRM(String path){
        //super("/bin/bash -c rm -rf '" + path + "'");
        super(new String[]{
//                "/bin/bash"
//                ,"-c"
                "rm"
                ,"-rf"
                ,"'" + path + "'"
        });
//        if(isFile){
//            command = new String[]{
//                    "rm",
//                    "-rf"
//            };
//        }
        delFilepath = path;
        setLogMode(BaseBackendBashTask.LOGMODE_FULL);
    }

    @Override
    public void startHook() {
        //super.startHook();
        log.info("删除文件进程开始，[{}]", String.join("|", command));
    }

    @Override
    public void logHook(String logLine) {
        //super.logHook(logLine);
        System.out.println(">delFile>["+ delFilepath + "]" + logLine);
    }

    @Override
    public void exitHook(int exitCode, String errMessage) {
        //super.exitHook(exitCode, errMessage);
        log.info("删除文件进程退出，[{}] - {} - {}\r\n{}", delFilepath, exitCode, errMessage, getLog_full().toString());
    }
}
