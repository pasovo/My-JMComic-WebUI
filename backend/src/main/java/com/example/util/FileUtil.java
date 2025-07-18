package com.example.util;

import com.example.entity.FileItem;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
public class FileUtil {
    public static boolean checkPath(File file){
        if(file==null){
            log.error("文件不存在");
            return false;
        }
        if (!file.exists()){
            log.error("文件不存在: [{}]", file.getPath());
            return false;
        }
        return true;
    }

    //private static final String[] fileSuffix = "bmp,jpg,png,tif,gif,pcx,tga,exif,fpx,svg,psd,cdr,pcd,dxf,ufo,eps,ai,raw,wmf,webp,avif,apng".split(",");
    private static final Set<String> fileSuffix = new HashSet<>();
    static{
        String[] split = "bmp,jpg,png,tif,gif,pcx,tga,exif,fpx,svg,psd,cdr,pcd,dxf,ufo,eps,ai,raw,wmf,webp,avif,apng".split(",");
        fileSuffix.addAll(List.of(split));
    }
    public static String getFileNameSuffix(String name){
        int ii = name.lastIndexOf(".");
        if(ii>0){
            return name.substring(ii+1, name.length());
        }
        return "";
    }
    public static String getFileName(String name){
        int ii = name.indexOf(".");
        if(ii>0){
            return name.substring(0,ii-1);
        }
        return "";
    }
    public static boolean isFileImage(File file){
        if(!checkPath(file)){
            return false;
        }

        String name = file.getName();
        String suffix = getFileNameSuffix(name);

        return fileSuffix.contains(suffix);
    }

    public static String findDirImage(File file){
        File[] files = file.listFiles();
        List<String> arr_dir = new ArrayList<>();
        List<String> arr_file = new ArrayList<>();
        if(files==null){
            return null;
        }
        for (int i = 0; i < files.length; i++) {
            File file_ = files[i];
            //String name = getFileName(file_.getName());
            String name = file_.getName();
            if(file_.isFile()){
                if(isFileImage(file_)){
                    arr_file.add(name);
                }
            }else{
                arr_dir.add(name);
            }
        }

        if(!arr_file.isEmpty()){
            arr_file.sort(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return FileUtil.compare(o1, o2);
                }
            });
            //Collections.sort(arr_file);
            return file.getPath() + File.separator + arr_file.get(0);
        }

        if(!arr_dir.isEmpty()){
            arr_dir.sort(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return FileUtil.compare(o1, o2);
                }
            });
            //Collections.sort(arr_dir);
            String oneDir = file.getPath() + File.separator + arr_dir.get(0);
            return findDirImage(new File(oneDir));
        }
        return null;
    }

    public static int compare(String n1, String n2){
        /*char[] c1 = n1.toCharArray();
        char[] c2 = n2.toCharArray();

        *//*int min = Math.min(c1.length, c2.length);
        int i_c1=0,i_c2=0;
        for (int i = 0; i < min; i++) {
            char c_1 = c1[i];
            char c_2 = c2[i];

            if(c_1)
        }*//*
        List<Integer> c_1 = new ArrayList<>();

        return 0;*/
        //return compare_v0(n1, n2);
        return compare_v1(n1, n2);
    }
    /*private static int compare_v0(String n1, String n2){
        char[] c1 = n1.toCharArray();
        char[] c2 = n2.toCharArray();

        int max = Math.max(c1.length, c2.length);
        int i_c1=0,i_c2=0;
        int tmp_1=0,tmp_2=0;
        for (int i = 0; i < max; i++) {
            tmp_1=0;
            tmp_2=0;
            // 溢出检查
            if(c1.length <= i+i_c1){
                //c1已遍历完成
                if(c2.length <= i+i_c2){
                    //如果c2也遍历完成，那就代表之前没分出前后，现在也没有分出先后，即两个文本同样的权重
                    // exp: 第001话 = 第0001话 = 第1话
                    return 0;
                }else{
                    // 如果c2没有遍历完成，那就代表c2比c1长，需要排在c1后面
                    return -(int)c2[i+i_c2];
                }
            }
            if(c2.length <= i+i_c1){
                // c1 没有超出，c2超出了
                return (int)c1[i+i_c1];
            }
            char c_1 = c1[i+i_c1];
            char c_2 = c2[i+i_c2];

            while (c_1>=48 && c_1 <=57 && c1.length > i+i_c1){
                //tmp_1 += Character.getNumericValue(c_1);
                tmp_1 = addStr(tmp_1, c_1);
                i_c1++;
                c_1 = c1[i+i_c1];
            }
            while (c_2>=48 && c_2 <=57 && c2.length > i+i_c2){
                //tmp_2 += Character.getNumericValue(c_2);
                tmp_2 = addStr(tmp_2, c_2);
                i_c2++;
                c_2 = c2[i+i_c2];
            }
            if(tmp_1==0){tmp_1 = (int)c_1;}
            if(tmp_2==0){tmp_2 = (int)c_2;}
            if(tmp_1 == tmp_2){
                continue;
            }
            return tmp_1 - tmp_2;
        }

        return 0;
    }*/

    private static int compare_v1(String n1, String n2) {
        char[] c1 = n1.toCharArray();
        char[] c2 = n2.toCharArray();
        int[] r1 = charArrMerge(c1);
        int[] r2 = charArrMerge(c2);

        int min = Math.min(r1.length, r2.length);
        int i = 0;
        for (; i < min; i++) {
            if(r1[i] == r2[i]){
                continue;
            }
            return r1[i] - r2[i];
        }
        //前面都没有判断出来，长的排后面
        if(r1.length > r2.length){
            return r1[i];
        } else if (r1.length == r2.length) {
            // 一样长的话，那就是两个文本权重相等
            return 0;
        } else{
            return r2[i];
        }
    }

    private static int[] charArrMerge(char[] cArr){
        int[] r1 = new int[cArr.length];
        int[] r_tmp = new int[cArr.length];
        int i_in = 0;
        int i_tmp_val = 0;
        for (int i = 0; i < cArr.length; i++) {
            char c_t = cArr[i];
            if(c_t>=48 && c_t <=57){
                i_tmp_val = addStr(i_tmp_val, c_t);
                continue;
            }else{
                // 缓存出栈
                if(i_tmp_val>0){
                    r_tmp[i_in] = i_tmp_val;
                    i_tmp_val = 0;
                    i_in++;
                }

                i_tmp_val = c_t;
            }

            r_tmp[i_in] = i_tmp_val;
            i_tmp_val = 0;
            i_in++;
        }

        // 缓存出栈
        if(i_tmp_val>0){
            r_tmp[i_in] = i_tmp_val;
            i_tmp_val = 0;
            i_in++;
        }

        if(i_in+1 == r1.length){
            return r_tmp;
        }
        //return Arrays.stream(r_tmp).limit(i_in+1).toArray();
        return Arrays.copyOf(r_tmp, i_in+1);
    }

    private static int addStr(int num, char str){
        String tmp = num + "" + Character.getNumericValue(str);
        return Integer.valueOf(tmp);
    }

    public static String readFile(File file, Long reLen, String charsetName){
        StringBuffer re = new StringBuffer();
        BufferedReader br = null;
        Long dataLen = 0L;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), charsetName));
            String buff = null;
            while ((buff = br.readLine()) != null) {
                re.append(buff);
                re.append("\r\n");
                dataLen++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        reLen = dataLen;
        return re.toString();
    }
}
