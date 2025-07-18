package com.example.service;

import com.example.entity.ApiResponse;
import com.example.service.BackendBashTask.BaseBackendBashTask;
import com.example.service.BackendBashTask.DownloadJMComic;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@ApplicationScoped
public class BackendBashTaskService {

    @Inject
    EntityManager entityManager;

    @ConfigProperty(name = "setting.dataPath", defaultValue = "./Archive")
    String dataSave;

    @ConfigProperty(name = "console.encoded")
    String defConsoleEncoded;

    //@Getter
    List<BaseBackendBashTask> backTask = new CopyOnWriteArrayList<>();
    public List<BaseBackendBashTask> getBackTask() {
        for(BaseBackendBashTask task: backTask){
            task.updateRunStatus();
        }
        return backTask;
    }

    @ConfigProperty(name = "setting.jmcomicInstall", defaultValue = "pip install jmcomic -i https://pypi.org/project -U")
    private String update_jmcomic;

    public void createDownloadTask(String id){
        DownloadJMComic downloadJMComic = new DownloadJMComic(id);
        log.info("编码: {}", defConsoleEncoded);
        downloadJMComic.setCharseName(defConsoleEncoded);
        downloadJMComic.setDataSave(dataSave);
//        downloadJMComic.setEntityManager(entityManager);
        backTask.add(downloadJMComic);
        downloadJMComic.startRun();
    }

    public ApiResponse updateJmcomic(){
//        BaseBackendBashTask backendBashTask = new BaseBackendBashTask(
//                "pip install jmcomic -i https://pypi.org/project -U"
//        );
        BaseBackendBashTask backendBashTask = new BaseBackendBashTask(update_jmcomic);
        backTask.add(backendBashTask);

        backendBashTask.setLogMode(BaseBackendBashTask.LOGMODE_FULL);
        backendBashTask.startRun();

        return ApiResponse.returnOK().setDataNow(backendBashTask);
    }

    public ApiResponse getTask(long id){
        for (BaseBackendBashTask task : backTask) {
            if(task.getId() == id){
                task.updateRunStatus();
                return ApiResponse.returnOK().setDataNow(task);
            }
        }
        return ApiResponse.returnFail("找不到对象");
    }

    public ApiResponse delTask(long id){
        for (BaseBackendBashTask task : backTask) {
            if(task.getId() == id){
                backTask.remove(task);
                return ApiResponse.returnOK();
            }
        }
        return ApiResponse.returnFail("找不到对象");
    }
}
